# Push
### Or, how notifications are pushed between devices.


### Device Flow
 1. Device receives a notification.
 2. Application utilizes a global notification listener to capture notification details (application icon, source intent, message, title, metadata, *et cetera*.)
 3. Notification metadata is structured into a JSON payload:
    ```
    {
    	"icon": "base64; ... image data ...",
    	"source": "<application_package_handle>",
    	"data": {
        	"title": "...",
        	"message": "...",
         },
         "metadata": {
         	...
         }
    }
    ```


### Device Flow (cont.)
 4. Payload is combined with the user's encryption key and hashed with HMAC-SHA1 algorithm.
 5. A wrapped payload is generated:
    ```
    {
    	"deviceId": "<uuid>",
    	"timestamp": <unix_ts>,
    	"signature": "<HMAC-SHA1 hash>",
    	"payload": "<encrypted payload>"
    }
    ```
 6. Payload is pushed up to the designated endpoint URL for the dispatcher server via `HTTP POST`.


### Dispatcher Flow
 1. Dispatcher verifies the registration status of the device id, validates the timestamp.
 2. Dispatched POSTs the payload to the GCM ([Google Cloud Messaging](https://developers.google.com/cloud-messaging/gcm)) API with the device's GCM target ID:
    ```
    {
    	"to": "<gcm_device_id>",
    	"data": "<wrapped payload>"
    }
    ```


### Receiver Flow
 1. Target device(s) receive the message, verify the metadata, attempt to decrypt the payload with the *shared* encryption key, and check the signature of the payload.
 2. *Notification is displayed*!
