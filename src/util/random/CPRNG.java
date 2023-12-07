package util.random;

import java.security.SecureRandom;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public abstract class CPRNG {

    private int seed;

    double x;
    double y;

    protected SecureRandom r = new SecureRandom();



    public double getNext()
    {
        double cx =f_x(x,y);
        y =  f_y(x,y);
        x = cx;


        return Math.abs(x%1);
    }

    protected abstract double f_y(double x, double y);

    protected abstract double f_x(double x, double y);
}
