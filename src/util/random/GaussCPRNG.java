package util.random;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public class GaussCPRNG extends CPRNG {

    private static final double a = 1.7;
    private static final double b = 0.5;


    public GaussCPRNG(){
        x = r.nextDouble();
        y = x;
    }

    @Override
    protected double f_y(double x, double y) {
        return x;
    }

    @Override
    protected double f_x(double x, double y) {
        if (x == 0)
            return 0;
        else return (1/x - Math.floor(1/x));
    }
}
