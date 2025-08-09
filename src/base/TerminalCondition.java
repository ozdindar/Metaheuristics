package base;

import metaheuristic.MetaHeuristic;
import representation.base.Population;

/**
 * Created by dindar.oz on 22.04.2015.
 */
public interface TerminalCondition {
    TerminalCondition NullTC = new TerminalCondition() {
        @Override
        public boolean isSatisfied(MetaHeuristic alg, Population population, OptimizationProblem problem) {
            return false;
        }

        @Override
        public boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem) {
            return false;
        }

        @Override
        public TerminalCondition clone() {
            return null;
        }

        @Override
        public void init() {

        }
    };

    boolean isSatisfied(MetaHeuristic alg, Population population, OptimizationProblem problem);
    boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem);
    TerminalCondition clone();
    void init();
}
