import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;

public class DBHandler extends SQLiteOpenHelper {

    public DBHandler(Context context) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }



    public Message[] getAllMessages() {

    }

    public void addMessage(Message msg) {


    }

   public void addService(Service service) {

       addServices(new Service[]{service});
    }

    public void removeService(Service service) {

    }

    public void deleteMessage(Message message) {

    }

    public Message getMessage(int id) throws Exception {

    }

    public void addServices(Service[] services) {

    }

    public int getServiceCount() {

    }

    public Service getService(String token) {

    }

    public void refreshServices(Service[] services) {

    }

    public boolean isListening(Service service) {
        return this.isListening(service.getToken());
    }

    public boolean isListening(String service) {

    }

    public Service[] getAllServices() {

    }

    public void truncateMessages() {

    }

    public void truncateServices() {

    }

    private Message getMessageFromRow(Cursor rowMsg) {

    }
}
