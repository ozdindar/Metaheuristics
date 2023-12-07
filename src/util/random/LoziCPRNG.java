package util.random;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public class LoziCPRNG extends CPRNG {

    private static final double a = 1.7;
    private static final double b = 0.5;


    public LoziCPRNG(){
        x = r.nextDouble();
        y = x;
    }

    @Override
    protected double f_y(double x, double y) {
        return x;
    }

    @Override
    protected double f_x(double x, double y) {
        return 1-a*Math.abs(x)+ b*y;
    }
}
