package metaheuristic.ea.terminalcondition;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.MetaHeuristic;
import representation.base.Population;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class OrCompoundTC implements TerminalCondition {
    List<TerminalCondition> tcList;

    public OrCompoundTC(List<TerminalCondition> tcList) {
        this.tcList = tcList;
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, Population population, OptimizationProblem problem) {
        for (TerminalCondition tc:tcList)
        {
            if (tc.isSatisfied(alg,population,problem))
                return true;
        }
        return false;
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem) {
        for (TerminalCondition tc:tcList)
        {
            if (tc.isSatisfied(alg,problem))
                return true;
        }
        return false;
    }

    @Override
    public TerminalCondition clone() {
        List<TerminalCondition> tcList = new ArrayList<>();
        for (TerminalCondition tc:this.tcList)
        {
            tcList.add(tc.clone());
        }
        return new OrCompoundTC(tcList);
    }

    @Override
    public void init() {

    }

    public void add(TerminalCondition terminalCondition) {
        tcList.add(terminalCondition);
    }
}
