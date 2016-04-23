
import android.content.Context;
import org.json.JSONObject;
import java.util.Date;

public class DeviceSync {
    private String url;
    private int lastCheck = 0;
    private String uuid;
    private Context context;

    public DeviceSync(Context context) {

    }

    public DeviceSync(Context context, String url) {
        this.url = url;
        this.context = context;
        this.uuid = (new DeviceUuid(context)).getDeviceUuid().toString();
    }

    public Context getContext() {

        return this.context;
    }

    public String getUuid() {

        return this.uuid;
    }

    public Date getLastCheck() {

        return new Date();
    }

    public Service addDevice(String service) throws Exception {

    }

    public void deleteDevice(String service) throws Exception {

    }

    public Service[] listDevices() throws Exception {

    }

    public void sendMessage(Message msg) throws Exception {

    }

    public Message[] getNewMessage() throws Exception {

    }


    private JSONObject GetHttp() throws Exception {

    }

    private JSONObject DeleteHttp() throws Exception {

    }
}