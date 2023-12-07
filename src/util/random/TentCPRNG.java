package util.random;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public class TentCPRNG extends CPRNG {

    private static final double a = 1.7;
    private static final double b = 0.5;


    public TentCPRNG(){
        x = r.nextDouble();
    }

    @Override
    protected double f_y(double x, double y) {
        return x;
    }

    @Override
    protected double f_x(double x, double y) {
        if (x<0.7)
        {
            return x/0.7;
        }
        else return ((double) 10/(double) 3*x*(1-x));
    }
}
