package metaheuristic.pso;


import metaheuristic.pso.base.PSOProblem;
import metaheuristic.pso.base.ParticleHandler;
import metaheuristic.pso.base.Velocity;
import org.apache.commons.math3.util.Pair;
import representation.IntegerPermutation;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 17.06.2015.
 */
public class IntegerPermutationParticleHandler implements ParticleHandler {

    int MAX_INITIAL_STEP =10;

    @Override
    public Class getRepresentationClass() {
        return IntegerPermutation.class;
    }

    public Velocity generateInitialVelocity(PSOProblem problem, int length)
    {
        int steps = RandUtil.randInt(MAX_INITIAL_STEP);

        List<Pair<Integer,Integer>> mList= new ArrayList<>();

        for (int i=0;i<steps;i++)
        {
            int n1 = RandUtil.randInt(length)+1;
            int n2 = n1;
            while (n2!= n1)
                n2 = RandUtil.randInt(length)+1;

            mList.add(new Pair(n1,n2));
        }


        IntegerPermutationVelocity v = new IntegerPermutationVelocity(mList);
        v.contract();
        return v ;
    }


}
