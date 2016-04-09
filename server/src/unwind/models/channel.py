# -*- coding: utf-8 -*-

import datetime
import peewee

from uuid import uuid4

from malibu.util import names

from unwind.models import BaseModel

# Pull external wordlists into the names module
if names:
    try:
        names.load_external_words(source="default")
        names.load_external_words(source="gfycat")
    except:
        raise Exception("Could not import external word lists")

generate_channel_name = lambda: names.get_complex_name(num_adjs=2, num_nouns=1)
generate_uuid = lambda: str(uuid4())


class Channel(BaseModel):

    creation_time = peewee.DateTimeField(
        default=datetime.datetime.now,
        null=False)
    friendly_name = peewee.CharField(
        default=generate_channel_name,
        index=True,
        unique=True,
        null=False)
    uuid = peewee.UUIDField(
        default=generate_uuid,
        index=True,
        unique=True,
        primary_key=True,
        null=False)
    signature = peewee.BlobField(
        index=True,
        unique=True,
        null=False)

    # Statistic fields
    message_count = peewee.IntegerField(
        default=0,
        null=False)

    class Meta:
        order_by = ('friendly_name', 'uuid', 'creation_time',)

    @staticmethod
    def uuid_by_alias(channel_alias):
        """ Returns a channel UUID given an alias.

            :param str channel_alias: Alias of channel to look up.
            :rtype: str
            :returns: UUID string representing our channel
        """

        try:
            c = Channel.get(friendly_name=channel_alias)
            if not c:
                return None

            return str(c.uuid)
        except:
            return None
