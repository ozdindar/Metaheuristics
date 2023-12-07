package util.random;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public class ArnoldCatCPRNG extends CPRNG {

    private static final double a = 1.7;
    private static final double b = 0.5;


    public ArnoldCatCPRNG(){
        x = r.nextDouble();
        y = r.nextDouble();
    }

    @Override
    protected double f_y(double x, double y) {
        return (x+2*y)%1;
    }

    @Override
    protected double f_x(double x, double y) {
        return (x+y)%1;
    }
}
