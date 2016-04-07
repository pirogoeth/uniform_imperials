# -*- coding: utf-8 -*-

import json

from bottle import request
from malibu.util import log
from rest_api import routing
from rest_api.routing.base import api_route

from unwind.models.device import Device
from unwind.models.channel import Channel


@routing.routing_module
class DeviceAPIRouter(routing.base.APIRouter):
    """ Routes for device-specific actions.

        GET /device/:uuid
        POST /device/register
        PUT /device/:uuid
        DELETE /device/:uuid
        GET /device/:uuid/ping
    """

    def __init__(self, manager):

        routing.base.APIRouter.__init__(self, manager)

        self.__log = log.LoggingDriver.find_logger()

    @api_route(path="/device/<uuid>",
               actions=["GET"],
               returns="application/json")
    def device_info_get():
        """ GET /device/:uuid

        """

        pass
