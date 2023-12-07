package util.random;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public class TinkerbellCPRNG extends CPRNG {

    private static final double a = 0.9;
    private static final double b = -0.6023;
    private static final double c = 2;
    private static final double d = 0.5;


    public TinkerbellCPRNG(){
        x = -0.72;
        y = -0.64;
    }

    @Override
    protected double f_y(double x, double y) {
        return (2*x*y + c*x + d*y);
    }

    @Override
    protected double f_x(double x, double y) {
        return (x*x - y*y + a*x + b*y);
    }
}
