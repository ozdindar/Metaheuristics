package metaheuristic.ea.terminalcondition;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.MetaHeuristic;
import problems.motap.MOTAProblem;
import representation.base.Population;

import java.util.ArrayList;

public class MotapBasedTc implements TerminalCondition {

    TerminalCondition terminalCondition;

    public MotapBasedTc(MOTAProblem motaProblem , int nonImprovementCount) {
        ArrayList<TerminalCondition> tcList = new ArrayList<>();
        tcList.add(new CPUTimeBasedTC(motaProblem.getModuleCount()*motaProblem.getProcessorCount()*1000));
        tcList.add(new NotImprovementTC(nonImprovementCount));
        terminalCondition = new OrCompoundTC(tcList);
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, Population population, OptimizationProblem problem) {
        return terminalCondition.isSatisfied(alg,population,problem);
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem) {
        return terminalCondition.isSatisfied(alg,problem);
    }

    @Override
    public TerminalCondition clone() {
        TerminalCondition clone = terminalCondition.clone();
        return clone;
    }

    @Override
    public void init() {
        terminalCondition.init();
    }
}
