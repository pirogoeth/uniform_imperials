import json

from bottle import request
from malibu.util import log, scheduler

from rest_api import routing
from rest_api.routing.base import api_route

from unwind.util import token_by_header_data


@routing.routing_module
class SystemAPIRouter(routing.base.APIRouter):
    """ Routing for Unwind-specific system statuses.
    """

    def __init__(self, manager):

        routing.base.APIRouter.__init__(self, manager)

        self.__log = log.LoggingDriver.find_logger()

    @api_route(path="/_health",
               actions=["GET"],
               returns="application/json")
    def health():
        """ GET /_health

            Returns API health information.
        """

        resp = routing.base.generate_bare_response()
        resp["health"] = "okay"
        resp["scheduler"] = {
            "jobs": len(scheduler.Scheduler(state="default")
                        .job_store.get_jobs())
        }

        return json.dumps(resp) + "\n"

    @api_route(path="/_scheduler/jobs",
               actions=["GET"],
               returns="application/json")
    def scheduler_jobs():
        """ GET /_scheduler/jobs

            System status function.

            Returns a JSON encoded list of jobs in the Scheduler
            job store.
        """

        auth_token = request.headers.get("X-Unwind-Session")
        token = token_by_header_data(auth_token)

        if not token:
            resp = routing.base.generate_error_response(code=409)
            resp["message"] = "Invalid authentication token."
            return json.dumps(resp) + "\n"

        sch = scheduler.Scheduler(state="default")
        jobs = sch.job_store.get_jobs()

        resp = routing.base.generate_bare_response()
        resp["jobs"] = []

        for job in jobs:
            resp["jobs"].append({
                "name": job.get_name(),
                "function_handle": str(job._function),
                "delta": str(job._delta),
                "recurring": job.is_recurring(),
                "metadata": job.metadata,
                "eta": str(job.get_eta()),
                "num_onfail": len(job._onfail),
            })

        return json.dumps(resp) + "\n"
