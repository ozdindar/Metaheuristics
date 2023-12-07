package util.random;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public class ChaosRNG extends AbstractRNG {

    private CPRNG cprng;

    public ChaosRNG(CPRNG cprng) {
        this.cprng = cprng;
    }

    @Override
    public int randInt(int max) {
        return (int) (max*cprng.getNext());
    }

    @Override
    public double randDouble() {
        return cprng.getNext();
    }
}
