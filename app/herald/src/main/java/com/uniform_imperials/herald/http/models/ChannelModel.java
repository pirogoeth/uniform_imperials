package com.uniform_imperials.herald.http.models;

import com.uniform_imperials.herald.http.AbstractHttpRequest;
import com.uniform_imperials.herald.http.AbstractHttpResponse;
import com.uniform_imperials.herald.http.HttpClient;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Sean Johnson on 4/25/2016.
 *
 * Performs actions on the Channel routes exposed by the API.
 */
public class ChannelModel {

    //// JSON Request Classes

    public static class CreateChannelJson extends AbstractHttpRequest<CreateChannelJson> {
        String signature;               // initial channel signature --
                                        // created with a HMAC-SHA1 signature with the encryption
                                        // key as the key and the channel uuid as the data.

        @Override
        public String encode(CreateChannelJson o) {
            return this.encode(CreateChannelJson.class, o);
        }
    }

    /**
     * Request to /channel/:identifier/push
     */
    public static class ChannelPushReqJson extends AbstractHttpRequest<ChannelPushReqJson> {
        ChannelPushReqMetadataJson metadata;
        byte[] payload;                 // aes256-encrypted JSON payload
        byte[] signature;               // salted HMAC-SHA1 payload signature

        @Override
        public String encode(ChannelPushReqJson o) {
            return this.encode(ChannelPushReqJson.class, o);
        }
    }

    /**
     * Metadata subkey of the channel push json class.
     */
    public static class ChannelPushReqMetadataJson {
        long received_at;               // <unix ts> time notification was originally received
        long pushed_at;                 // <unix ts> time notification was pushed to backend
        String from_id;                 // device uuid that pushed notification
        List<String> to_id;             // list of device uuids notifications are forwarded to
    }

    //// JSON Response Classes

    /**
     * Base response from POST /channel/create
     */
    public static class ChannelJson extends AbstractHttpResponse<ChannelJson> {
        String uuid;                    // string uuid
        String alias;                   // alias generated name mapping of uuid
        ChannelStatsJson stats = null;  // stats object

        @Override
        public ChannelJson decode(String jsonString) {
            return this.decode(ChannelJson.class, jsonString);
        }
    }

    /**
     * Response from GET /channel/:identifier
     */
    public static class ChannelStatsJson {
        int members;                    // member count
        int messages;                   // message count
    }

    /**
     * Response from POST /channel/:identifier/push
     */
    public static class ChannelPushRespJson extends AbstractHttpResponse<ChannelPushRespJson> {
        long timestamp;                 // unix timestamp
        String status;                  // string (accepted|rejected)
        String message;                 // return message
        String job_id;                  // push uuid

        @Override
        public ChannelPushRespJson decode(String jsonString) {
            return this.decode(ChannelPushRespJson.class, jsonString);
        }
    }

    /**
     * Response from GET /channel/:identifier/devices
     */
    public static class ChannelDevicesJson extends AbstractHttpResponse<ChannelDevicesJson> {
        String channel;                 // channel uuid
        List<String> devices;           // list of device uuids

        @Override
        public ChannelDevicesJson decode(String jsonString) {
            return this.decode(ChannelDevicesJson.class, jsonString);
        }
    }

    /**
     * Status response from DELETE /channel/:identifier
     */
    public static class ChannelStatusJson extends AbstractHttpResponse<ChannelStatusJson> {
        String channel;                 // channel uuid
        String status;                  // channel status

        @Override
        public ChannelStatusJson decode(String jsonString) {
            return this.decode(ChannelStatusJson.class, jsonString);
        }
    }

    public static ChannelJson createChannel(HttpClient hbc) throws ExecutionException {
        hbc.setDecodingTarget(ChannelJson.class);

        Future<AbstractHttpResponse> fhr = hbc.post("/channel/create");

        try {
            return (ChannelJson) fhr.get();
        } catch (InterruptedException exc) {
            // Uhh, what? Execution was interrupted, I guess.
        } catch (ExecutionException exc) {
            // Execution failed?
        }

        return null;
    }
}
