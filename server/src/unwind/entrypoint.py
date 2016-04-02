#!/usr/bin/env python2.7

from datetime import datetime
from rest_api import routing
from rest_api.manager import RESTAPIManager
from malibu.util import log
from malibu.util.scheduler import Scheduler

from unwind import migrations
from unwind import models


def main():

    manager = RESTAPIManager()

    scheduler = Scheduler()
    scheduler.save_state("default")

    manager.load_logging()
    log.LoggingDriver.from_config(manager.config.get_section("logging"),
                                  name="unwind")
    logger = log.LoggingDriver.find_logger(name="unwind.__main__")
    models.init_database_from_config(manager.config.get_section("database"))
    manager.load_bottle()
    routing.load_routing_modules(manager, package="unwind.routes")
    manager.load_dsn()

    if "migrate" in manager.arg_parser.options:
        migrations.load_migrations()
        mig_do = manager.arg_parser.options["migrate"]
        if "migrate-script" in manager.arg_parser.options:
            migration_idx = int(manager.arg_parser.options["migrate-script"])
            if mig_do == "upgrade":
                migrations.migrate_single(
                    models.database_migrator,
                    "upgrade",
                    migration_idx)
            elif mig_do == "downgrade":
                migrations.migrate_single(
                    models.database_migrator,
                    "downgrade",
                    migration_idx)
            else:
                log.error("Invalid migration action! Only upgrade or downgrade"
                          " are allowed.")
                exit(1)
            exit(0)
        else:
            if mig_do == "upgrade":
                migrations.migrate_upgrades(models.database_migrator)
            elif mig_do == "downgrade":
                migrations.migrate_downgrades(models.database_migrator)
            else:
                log.error("Invalid migration action! Only upgrade or downgrade"
                          " are allowed.")
                exit(1)

    try:
        manager.run_bottle()
    except:
        manager.dsn.client.captureException()
        exit(1)
