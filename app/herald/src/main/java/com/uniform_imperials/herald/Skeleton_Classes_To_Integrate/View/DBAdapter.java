

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;


public class DBAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Service> entries = new ArrayList<Service>();

    public DBAdapter(Context context) {

    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int i) {
        return entries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    }

    public void addEntries(ArrayList<Service> entries) {

    }

    public void addEntry(Service entry) {

    }

    public void upDateEntries(ArrayList<Service> entries) {

    }
}