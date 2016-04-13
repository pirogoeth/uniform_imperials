
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class AddService extends AsyncTask<String, Void, Service> {
    private DeviceSync device;
    private DBAdapter adapter;
    private DBHandler db;
    private Exception exception;

    public AddService(DeviceSync device, DBHandler db, DBAdapter adapter) {
        this.device = device;
        this.adapter = adapter;
        this.db = db;
    }

    @Override
    protected Service doInBackground(String) {
        try {
            return device.addDevice(strings[0]);
        } catch (Exception e) {
            Log.e("insert msg", e.getMessage());
            exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Service service) {
        if (service != null) {

        } else {
            String message = exception.getMessage();
            Toast.makeText(device.getContext(), variable, Toast.LENGTH_SHORT);
        }
    }
}
