package problems.movingpeaks;


public class Peak_Function_Sphere implements Peak_Function {

    public double calculate(double[] gen, int peak_number) {


        int j;
        double dummy;

        dummy =	(gen[0] - MovingPeaks.peak[peak_number][0]) * (gen[0] - MovingPeaks.peak[peak_number][0]);
        for (j = 1; j < MP_Scenario.geno_size; j++)
            dummy += (gen[j] - MovingPeaks.peak[peak_number][j])
                    * (gen[j] - MovingPeaks.peak[peak_number][j]);

        return MovingPeaks.peak[peak_number][MP_Scenario.geno_size + 1] - dummy;

    }

}