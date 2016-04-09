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
    friendly_name = peewee.CharField()
    signature = peewee.BlobField(
        null=False,
        unique=True,
        index=True)

    class Meta:
        order_by = ('uuid', 'last_ping',)

    @staticmethod
    def by_channel_uuid(channel_uuid=None):
        """ Returns a list of the device members that are a part of the
            channel identified by channel_uuid.

            :param str channel_uuid: UUID of the channel to find
            :rtype: list
            :returns: List of Device objects.
        """

        if channel_uuid is None:
            raise ValueError("channel_uuid can not be null")

        return (Device
                .select()
                .join(chan.Channel)
                .where(chan.Channel.uuid == channel_uuid))

    @staticmethod
    def by_channel_alias(channel_alias=None):
        """ Returns a list of the device members that are a part of the
            channel identified by channel_alias.

            :param str channel_alias: Alias of the channel to find
            :rtype: list
            :returns: List of Device objects.
        """

        if channel_alias is None:
            raise ValueError("channel_alias can not be null")

        return (Device
                .select()
                .where(Device.channel.friendly_name == channel_alias))
