# -*- coding: utf-8 -*-

import datetime
import peewee

from gcm import GCM

from malibu.config import configuration
from malibu.util.log import LoggingDriver

from rest_api import manager

from uuid import uuid4

from unwind.models import BaseModel
from unwind.models import channel as chan

LOG = LoggingDriver.find_logger()


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
    target_id = peewee.CharField(
        null=True,
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

    def push_payload(payload):
        """ Pushes a payload through the proper broker to the current device
            instance.

            Payload should be structured like:

                {
                    "metadata": {
                        "received_at": <unix ts>,
                        "pushed_at": <unix ts>,
                        "from_id": "<device uuid>"
                    },
                    "payload": "<aes256 salted, encrypted JSON payload>",
                    "signature": "<salted HMAC-SHA1 payload>"
                }

            Supported brokers:
              - Google Cloud Messaging (through `python-gcm`)

            :param dict payload: Dictionary of push payload.
            :rtype: None
            :returns: None
        """

        if not self.target_id:
            raise ValueError("No target id has been set")

        config = manager.get_instance().config
        gcm_conf = config.get_section('gcm')

        g = None
        try:
            g = GCM(gcm_conf.get_string('api_key', None))
        except (Exception e):
            raise e

        resp = g.json_request(
            registration_ids=self.target_id,
            data=payload,
            delay_while_idle=True,
            time_to_live=3600
        )

        if resp and 'success' in resp:
            for devid, succid in resp['success'].items():
                LOG.info('Pushed payload to device %s' % (devid))

        if 'errors' in resp:
            for err, devids in resp['errors'].items():
                if err in ['NotRegistered', 'InvalidRegistration']:
                    for devid in devids:
                        print('Invalid device id: %s' % (devid))

        if 'canonical' in resp:
            for devid, canonical_id in resp['canonical'].items():
                d = Device.get(target_id=devid)
                d.target_id = canonical_id
                d.save()
