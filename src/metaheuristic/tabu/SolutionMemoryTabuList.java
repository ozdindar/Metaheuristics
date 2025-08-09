package metaheuristic.tabu;

import base.NeighboringFunction;
import base.OptimizationProblem;
import metaheuristic.tabu.base.TabuList;
import representation.base.Representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 23.06.2015.
 */
public class SolutionMemoryTabuList implements TabuList {

    int capacity;
    List<Representation> solutionMemory = new ArrayList<>();

    public SolutionMemoryTabuList(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean isTabu(OptimizationProblem problem, Representation solution, NeighboringFunction nf) {
        return solutionMemory.contains(solution);
    }

    @Override
    public void record(OptimizationProblem problem, Representation currentSolution, NeighboringFunction nf) {
        solutionMemory.add(currentSolution);
        while (solutionMemory.size()>capacity)
            solutionMemory.remove(0);
    }

    @Override
    public void record(OptimizationProblem problem, Representation currentSolution) {
        solutionMemory.add(currentSolution);
        while (solutionMemory.size()>capacity)
            solutionMemory.remove(0);
    }
}
