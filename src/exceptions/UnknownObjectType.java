package exceptions;

/**
 * Created by dindar.oz on 10.07.2015.
 */
public class UnknownObjectType extends RuntimeException {
    public UnknownObjectType(String s) {
        super(s);
    }
}
