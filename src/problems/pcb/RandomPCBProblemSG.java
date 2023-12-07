package problems.pcb;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.IntegerPermutation;
import representation.base.Representation;
import util.random.RNG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dindar.oz on 15.12.2016.
 */
public class RandomPCBProblemSG implements InitialSolutionGenerator {
    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        return generate(problem,c);
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        PCBProblem pcbProblem = (PCBProblem) problem;
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0 ;i<pcbProblem.getData().getN(); i++)
        {
            list.add(i+1);
        }

        List<Representation> pList = new ArrayList<Representation>();
        for (int p=0;p<c; p++)
        {
            Collections.shuffle(list);
            IntegerPermutation ip = new IntegerPermutation(list);

            pList.add(ip);
        }
        return pList;
    }
}
