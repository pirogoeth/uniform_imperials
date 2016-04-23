
import java.util.Date;
import java.util.TimeZone;

public class Message {
    private String header;
    private String message;
    private Service service;
    private int decible;
    private Date timestamp;
    private String uri;
    private int id = -1;

    public Message(Service service, String message, String header) {
        this(service, message, header);
    }

    public Message(Service service, String message, String header, int timestamp) {
        this(service, message, header, new Date((long) timestamp ));
    }

    public Message(Service service, String message, String header, Date timestamp) {
        this.service = service;
        this.message = message;
        this.header = header;
        this.timestamp = timestamp;
        this.decible = 0;
        this.uri = "";
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getDecible() {

        return decible;
    }

    public void setDecible(int decible) {

        this.decible = decible;
    }

    public Service getService() {

        return service;
    }

    public void setService(Service service) {

        this.service = service;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public String getHeader() {

        return header;
    }

    public void setHeader(String header) {

        this.header = header;
    }

    public Date getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(Date timestamp) {

        this.timestamp = timestamp;
    }

    public Date getLocalTimestamp() {
        return new Date(timestamp.getTime() + TimeZone.getDefault().getOffset(System.currentTimeMillis()));
    }

    public String getHeaderOrName() {
        String name = header;
        if (name.equals(""))
            name = service.getName();
        return name;
    }

    public String toString() {

        return getHeaderOrName() + " " + getMessage();
    }

    public String getUri() {

        return uri;
    }

    public void setUri(String uri) {

        this.uri = uri;
    }

    public boolean hasLink() {

        return !this.uri.equals("");
    }
}
