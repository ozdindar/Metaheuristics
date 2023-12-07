package metaheuristic.ea.crossover;

import base.OptimizationProblem;
import exceptions.WrongIndividualType;
import metaheuristic.ea.base.CrossOverOperator;
import representation.*;
import representation.base.Array;
import representation.base.Representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class SimplePermutationCrossover implements CrossOverOperator {
    @Override
    public List<Representation> apply(OptimizationProblem problem, Representation i1, Representation i2) {

        if (!(i1 instanceof Array) || !(i2 instanceof Array))
            throw new WrongIndividualType("Swap can only be applied to permutation");

        Array p1 = (Array)i1;
        Array p2 = (Array)i2;

        int n = p1.getLength();

        Array p3 = (Array)i1.clone();

        for (int i = (n/2) ;i<n;i++)
        {
            p3.set(i,Integer.valueOf(0));
        }
        for (int i = (n/2) ;i<n;i++)
        {
            if (!p3.exists(p2.get(i)))
            {
                p3.set(i,p2.get(i));
            }
        }

        for (int i = 1 ;i<=n;i++)
        {
            if (! p3.exists(i) )
            {
                int x = p3.firstOf(Integer.valueOf(0));
                if (x ==-1)
                    continue;
                p3.set(x, i);
            }
        }

        List<Representation> offsprings = new ArrayList<Representation>();
        offsprings.add((Representation)p3);

        return offsprings;
    }
}
