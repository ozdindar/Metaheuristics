package experiments;

import base.OptimizationProblem;
import metaheuristic.AbstractMetaheuristic;
import problems.base.InitialSolutionGenerator;
import metrics.partitioning.SearchSpacePartitioner;

public class ExperimentCase {
    final String title;
    final SearchSpacePartitioner ssp;
    final AbstractMetaheuristic alg;
    final OptimizationProblem problem;
    final InitialSolutionGenerator isg;


    public ExperimentCase(String title, SearchSpacePartitioner ssp, AbstractMetaheuristic alg, OptimizationProblem problem, InitialSolutionGenerator isg) {
        this.title = title;
        this.ssp = ssp;
        this.alg = alg;
        this.problem = problem;
        this.isg = isg;
    }

    public String getTitle() {
        return title;
    }

    public SearchSpacePartitioner getSsp() {
        return ssp;
    }

    public AbstractMetaheuristic getAlg() {
        return alg;
    }

    public OptimizationProblem getProblem() {
        return problem;
    }

    public InitialSolutionGenerator getIsg() {
        return isg;
    }

    @Override
    public String toString() {
        return title;
    }
}