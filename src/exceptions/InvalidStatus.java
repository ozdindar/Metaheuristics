package exceptions;

/**
 * Created by dindar.oz on 01.06.2015.
 */
public class InvalidStatus extends RuntimeException {
    public InvalidStatus(String s) {
        super(s);
    }
}
