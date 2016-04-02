import glob
import os
import re

from importlib import import_module
from unwind.models.meta import MigrateMeta

from malibu.util import log
from malibu.util.decorators import function_marker, function_registrator


modules = glob.glob(os.path.dirname(__file__) + "/*.py")
__all__ = [os.path.basename(f)[:-3] for f in modules
           if not os.path.basename(f).startswith('_') and
           not f.endswith('__init__.py') and os.path.isfile(f)]


__UPGRADE__ = []
__DOWNGRADE__ = []

MIGRATION_ORDERING_RGX = re.compile(r"^(?P<order>[\d]+)(?:.*)$")

""" upgrade and downgrade are function decorators that designate a migration

    function as a database upgrader or a database downgrader. A migration
    function should take one argument, which would be an instance of
    peewee's playhouse.migrate.SchemaMigrator.
"""
__upgrade_reg = function_registrator(__UPGRADE__)
__downgrade_reg = function_registrator(__DOWNGRADE__)
__upgrade_mark = function_marker("mig_type", "upgrade")
__downgrade_mark = function_marker("mig_type", "downgrade")


def upgrade(func):
    __upgrade_reg(func)
    __upgrade_mark(func)

    return func


def downgrade(func):
    __downgrade_reg(func)
    __downgrade_mark(func)

    return func


def load_migrations():
    """ Loads all migration modules.
        Migration functions should be defined with @upgrade and @downgrade.
        Only labelled functions will be executed with the specified migration
        case!

        Migration functions will be loaded into __UPGRADE__ or __DOWNGRADE__
        depending on how they are marked. Because of the way migrations are
        marked, stored, and loaded, this function doesn't return a list of
        migration functions, but returns a list of migration modules.
    """

    LOG = log.LoggingDriver.find_logger()
    migrations = []

    for migration in __all__:
        LOG.info("Checking for migrations in {}.{}".format(
            __package__, migration))

        module = import_module("{}.{}".format(__package__, migration))
        order_match = MIGRATION_ORDERING_RGX.match(migration)

        if not order_match:
            LOG.warning("No leading migration number found in '%s', skipping"
                        % (migration))
            continue

        mig_num = order_match.groups(
            order_match.groupdict({"order": None})["order"])[0]

        if not mig_num:
            LOG.warning("No leading migration number found in '%s', skipping"
                        % (migration))
            continue

        migrations.append(module)

        mig_num = int(mig_num)
        for obj_n in dir(module):
            obj = getattr(module, obj_n)
            if hasattr(obj, "mig_type"):
                # This function is a migration. Tag it!
                obj.mig_order = mig_num
                LOG.info("%s migration (%s) loaded from %s (order: %s)"
                         % (obj.mig_type, obj.__name__, module.__name__,
                            mig_num))
            else:
                continue

    return migrations


def run_migrations(migrator, migrations, delete_meta=False):

    LOG = log.LoggingDriver.find_logger()

    ordered_migrations = sorted(
        migrations,
        key=lambda item: item.mig_order)

    for mig in ordered_migrations:
        try:
            mig_meta = MigrateMeta.get(
                MigrateMeta.upgrade_number == mig.mig_order)
        except:
            mig_meta = None

        if delete_meta and mig_meta:
            try:
                mig_meta.delete()
            except:
                LOG.info("Could not delete migration meta: %s %s"
                         % (mig.__name__, mig.mig_order))
        elif not delete_meta and not mig_meta:
            mig_meta = MigrateMeta.create(
                upgrade_number=mig.mig_order)
            mig_meta.save()
        elif not delete_meta and mig_meta:
            LOG.info("Can't perform migration, meta for migration %s "
                     "already exists (migration has already been applied)"
                     % (mig.mig_order))
            continue
        elif delete_meta and not mig_meta:
            LOG.info("Not going to perform downgrade migration, migration "
                     "meta does not exist: %s" % (mig.mig_order))
            continue

        try:
            mig(migrator)
            LOG.info("Finished migration: %s (num: %s)"
                     % (mig.__name__, mig.mig_order))
        except Exception:
            LOG.error("An error occured while running a migration (order: %s)"
                      % (mig.mig_order))
            raise


def migrate_single(migrator, migrate_action, migrate_num):
    """ Runs a single migration specified by migrate_num.
        migrate_action should be a string, either "upgrade" or
        "downgrade".
    """

    LOG = log.LoggingDriver.find_logger()

    mig_list = []
    if migrate_action == "upgrade":
        mig_list = __UPGRADE__
    elif migrate_action == "downgrade":
        mig_list = __DOWNGRADE__
    else:
        LOG.error("Invalid migration action! Only 'upgrade' and 'downgrade' "
                  "are allowed.")
        return False

    migrations = filter(
        lambda item: item.mig_order == migrate_num,
        mig_list)

    if len(migrations) == 0:
        LOG.error("No migrations found for migration number %s"
                  % (migrate_num))
        return False

    try:
        mig_meta = MigrateMeta.get(
            MigrateMeta.upgrade_number == migrate_num)
    except:
        mig_meta = None

    if migrate_action == "downgrade" and mig_meta:
        try:
            mig_meta.delete()
        except:
            LOG.info("Could not delete migration meta: %s %s"
                     % (mig_meta.__name__, migrate_num))
    elif migrate_action == "upgrade" and not mig_meta:
        mig_meta = MigrateMeta.create(
            upgrade_number=migrate_num)
        mig_meta.save()
    elif migrate_action == "upgrade" and mig_meta:
        LOG.info("Can't perform migration, meta for migration %s "
                 "already exists (migration has already been applied)"
                 % (migrate_num))
        return False
    elif migrate_action == "downgrade" and not mig_meta:
        LOG.info("Not going to perform downgrade migration, migration "
                 "meta does not exist: %s" % (migrate_num))
        return False

    for mig in migrations:
        try:
            mig(migrator)
            LOG.info("Finished migration: %s (num: %s)"
                     % (mig.__name__, mig.mig_order))
        except Exception:
            LOG.error("An error occured while running a migration (order: %s)"
                      % (mig.mig_order))
            raise

    return True


migrate_upgrades = lambda migrator: run_migrations(
    migrator, __UPGRADE__, delete_meta=False)
migrate_downgrades = lambda migrator: run_migrations(
    migrator, __DOWNGRADE__, delete_meta=True)
