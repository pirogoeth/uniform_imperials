
import android.os.AsyncTask;


public class UpdateService extends AsyncTask<Void, Void, Service[]> {
    private DatabaseHandler db;
    private DeviceSync api;
    private UpdateServiceCallback callback;

    public UpdateService(DeviceSync api, DatabaseHandler db) {
        this.api = api;
        this.db = db;
        this.callback = null;
    }

    public void setCallback(UpdateServiceCallback callback) {
        this.callback = callback;
    }


    @Override
    protected Service[] doInBackground(Void... voids) {
        try {
            Service[] subscription = this.api.listDevices();
            db.refreshServices(subscription);
            return subscription;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Service[0];
    }

    @Override
    protected void onPostExecute(Service[] services) {
        super.onPostExecute(services);
        for (Service service : services)
            new DownloadServiceLogoAsync(api.getContext()).execute(service);
        if (callback != null)
            callback.onComplete(services);
    }
}
