package representation.base;

/**
 * Created by dindar.oz on 22.04.2015.
 */
public interface Individual {
    public double getCost();
    public Representation getRepresentation();

    public Individual clone();

    public void update(Representation rep,double cost);
}
