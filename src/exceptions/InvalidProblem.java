package exceptions;

/**
 * Created by oz on 16.07.2015.
 */
public class InvalidProblem extends RuntimeException {
    public InvalidProblem(String s) {
        super(s);
    }
}
