

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;



import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class Service {

    public Service(String token, String name) {


    }

    public Service(String token, String name, Date created) {

    }

    public Service(String token, String name, Date created) {

        this(token, name, created);
    }

    public Service(String token, String name, Date created) {
        this.token = token;
        this.name = name;
        this.created = created;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getToken() {

        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public Date getCreated() {

        return created;
    }

    public void setDate(Date created) {
        this.created = created;
    }


}
