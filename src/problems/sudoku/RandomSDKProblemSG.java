package problems.sudoku;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.base.Representation;
import util.random.RNG;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 15.12.2016.
 */
public class RandomSDKProblemSG implements InitialSolutionGenerator {
    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        return generate(problem,c);
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        SDKProblem sdkProblem = (SDKProblem)problem;
        SDKRepresentation initialRep = new SDKRepresentation(sdkProblem.blockSize,sdkProblem.initialBoard);

        List<Representation> initialStates = new ArrayList<Representation>();
        for (int i =0 ;i<c;i++)
        {
            Representation ci =    initialRep.clone();
            initialStates.add(ci);
        }
        return initialStates;
    }
}
