package util.random;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public abstract class AbstractRNG implements RNG{


    public  boolean rollDice(double a) {
        return  (randDouble()<a);
    }

}
