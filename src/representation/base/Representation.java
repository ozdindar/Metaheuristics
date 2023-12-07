package representation.base;

/**
 * Created by dindar.oz on 02.06.2015.
 */
public interface Representation {

    public Representation clone();
    boolean equals(Object o);
    int hashCode();

    double distanceTo(Representation r);
}
