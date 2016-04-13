
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Date;

public class FirsTRun extends AsyncTask<Context, Void, Void> {

    @Override
    protected Void doInBackground(Context... params) {
        Context context = params[0];

        try {
            Resources resources = context.getResources();

            DeviceSync device = new DeviceSync(context, SettingsActivity.getRegisterUrl(context));
            DBHandler db = new DBHandler(context);

            Service service;
            String serviceToken = resources.getString(Resources.string.);
            try {
                service = device.addDevice(serviceToken);
            } catch (Exception e) {

                if (e.code != 4) {
                    throw e;
                } else {
                    service = new Service(serviceToken, "messgage", new Date());
                }
            }

            Message message = new Message(
                    service, resources.getString(Resources.string....),
                    resources.getString(Resources.string....), new Date()
            );

            db.addService(service);
            db.addMessage(message);

            context.sendBroadcast(new Intent("MessageUpdateS"));
            new UpdateService(device, db).execute();
        } catch (Exception e) {
            Toast.makeText(context, "Could not register : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return null;
    }
}
