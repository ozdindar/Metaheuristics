package util.random;

/**
 * Created by dindar.oz on 7.01.2016.
 */
public class DelayedLogisticCPRNG extends CPRNG {

    private static final double a = 4;// 2.27;



    public DelayedLogisticCPRNG(){
        x = 0.2027;
        y = 0;
    }

    @Override
    protected double f_y(double x, double y) {
        return x;
    }

    @Override
    protected double f_x(double x, double y) {
        return a*x*(1-x);
    }

    public static void main(String[] args) {
        CPRNG d = new DelayedLogisticCPRNG();

        for (int i=0;i<1000;i++)
        {
            System.out.println((int)(1000*d.getNext()));
        }
    }
}
