package problems.mccdp.solutiongenerator;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import problems.mccdp.MCCDP;
import problems.mccdp.WSNModel;
import representation.IntegerVector;
import representation.base.Representation;
import util.ArrayUtil;
import util.random.RNG;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 1.06.2017.
 */
public class SmartSG implements InitialSolutionGenerator {

    private int RandomNodeCount= 2;

    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        MCCDP mccdp = (MCCDP)problem;

        List<Representation> solutions = new ArrayList<>(c);


        for (int i = 0; i <c; i++) {
            Representation solution = generateASolution((MCCDP)problem,rng);
            solutions.add(solution);
        }

        return solutions;
    }

    private Representation generateASolution(MCCDP problem, RNG rng) {



        List<Integer> nodes = new ArrayList<>();
        WSNModel model =  problem.getWsnModel();

        for (; nodes.size() < RandomNodeCount;) {
            int n = rng.randInt(problem.targetPointCount());
            if (!nodes.contains(n))
                nodes.add(n);
        }
        int coverage[]= model.calculateCoverageCounts(nodes);
        while (model.coveredPointsCount(coverage)-problem.targetPointCount()<0)
        {
            putNewNode(nodes,model,coverage);
        }
        Representation solution = new IntegerVector(nodes);
        return solution;
    }

    private void putNewNode(List<Integer> nodes, WSNModel model, int[] coverage) {
        int maxDelta=0;
        int maxDeltaNode=-1;
        int maxCoverage[]= null;

        int coverageScore = model.coveredPointsCount(coverage);
        for (int s = 0; s<model.sensorPointCount();s++)
        {
            if (nodes.contains(s))
                continue;
            nodes.add(s);
            int newCoverage[]= model.deltaCoverage(coverage,s);
            int delta= model.coveredPointsCount(newCoverage)- coverageScore;
            if (delta>maxDelta || (maxDelta==delta && RandUtil.rollDice(0.5)))
            {
                maxDelta = delta;
                maxDeltaNode = s;
                maxCoverage = newCoverage;
            }

            nodes.remove(nodes.size()-1);
        }

        if (maxDeltaNode>=0) {
            nodes.add(maxDeltaNode);

            ArrayUtil.arrayCopy(maxCoverage,coverage);
        }
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        return generate(problem, RandUtil.getDefaultRNG(),c);
    }
}
