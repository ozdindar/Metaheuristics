package problems.bbob.f20_schwefel;

public class SchwefelBBOB {

    private static final double FOPT = 0.0; // Replace with actual f_opt value
    private static final double CONSTANT = 4.189828872724339;
    private static final double X_OPT = 4.2096874633 / 2;

    public static double schwefel(double[] x) {
        int D = x.length;
        double[] z = transform(x);
        double sum = 0.0;

        for (int i = 0; i < D; i++) {
            sum += z[i] * Math.sin(Math.sqrt(Math.abs(z[i])));
        }

        double penalty = penalty(z, 100.0);
        return -sum / (100.0 * D) + CONSTANT + 100.0 * penalty + FOPT;
    }

    private static double[] transform(double[] x) {
        int D = x.length;
        double[] xHat = new double[D];
        double[] zHat = new double[D];
        double[] z = new double[D];

        // Step 1: xHat = 2 * alternatingSignVector * x
        for (int i = 0; i < D; i++) {
            int sign = (i % 2 == 0) ? -1 : 1;
            xHat[i] = 2.0 * sign * x[i];
        }

        // Step 2: zHat transformation
        zHat[0] = xHat[0];
        for (int i = 1; i < D; i++) {
            zHat[i] = xHat[i] + 0.25 * (xHat[i - 1] - 2.0 * Math.abs(X_OPT));
        }

        // Step 3: Apply Lambda^10 scaling and shift
        for (int i = 0; i < D; i++) {
            double exponent = (double) i / (D - 1);
            double lambda = Math.pow(10.0, exponent);
            z[i] = 100.0 * lambda * (zHat[i] - 2.0 * Math.abs(X_OPT));
        }

        return z;
    }

    private static double penalty(double[] z, double bound) {
        double sum = 0.0;
        for (double zi : z) {
            if (Math.abs(zi) > bound) {
                sum += Math.pow(Math.abs(zi) - bound, 2);
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        double[] x = new double[] {420.9687, 420.9687}; // Example input
        System.out.println("Schwefel BBOB value: " + schwefel(x));
    }
}
