package util.random;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public class SinaiCPRNG extends CPRNG {

    private static final double a = 0.75;

    public SinaiCPRNG(){
        x = r.nextDouble();
        y = r.nextDouble();

    }

    @Override
    protected double f_y(double x, double y) {
        return (x+2*y)%1;
    }

    @Override
    protected double f_x(double x, double y) {
        return (x + y + a*Math.cos(2*Math.PI*y))%1;
    }
}
