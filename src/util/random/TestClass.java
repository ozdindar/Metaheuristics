package util.random;

public class TestClass {
    public static void main(String[] args) throws Exception {

        int time = 0, iterations = 50000;
        double x = -0.72, y = -0.64;
        double a = 0.9, b = -0.6013, c = 2.0, d = 0.5;
        while (time < iterations) {
            double oldX = x;
            x = Math.pow(x,2)-Math.pow(y,2)+a*x+b*y;
            y = 2*oldX*y+c*oldX+d*y;
            System.out.println(x+" "+y+"\n"); //writing data to a txt file to be read by Mathematica
            time++;
        }
    }
}