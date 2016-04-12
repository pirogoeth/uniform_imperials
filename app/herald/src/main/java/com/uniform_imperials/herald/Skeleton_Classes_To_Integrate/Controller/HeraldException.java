
public class HeraldException extends java.lang.Exception {
    public int code;

    public HeraldException(String message, int code) {
        super(message);
        this.code = code;
    }
}
