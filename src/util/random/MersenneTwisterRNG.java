package util.random;

import org.apache.commons.math3.random.MersenneTwister;

import java.security.SecureRandom;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public class MersenneTwisterRNG implements RNG {
    static SecureRandom r = new SecureRandom();
    static MersenneTwister mt = new MersenneTwister(r.nextInt());

    @Override
    public int randInt(int max) {
        return mt.nextInt(max);
    }

    @Override
    public double randDouble() {
        return mt.nextDouble();
    }
}
