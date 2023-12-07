package exceptions;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class InvalidIndividual extends RuntimeException {
    public InvalidIndividual(String s) {
        super(s);
    }
}
