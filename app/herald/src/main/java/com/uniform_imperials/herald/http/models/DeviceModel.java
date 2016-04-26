package com.uniform_imperials.herald.http.models;

import com.uniform_imperials.herald.http.AbstractHttpRequest;
import com.uniform_imperials.herald.http.AbstractHttpResponse;
import com.uniform_imperials.herald.http.HttpClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Sean Johnson on 4/26/2016.
 *
 * Provides interactions with the Devices API on Unwind.
 */
public class DeviceModel {

    /// JSON Request Classes

    /**
     * Request for POST /device/register and PUT /device/:identifier
     */
    public static class DeviceRegisterRequestJson extends AbstractHttpRequest<DeviceRegisterRequestJson> {
        String friendly_name;                   // Device friendly name
        DeviceTargetMetadataJson target;        // Target information
        byte[] signature;                       // HMAC-SHA1 signature of the device --
                                                // in device updates, used for verification.

        @Override
        public String encode(DeviceRegisterRequestJson o) {
            return this.encode(DeviceRegisterRequestJson.class, o);
        }
    }

    /**
     * Verified request to use with:
     * - DELETE /device/:identifier
     * - POST /device/:identifier/ping
     */
    public static class DeviceVerifiedRequestJson extends AbstractHttpRequest<DeviceVerifiedRequestJson> {
        byte[] signature;                       // Signature param for validating request.

        @Override
        public String encode(DeviceVerifiedRequestJson o) {
            return this.encode(DeviceVerifiedRequestJson.class, o);
        }
    }

    /// JSON Response Classes

    /**
     * Response from GET /device/:identifier
     */
    public static class DeviceInfoJson extends AbstractHttpResponse<DeviceInfoJson> {
        String id;                              // Device long uuid
        long registered_at;                     // Unix timestamp -- registration time
        long last_seen;                         // Unix timestamp -- last seen time
        String friendly_name;                   // "Friendly name" of the device
        DeviceTargetMetadataJson target;        // Device target information.
        byte[] signature;                       // HMAC-SHA1 signature of the device

        @Override
        public DeviceInfoJson decode(String jsonString) {
            return this.decode(DeviceInfoJson.class, jsonString);
        }
    }

    public static class DeviceTargetMetadataJson {
        String id;                              // Device target uuid
        String backend;                         // Device push backend <gcm|broker>
    }

    /**
     * Response from DELETE /device/:identifier
     */
    public static class DeviceRemoveResponseJson extends AbstractHttpResponse<DeviceRemoveResponseJson> {
        String id;                              // Long uuid of removed device
        String status;                          // Device status

        @Override
        public DeviceRemoveResponseJson decode(String jsonString) {
            return this.decode(DeviceRemoveResponseJson.class, jsonString);
        }
    }

    public static class DevicePingResponseJson extends AbstractHttpResponse<DevicePingResponseJson> {
        String id;                              // Resolved device id
        long ping_xmit;                         // Ping transmission time
        long ping_recv;                         // Ping received time
        DevicePingStatusJson status;            // Status of the pinged device

        @Override
        public DevicePingResponseJson decode(String jsonString) {
            return this.decode(DevicePingResponseJson.class, jsonString);
        }
    }

    public static class DevicePingStatusJson {
        String code;                            // Device status code
        String message;                         // Message describing code
    }

    public static DeviceInfoJson registerDevice(
            HttpClient hc,
            DeviceRegisterRequestJson dRegReq) throws ExecutionException {
        hc.setDecodingTarget(DeviceInfoJson.class);

        Future<AbstractHttpResponse> fhr = hc.post("/device/register", dRegReq);

        try {
            return (DeviceInfoJson) fhr.get();
        } catch (InterruptedException exc) {
            return null;
        } catch (ExecutionException exc) {
            throw exc;
        }
    }

}
