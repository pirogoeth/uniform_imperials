
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class DeviceUuid {

    public DeviceUuid(Context context) {

    }

    public UUID getDeviceUuid() {

        return uuid;
    }
}