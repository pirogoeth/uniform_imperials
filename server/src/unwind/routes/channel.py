import json
import uuid

from malibu.util import log

from rest_api import routing
from rest_api.routing.base import api_route

from unwind.models.channel import Channel
from unwind.models.device import Device


@routing.routing_module
class ChannelAPIRouter(routing.base.APIRouter):
    """ Routes for channel management actions.

        POST /channel/create
        GET /channel/:uuid-or-:alias/info
        POST /channel/:uuid-or-:alias/push
        GET /channel/:uuid-or-:alias/devices
        DELETE /channel/:uuid-or-:alias
    """

    def __init__(self, manager):

        routing.base.APIRouter.__init__(self, manager)

        self.__log = log.LoggingDriver.find_logger()

    @api_route(path="/channel/create",
               actions=["POST"],
               returns="application/json")
    def channel_create():
        """ POST /channel/create

            Creates a new channel and returns the UUID and alias.

            Return data:
                {
                  "uuid": "886313e1-3b8a-5372-9b90-0c9aee199e5d",
                  "alias": "amplified_scary_feynman"
                }
        """

        c = Channel.create()
        c.save()

        resp = routing.base.generate_bare_response()
        resp["uuid"] = c.uuid
        resp["alias"] = c.friendly_name

        return json.dumps(resp) + "\n"

    @api_route(path="/channel/<identifier>",
               actions=["GET"],
               returns="application/json")
    def channel_get_info(identifier):
        """ GET /channel/:identifier

            Returns information about a channel.

            Return data:
                {
                  "uuid": "886313e1-3b8a-5372-9b90-0c9aee199e5d",
                  "alias": "amplified_scary_feynman",
                  "stats": {
                    "members": 2,
                    "messages": 20
                  }
                }
        """

        try:
            identifier = str(uuid.UUID(identifier))
        except:
            identifier = Channel.uuid_by_alias(identifier)

        c = Channel.get(uuid=identifier)
        if not c:
            resp = routing.base.generate_error_response(code=404)
            resp["message"] = "Could not find channel: %s" % (identifier)
            return json.dumps(resp) + "\n"

        d = Device.by_channel_uuid(identifier)

        resp = routing.base.generate_bare_response()
        resp["channel"] = {
            "uuid": str(c.uuid),
            "alias": c.friendly_name,
            "stats": {
                "messages": c.message_count,
                "devices": len(d),
            },
        }
        return json.dumps(resp) + "\n"

    @api_route(path="/channel/<identifier>/push",
               actions=["POST"],
               returns="application/json")
    def channel_push_payload(identifier):
        """ POST /channel/:identifier/push

            Accepts a structured JSON payload and forwards it onward to
            the GCM servers for the available registered devices.

            TODO: Instead of directly forwarding notifications, we should
                  enqueue the push job with Huey and assign a job ID to
                  retrieve results later.

            Request data:
                {
                    "metadata": {
                        "received_at": <unix ts>,
                        "pushed_at": <unix ts>,
                        "from_id": "<device uuid>",
                        "to_id": [
                            "<device uuid>",
                            "<device uuid>",
                            ...
                        ],
                    },
                    "payload": "<aes256 encrypted JSON payload>",
                    "signature": "<salted HMAC-SHA payload signature>"
                }

            Return data:
                {
                    "timestamp": <unix ts>,
                    "status": "<accepted|rejected>",
                    "message": "<Error message|okay>"
                    "job_id": <long push id> -or- "<push uuid>"
                }
        """

        resp = routing.base.generate_error_response(code=501)
        resp["message"] = "Not yet implemented."

        return json.dumps(resp) + "\n"

    @api_route(path="/channel/<identifier>/devices",
               actions=["GET"],
               returns="application/json")
    def channel_get_devices(identifier):
        """ GET /channel/:identifier/devices

            Lists the device ids that are registered to this channel.

            Return data:
                {
                    "channel": "<uuid>",
                    "devices": [
                        "<device uuid>",
                        "<device uuid>",
                        ...
                    ]
                }
        """

        resp = routing.base.generate_error_response(code=501)
        resp["message"] = "Not yet implemented."

        return json.dumps(resp) + "\n"

    @api_route(path="/channel/<identifier>",
               actions=["DELETE"],
               returns="application/json")
    def channel_delete(identifier):
        """ DELETE /channel/:identifier

            Allows deletion of a channel.

            Return data:
                {
                    "channel": "<uuid>",
                    "status": "deleted"
                }
        """

        resp = routing.base.generate_error_response(code=501)
        resp["message"] = "Not yet implemented."

        return json.dumps(resp) + "\n"
