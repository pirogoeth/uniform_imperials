# -*- coding: utf-8 -*-

import datetime
import peewee

from uuid import uuid4

from malibu.util import names

from unwind.models import BaseModel

## Pull external wordlists into the names module
if names:
    try:
        names.load_external_words(source="default")
        names.load_external_words(source="gfycat")
    except:
        raise Exception("Could not import external word lists")

generate_channel_name = lambda: names.get_complex_name(num_adjs=2, num_nouns=1)


class Channel(BaseModel):

    creation_time = peewee.DateTimeField(
        default=datetime.datetime.now,
        null=False)
    simple_name = peewee.CharField(
        default=generate_channel_name,
        index=True,
        unique=True,
        null=False)
    uuid = peewee.UUIDField(
        default=uuid4,
        index=True,
        unique=True,
        null=False)

    class Meta:
        order_by = ('simple_name', 'uuid', 'creation_time',)
