package problems.motap.mutation;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.InvalidProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.base.Representation;

/**
 * Created by oz on 16.07.2015.
 */
public class GreedySearch implements MutationOperator {


    public int improveCount;

    @Override
    public Representation apply(OptimizationProblem problem, Representation i) {

        if (!(problem instanceof MOTAProblem))
            throw new InvalidProblem("Works only for MOTA Problems");

        if (!(i instanceof IntegerAssignment))
            throw new InvalidIndividual("Works only for Integer Assignments");

        MOTAProblem mp = (MOTAProblem)problem;
        IntegerAssignment ni = (IntegerAssignment)i.clone();

        search(mp, ni);

        return ni;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

    private void search(MOTAProblem mp, IntegerAssignment ni) {

        boolean improved = true;
        int tryCount =0;
        while (improved) {
            improved = false;
            double gain =0;
            int processor= 0;
            int module =0;

            for (int m =0;m<ni.getLength();m++) {
                for (int i = 0; i < mp.getDcs().getProcessors().size(); i++) {
                    if (i == ni.get(m))
                        continue;

                    double diff = mp.costDifference(mp.getTask(), m, ni.get(m), i, ni);
                    if (diff < gain) {
                        processor = i;
                        module = m;
                        gain = diff;
                    }
                }
                tryCount++;
            }

            if (gain<0)
            {
                ni.set(module,processor);
                improved = true;
            }
        }
        improveCount = tryCount/5;

    }


}
