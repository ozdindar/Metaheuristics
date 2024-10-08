package problems.movingpeaks;

/**
 * Moving Peaks Function --- 10/99
 *
 * Copyright (C) 1999 Juergen Branke.
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License.
 *
 * This module is an example of how to use the Moving Peaks Evaluation
 * Function, a dynamic benchmark problem changing over time.
 *
 *
 *
 */

public class Peak_Function_Twin implements Peak_Function {

    public double calculate(double[] gen, int peak_number) {

        int j;
        double maximum = -100000.0, dummy;
		/* difference to first peak */
		/*static*/
        double[] twin_peak = { 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0 };

        dummy = Math.pow(gen[0] - MovingPeaks.peak[peak_number][0], 2);
        for (j = 1; j < MP_Scenario.geno_size; j++)
            dummy += Math.pow(gen[j] - MovingPeaks.peak[peak_number][j], 2);
        dummy =
                MovingPeaks.peak[peak_number][MP_Scenario.geno_size
                        + 1]
                        - (MovingPeaks.peak[peak_number][MP_Scenario.geno_size] * dummy);
        maximum = dummy;
        //System.out.println("j: "+j);
        dummy =
                Math.pow(gen[j] - (MovingPeaks.peak[peak_number][0] + twin_peak[0]), 2);
        for (j = 1; j < MP_Scenario.geno_size; j++)
            dummy
                    += Math.pow(
                    gen[j] - (MovingPeaks.peak[peak_number][j] + twin_peak[0]),
                    2);
        dummy =
                MovingPeaks.peak[peak_number][MP_Scenario.geno_size
                        + 1]
                        + twin_peak[MP_Scenario.geno_size
                        + 1]
                        - ((MovingPeaks.peak[peak_number][MP_Scenario.geno_size]
                        + twin_peak[MP_Scenario.geno_size])
                        * dummy);
        if (dummy > maximum)
            maximum = dummy;

        return maximum;

    }

}