package exceptions;

/**
 * Created by dindar.oz on 10.07.2015.
 */
public class InvalidParameters extends RuntimeException {
    public InvalidParameters(String s) {
        super(s);
    }
}
