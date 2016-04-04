# -*- coding: utf-8 -*-

import datetime
import peewee

from uuid import uuid4

from unwind.models import BaseModel
from unwind.models import channel as chan


class Device(BaseModel):

    registered_at = peewee.DateTimeField(
        default=datetime.datetime.now)
    last_ping = peewee.DateTimeField(
        default=datetime.datetime.now)
    uuid = peewee.UUIDField(
        default=uuid4)
    channel = peewee.ForeignKeyField(chan.Channel)

    class Meta:
        order_by = ('uuid', 'last_ping',)
