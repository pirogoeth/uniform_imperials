
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;


public class NotificationReceive extends AsyncTask<Void, Void, ArrayList<Message>> {
    private DeviceSync api;
    private ListAdapter adapter;
    private Exception error;
    private NotificationReceiveCallBack callback;

    public NotificationReceive(DeviceSync api, ListAdapter adapter) {
        this.api = api;
        this.adapter = adapter;
    }

    public void setCallBack(NotificationReceiveCallBack cb) {
        this.callback = cb;
    }

    @Override
    protected ArrayList<Message> doInBackground(Void... voids) {
        try {
            return new ArrayList<Message>(Arrays.asList(this.api.getNewMessage()));
        } catch (Exception e) {
            this.error = e;
            return new ArrayList<Message>();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Message> result) {
        DatabaseHandler dbh = new DatabaseHandler(this.api.getContext());
        for (Message msg : result)
            dbh.addMessage(msg);
        adapter.addEntries(result);
        if (this.callback != null) {
            this.callback.receivePush(result);
        }
    }
}
