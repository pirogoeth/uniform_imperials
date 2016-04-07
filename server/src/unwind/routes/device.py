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

        GET /device/:identifier
        POST /device/register
        PUT /device/:identifier
        DELETE /device/:identifier
        GET /device/:identifier/ping
    """

    def __init__(self, manager):

        routing.base.APIRouter.__init__(self, manager)

        self.__log = log.LoggingDriver.find_logger()

    @api_route(path="/device/<identifier>",
               actions=["GET"],
               returns="application/json")
    def device_info_get(identifier):
        """ GET /device/:identifier

            Returns device information.

            Return data:
                {
                    "id": "<device uuid>",
                    "registered_at": <unix ts>,
                    "last_seen": <unix ts>,
                    "friendly_name": "...",
                    "target": {
                        "id": "[target identifier]",
                        "backend": "[gcm|broker]"
                    },
                    "signature": "<HMAC-SHA1 device sig>"
                }
        """

        resp = routing.base.generate_error_response(code=501)
        resp["message"] = "Not yet implemented."

        return json.dumps(resp) + "\n"

    @api_route(path="/device/register",
               actions=["POST"],
               returns="application/json")
    def device_register():
        """ POST /device/register

            Registers a device to a channel.

            Request data:
                {
                    "friendly_name": "[optional]",
                    "target": {
                        "id": "[target identifier]",
                        "backend": "[gcm|broker]"
                    },
                    "signature": "<HMAC-SHA1 device sig>"
                }

            Return data:
                {
                    "id": "<device uuid>",
                    "registered_at": <unix ts>,
                    "last_seen": <unix ts>,
                    "friendly_name": "...",
                    "target": {
                        "id": "[target identifier]",
                        "backend": "[gcm|broker]"
                    }
                }

        """

        resp = routing.base.generate_error_response(code=501)
        resp["message"] = "Not yet implemented."

        return json.dumps(resp) + "\n"

    @api_route(path="/device/<identifier>",
               actions=["PUT"],
               returns="application/json")
    def device_info_update(identifier):
        """ PUT /device/:identifier

            Update a devices registration information.

            Request data:
                {
                    "friendly_name": "[optional]",
                    "target": {
                        "id": "[target identifier]",
                        "backend": "[gcm|broker]"
                    },
                    "signature": "<HMAC-SHA1 device sig>"
                }

            Return data:
                {
                    "id": "<device uuid>",
                    "registered_at": <unix ts>,
                    "friendly_name": "...",
                    "last_seen": <unix ts>,
                    "target": {
                        "id": "[target identifier]",
                        "backend": "[gcm|broker]"
                    }
                }
        """

        resp = routing.base.generate_error_response(code=501)
        resp["message"] = "Not yet implemented."

        return json.dumps(resp) + "\n"

    @api_route(path="/device/<uuid>",
               actions=["DELETE"],
               returns="application/json")
    def device_delete():
        """ DELETE /device/:uuid

            Removes a device from the registration system.

            Return data:
                {
                    "id": "<device uuid>",
                    "status": "deleted"
                }
        """

        resp = routing.base.generate_error_response(code=501)
        resp["message"] = "Not yet implemented."

        return json.dumps(resp) + "\n"

    @api_route(path="/device/<identifier>/ping",
               actions=["GET"],
               returns="application/json")
    def device_ping(identifier):
        """ GET /device/:uuid/ping

            Pings a user's device through the target communication module.
            Also updates the devices last seen property.

            Return data:
                {
                    "id": "<device uuid>",
                    "ping_recv": <unix ts>,
                    "ping_xmit": <unix ts>,
                    "status": {
                        "code": <int status code>,
                        "message": "<status message>"
                    }
                }

        """

        resp = routing.base.generate_error_response(code=501)
        resp["message"] = "Not yet implemented."

        return json.dumps(resp) + "\n"
