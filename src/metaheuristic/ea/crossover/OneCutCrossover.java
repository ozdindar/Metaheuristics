package metaheuristic.ea.crossover;

import base.OptimizationProblem;
import exceptions.WrongIndividualType;
import metaheuristic.ea.base.CrossOverOperator;
import representation.IntegerPermutation;
import representation.IntegerVector;
import representation.base.Array;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */

/**
 * crossover operator written for PSO
 * See: Pan, tasgetiren, Liang, A Discrete PSO algorithm for the no-wait
 * flowshop scheduling problem, p.2811, 2008, (journal)
 * randomly selects a cut point, r1
 * gets the elements up to r1 from this permutation, the rest are taken from
 * Solution V in the same order, taking care of repetition
 * The reverse is done with 0.5 prob.
 * example, assume r1=2
 * P1:5,1,4,2,3
 * P2:3,5,4,2,1
 * O1:5,1,3,4,2
 * O2:3,5,1,4,2
 */
public class OneCutCrossover implements CrossOverOperator {
    @Override
    public List<Representation> apply(OptimizationProblem problem, Representation i1, Representation i2) {

        if (!(i1 instanceof Array) || !(i2 instanceof Array))
            throw new WrongIndividualType("Swap can only be applied to permutation");



        Array p1 = (Array)i1;
        Array p2 = (Array)i2;
        boolean change= RandUtil.rollDice(0.5);

        if (change)
        {
            p1= p2;
            p2 = (Array)i1;
        }

        int n = p1.getLength();
        int cutPoint = RandUtil.randInt(n);

        Array p3 = (Array)p1.cloneArray();

        if (cutPoint!=p1.getLength()-1 && cutPoint !=0 && cutPoint<p2.getLength()) { // no change otherwise
            for (int i = cutPoint; i < n; i++) {
                p3.set(i, 0);
            }

            for (int i = cutPoint; i < p2.getLength(); i++) {
                if (!p3.exists(p2.get(i))) {
                    p3.set(i, p2.get(i));
                }
            }

            for (int i = 1; i <= n; i++) {
                if (!p3.exists(i)) {
                    int x = p3.firstOf(Integer.valueOf(0));
                    if (x == -1)
                        continue;
                    p3.set(x, i);
                }
            }
        }

        List<Representation> offsprings = new ArrayList<Representation>();
        offsprings.add((Representation)p3);

        return offsprings;
    }

    public static void main(String args[])
    {
        IntegerPermutation ip1 = new IntegerPermutation(new int[]{1,2,3,4,5,6});
        IntegerPermutation ip2 = new IntegerPermutation(new int[]{6,5,4,3,2,1});

        OneCutCrossover co = new OneCutCrossover();

        IntegerVector iv1 = new IntegerVector(Arrays.asList(5,1,3,2,4,7,6));
        IntegerVector iv2 = new IntegerVector(Arrays.asList(1,2,3,4,5,6,7));


        IntegerVector iv3 = (IntegerVector) (co.apply(null, iv1, iv2).get(0));

        System.out.println(iv3);
    }
}
