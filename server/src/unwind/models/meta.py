import datetime
import peewee

from unwind.models import BaseModel


class MigrateMeta(BaseModel):

    patch_time = peewee.DateTimeField(
        null = False,
        default = datetime.datetime.now)
    upgrade_number = peewee.IntegerField(
        index = True,
        unique = False,
        null = False)

    class Meta:
        order_by = ('upgrade_number',)

