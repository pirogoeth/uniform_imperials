

import android.os.AsyncTask;

public class DeleteService extends AsyncTask<Service, Void, Void> {
    private DBHandler db;
    private DeviceSync device;
    private Callback callback;

    public DeleteService(DeviceSync device, DBHandler db) {
        this.device = device;
        this.db = db;
        this.callback = null;
    }

    public void setCallback(Callback callback) {

        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Service... services) {
        for (Service service : services) {
            try {
                device.deleteDevice(service.getToken());
                db.removeService(service);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (callback != null)
            callback.onComplete();
    }
}
