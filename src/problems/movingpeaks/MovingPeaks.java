package problems.movingpeaks;


import base.DynamicOptimizationProblem;
import metaheuristic.pso.base.PSOProblem;
import representation.DoubleVector;
import representation.base.Representation;

import java.util.Random;


/**
 * Moving Peaks Function --- 10/99  Copyright (C) 1999 Juergen Branke. This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License. This module is an example of how to use the Moving Peaks Evaluation  Function, a dynamic benchmark problem changing over time.
 */


public class MovingPeaks implements DynamicOptimizationProblem,PSOProblem {

    MP_Scenario scenario;

    /*
 *  if set to 1, a static landscape (basis_function) is included in the fitness
 *  evaluation
 */
    protected boolean use_basis_function = false;

    /* saves computation time if not needed and set to 0 */
    private boolean calculate_average_error = true; //int calculate_average_error = 1;

    /* saves computation time if not needed and set to 0 */
    private boolean calculate_offline_performance = true;
    //int calculate_offline_performance = 1;

    /* saves computation time if not needed and set to 0 */
    private boolean calculate_right_peak = true; //int calculate_right_peak = 1;

    /***** END OF PARAMETER SECTION *****/

    //void change_peaks();   /* preliminary declaration of function change_peaks()*/
    private boolean recent_change = true; /* indicates that a change has just ocurred */
    private int current_peak; /* peak on which the current best individual is located */
    private int maximum_peak; /* number of highest peak */
    private double current_maximum; /* fitness value of currently best individual */
    private double offline_performance = 0.0;
    private double offline_error = 0.0;
    private double avg_error = 0; /* average error so far */
    private double current_error = 0; /* error of the currently best individual */
    private double global_max; /* absolute maximum in the fitness landscape */
    /**
     * @uml.property  name="evals"
     */
    private int evals = 0; /* number of evaluations so far */

    /* data structure to store peak data */
    public static double[][] peak; //double * * peak;

    private double[] shift; //double * shift;

    private double[] coordinates; //double * coordinates;

    /* which peaks are covered by the population ? */
    private int[] covered_peaks; //int * covered_peaks;

    /* to store every peak's previous movement */
    private double[][] prev_movement; //double * * prev_movement;

    //	/*
    //	 * two variables needed in method movnrand().
    //	 */
    //	static boolean backup = false;
    //	static double x2;

    /*
     * two variables needed in method change_stepsize_linear(). Perhaps it would be
     * appropriate to put change_stepsize_linear() in its own class.
     */
    private static int counter = 1;
    private static double frequency = 3.14159 / 20.0;



    private Random movrand = new Random();
    private Random movnrand = new Random();

    final int PEAKFUNCTION1 = 0;
    final int PEAKFUNCTIONCONE = 1;
    final int PEAKFUNCTIONSPHERE = 2;

    /*
     *  Constructor
     */

    public MovingPeaks(){

    }

    public MovingPeaks(MP_Scenario scenario,
                       long seed){

        this.scenario = scenario;

        long newMovrandseed = scenario.getMovrandseed()  + seed;
        movrand = new Random( newMovrandseed );
        movnrand = new Random( newMovrandseed );
    }

    /**
     * @return
     * @uml.property  name="evals"
     */
    public int getEvals() {
        return evals;
    }

    /* initialize all variables at the beginning of the program */
    public void init_peaks() {
        int i, j;
        double dummy;

        shift = new double[scenario.getGeno_size()];
        this.coordinates = new double[scenario.getGeno_size()];
        this.covered_peaks = new int[scenario.getNumber_of_peaks()];
        MovingPeaks.peak = new double[scenario.getNumber_of_peaks()][];
        this.prev_movement = new double[scenario.getNumber_of_peaks()][];

        for (i = 0; i < scenario.getNumber_of_peaks(); i++) {
            peak[i] = new double[scenario.getGeno_size() + 2];
            prev_movement[i] = new double[scenario.getGeno_size()];
        }

        for (i = 0; i < scenario.getNumber_of_peaks(); i++)
            for (j = 0; j < scenario.getGeno_size(); j++) {
                peak[i][j] = 100.0 * this.movrand.nextDouble();
                this.prev_movement[i][j] = this.movrand.nextDouble() - 0.5;
            }

        if (scenario.getStandardheight() <= 0.0) {

            for (i = 0; i < this.scenario.getNumber_of_peaks(); i++)
                peak[i][scenario.getGeno_size() + 1] =
                        (this.scenario.getMaxheight() - this.scenario.getMinheight()) * this.movrand.nextDouble()
                                + this.scenario.getMinheight();

        }
        else {

            for (i = 0; i < scenario.getNumber_of_peaks(); i++) {

                peak[i][scenario.getGeno_size() + 1] = scenario.getStandardheight();
            } // for
        } // else

        if (scenario.getStandardwidth() <= 0.0) {

            for (i = 0; i < scenario.getNumber_of_peaks(); i++) {

                peak[i][scenario.getGeno_size()] =
                        (scenario.getMaxwidth() - scenario.getMinwidth()) * this.movrand.nextDouble() + scenario.getMinwidth();
            } //for
        }
        else {

            for (i = 0; i < scenario.getNumber_of_peaks(); i++) {

                peak[i][scenario.getGeno_size()] = scenario.getStandardwidth();
            } //for
        } // else

        if (this.calculate_average_error) {

            this.global_max = -100000.0;

            for (i = 0; i < scenario.getNumber_of_peaks(); i++) {

                for (j = 0; j < scenario.getGeno_size(); j++) {

                    this.coordinates[j] = peak[i][j];
                } // for

                dummy = this.dummy_eval(coordinates);

                if (dummy > this.global_max)
                    this.global_max = dummy;
            } //for
        } //if
    } //init_peaks

    /* dummy evaluation function allows to evaluate without being counted */
    public double dummy_eval(double[] gen) {
        int i;
        double maximum = -100000.0, dummy;

        for (i = 0; i < scenario.getNumber_of_peaks(); i++) {
            dummy = scenario.getPf().calculate(gen, i);
            if (dummy > maximum)
                maximum = dummy;
        }

        if (use_basis_function) {

            dummy = scenario.getBf().calculate(gen);
	    /* If value of basis function is higher return it */
            if (maximum < dummy)
                maximum = dummy;
        }
        return (maximum);
    }

    /* evaluation function */
    public double eval_movpeaks(double[] gen) {
        int i;
        double maximum = -100000.0, dummy;

        if ((scenario.getChange_frequency() > 0)
                && (this.evals % scenario.getChange_frequency() == 0))
            this.change_peaks();

        for (i = 0; i < scenario.getNumber_of_peaks(); i++) {
            dummy = scenario.getPf().calculate(gen, i);
            if (dummy > maximum)
                maximum = dummy;
        }

        if (this.use_basis_function) {

            dummy = scenario.getBf().calculate(gen);
	    /* If value of basis function is higher return it */
            if (maximum < dummy)
                maximum = dummy;
        }
        if (this.calculate_average_error) {
            this.avg_error += this.global_max - maximum;
        }
        if (calculate_offline_performance) {
            if (this.recent_change || (maximum > current_maximum)) {
                this.current_error = this.global_max - maximum;
                if (this.calculate_right_peak)
                    this.current_peak_calc(gen);
                this.current_maximum = maximum;
                this.recent_change = false;
            }
            this.offline_performance += this.current_maximum;
            this.offline_error += this.current_error;
        }
        this.evals++; /* increase the number of evaluations by one */
        return (maximum);
    } //eval_movpeaks

    /* whenever this function is called, the peaks are changed */
    public void change_peaks() {
        int i, j;
        double sum, sum2, offset, dummy;

        for (i = 0; i < scenario.getNumber_of_peaks(); i++) {
	    /* shift peak locations */
            sum = 0.0;
            for (j = 0; j < scenario.getGeno_size(); j++) {
                this.shift[j] = this.movrand.nextDouble() - 0.5;
                sum += this.shift[j] * this.shift[j];
            }
            if (sum > 0.0) {
                sum = this.scenario.getVlength() / Math.sqrt(sum);
            } else {/* only in case of rounding errors */
                sum = 0.0;
            }
            sum2 = 0.0;
            for (j = 0; j < scenario.getGeno_size(); j++) {
                this.shift[j] =
                        sum * (1.0 - scenario.getLambda()) * this.shift[j]
                                + scenario.getLambda() * this.prev_movement[i][j];
                sum2 += this.shift[j] * this.shift[j];
            }
            if (sum2 > 0.0)
                sum2 = scenario.getVlength() / Math.sqrt(sum2);
            else /* only in case of rounding errors */
                sum2 = 0.0;
            for (j = 0; j < scenario.getGeno_size(); j++) {
                this.shift[j] *= sum2;
                this.prev_movement[i][j] = this.shift[j];
                if ((peak[i][j] + this.prev_movement[i][j])
                        < scenario.getMincoordinate()) {
                    peak[i][j] =
                            2.0 * scenario.getMincoordinate()
                                    - peak[i][j]
                                    - this.prev_movement[i][j];
                    this.prev_movement[i][j] *= -1.0;
                }
                else if (
                        (peak[i][j] + this.prev_movement[i][j])
                                > scenario.getMaxcoordinate()) {
                    peak[i][j] =
                            2.0 * scenario.getMaxcoordinate()
                                    - peak[i][j]
                                    - this.prev_movement[i][j];
                    this.prev_movement[i][j] *= -1.0;
                }
                else
                    peak[i][j] += prev_movement[i][j];
            }
	    /* change peak width */
            j = scenario.getGeno_size();
            offset = this.movnrand.nextGaussian() * scenario.getWidth_severity();
            if ((peak[i][j] + offset) < scenario.getMinwidth())
                peak[i][j] = 2.0 * scenario.getMinwidth() - peak[i][j] - offset;
            else if ((peak[i][j] + offset) > scenario.getMaxwidth())
                peak[i][j] = 2.0 * scenario.getMaxwidth() - peak[i][j] - offset;
            else
                peak[i][j] += offset;
	    /* change peak height */
            j++;
            offset = scenario.getHeight_severity() * this.movnrand.nextGaussian();
            if ((peak[i][j] + offset) < scenario.getMinheight())
                peak[i][j] = 2.0 * scenario.getMinheight() - peak[i][j] - offset;
            else if ((peak[i][j] + offset) > scenario.getMaxheight())
                peak[i][j] = 2.0 * scenario.getMaxheight() - peak[i][j] - offset;
            else
                peak[i][j] += offset;
        }
        if (this.calculate_average_error) {
            this.global_max = -100000.0;
            for (i = 0; i < scenario.getNumber_of_peaks(); i++) {
                for (j = 0; j < scenario.getGeno_size(); j++)
                    this.coordinates[j] = peak[i][j];
                dummy = this.dummy_eval(coordinates);
                if (dummy > this.global_max) {
                    this.global_max = dummy;
                    this.maximum_peak = i;
                }
            }
        }
        this.recent_change = true;
        //printPeakData();
    } //change_peaks

    /* current_peak_calc determines the peak of the current best individual */
    private void current_peak_calc(double[] gen) {
        int i;
        double maximum = -100000.0, dummy;

        this.current_peak = 0;
        maximum = scenario.getPf().calculate(gen, 0);
        for (i = 1; i < scenario.getNumber_of_peaks(); i++) {
            dummy = scenario.getPf().calculate(gen, i);
            if (dummy > maximum) {
                maximum = dummy;
                this.current_peak = i;
            } // if
        } // for
    } // current_peak_calc

    //	/* simple random number generator solely for the test function */
    //	/* movrand creates random number between 0 and 1 */
    //	/* This RNG is taken from the book by Kernighan/Ritchie, maybe it would */
    //	/* be worth to try a better one. */
    //
    //	double movrand() {
    //		/*  static unsigned long int next;*/
    //		this.movrandseed = this.movrandseed * 1103515245 + 12345;
    //		return (double) ((int) (this.movrandseed / 65536) % 32768) / 32767;
    //	}
    //
    //	/* this function produces normally distributed random values */
    //	double movnrand() {
    //
    //		double x1, w;
    //
    //		if (MovingPeaks.backup) {
    //			MovingPeaks.backup = false;
    //			return (MovingPeaks.x2);
    //		}
    //		else {
    //			do {
    //				x1 = 2.0 * this.movn_rand.nextDouble() - 1.0;
    //				MovingPeaks.x2 = 2.0 * this.movn_rand.nextDouble() - 1.0;
    //				w = x1 * x1 + MovingPeaks.x2 * MovingPeaks.x2;
    //			}
    //			while (w >= 1.0);
    //			w = Math.sqrt((-2.0 * Math.log(w)) / w);
    //			MovingPeaks.x2 = w * MovingPeaks.x2;
    //			MovingPeaks.backup = true;
    //			return (x1 * w);
    //		}
    //	}

    /* free disc space at end of program */
    public void free_peaks() {
        int i;

        for (i = 0; i < scenario.getNumber_of_peaks(); i++) {
            peak[i] = null;
            prev_movement[i] = null;
        }
        System.gc();
    } //free_peaks

    /* The following procedures may be used to change the step size over time */

    private void change_stepsize_random() /* assigns vlength a value from a normal distribution */ {
        scenario.setVlength(this.movnrand.nextGaussian());
    }

    private void change_stepsize_linear() /* sinusoidal change of the stepsize, */ {

	/* returns to same value after 20 changes */

        scenario.setVlength(1 + Math.sin((double) counter * frequency));
        counter++;
    }

    public double get_avg_error() /* returns the average error of all evaluation calls so far */ {
        return (this.avg_error / (double) this.evals);
    }

    public double get_current_error() /* returns the error of the best individual evaluated since last change */
    /* To use this function, calculate_average_error and calculate_offline_performance must be set */ {
        return this.current_error;
    }

    public double get_offline_performance() /* returns offline performance */ {
        return (this.offline_performance / (double) this.evals);
    }

    public double get_offline_error() /* returns offline error */ {
        return (this.offline_error / (double) this.evals);
    }

    public int get_number_of_evals() /* returns the number of evaluations so far */ {
        return this.evals;
    }

    public boolean get_right_peak() /* returns 1 if current best individual is on highest peak, 0 otherwise */ {
        if (this.current_peak == this.maximum_peak)
            return true;
        else
            return false;
    }

    public double[][] getPeakPositions(){

        double[][] positions = new double[ scenario.getNumber_of_peaks() ][ scenario.getGeno_size() ];

        for ( int i = 0; i < scenario.getNumber_of_peaks(); i++ ){

            for ( int j = 0 ; j < scenario.getGeno_size(); j++ ){

                positions[ i ][ j ] = peak[ i ][ j ];
            }
        }
        return positions;
    }

    public double[] getPeakHeights(){

        double[] temp = new double[ scenario.getNumber_of_peaks() ];
        int index = scenario.getGeno_size() + 1;
        for ( int i = 0; i < scenario.getNumber_of_peaks(); i++ ){
            temp[ i ] = peak[i][ index ];
        }
        return temp;
    }

    public void printPeakData(){

        double[] temp = getPeakHeights();
        System.out.print( "Peak heights:\t" );
        for ( int i = 0; i < temp.length; i++ ){
            System.out.print( temp[ i ] + "\t" );
        }
        System.out.println();

        //System.out.println("Current peak: " + this.current_peak + "\tMax peak: " + this.maximum_peak );
    }

    public int getCurrentPeak(){

        return this.current_peak;
    }

    public int getMaximumPeak(){

        return this.maximum_peak;
    }




    /**
     * @param i
     * @uml.property  name="evals"
     */
    public void setEvals(int i) {
        evals = i;

    }

    public void set_Avg_error(double avg_error) {
        this.avg_error = avg_error;
    }

    public void set_current_error(double avg_error) {
        this.current_error = avg_error;
    }

    public void set_offline_error(double avg_error) {
        this.offline_error = avg_error;
    }

    public void set_offline_performance(double avg_error) {
        this.offline_performance = avg_error;
    }




    @Override
    public boolean isFeasible(Representation i) {
        return false;
    }

    @Override
    public double cost(Representation i) {
        return -1*eval_movpeaks(((DoubleVector)i).getValues());
    }

    @Override
    public double maxDistance() {
        return Math.sqrt(scenario.getGeno_size())*(getUpperBound()-getLowerBound());
    }

    @Override
    public int getDimensionCount() {
        return MP_Scenario.geno_size;
    }

    @Override
    public double getUpperBound() {
        return scenario.getMaxcoordinate();
    }

    @Override
    public double getLowerBound() {
        return scenario.getMincoordinate();
    }
} //MovingPeaks