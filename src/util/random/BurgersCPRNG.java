package util.random;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public class BurgersCPRNG extends CPRNG {

    private static final double a = 0.75;
    private static final double b = 1.75;


    public BurgersCPRNG(){
        x = -0.1;
        y = 0.1;
    }

    @Override
    protected double f_y(double x, double y) {
        return b*y + x*y;
    }

    @Override
    protected double f_x(double x, double y) {
        return a*x-y*y;
    }
}
