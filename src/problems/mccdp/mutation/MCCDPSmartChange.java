package problems.mccdp.mutation;

import base.OptimizationProblem;
import exceptions.WrongIndividualType;
import metaheuristic.ea.base.MutationOperator;
import problems.mccdp.MCCDP;
import problems.mccdp.WSNModel;
import representation.IntegerVector;
import representation.base.Representation;

import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class MCCDPSmartChange implements MutationOperator {
    private static final int MAX_ATTEMPT = 5;
    private final int ShiftRange= 2;

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        if (!(r instanceof IntegerVector))
            throw new WrongIndividualType("Random Change can only be applied to IntegerVector");

        MCCDP mccdp = (MCCDP)problem;

        IntegerVector nodes = (IntegerVector) r.clone();

        for (int i = 0; i < nodes.getLength(); i++) {
            improveSensor(mccdp,nodes.getList(),i);
        }

        return (Representation)nodes
                ;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

    private void improveSensor(MCCDP mccdp, List<Integer> list, int index) {
        WSNModel wsnModel =  mccdp.getWsnModel();
        int coverageCounts[] = wsnModel.calculateCoverageCounts(list);
        int totalCoverage = wsnModel.coveredPointsCount(coverageCounts);

        int node = list.get(index);
        List<Integer> neigbors = wsnModel.neighborsOf(node,ShiftRange);

        for (Integer n:neigbors)
        {
            int newCoverage[] = wsnModel.deltaCoverage(coverageCounts,node,n);
            int newTotalCoverage = wsnModel.coveredPointsCount(newCoverage);
            if (newTotalCoverage>totalCoverage) {
                node = n;
                coverageCounts = newCoverage;

            }
        }
        list.set(index,node);
    }
}
