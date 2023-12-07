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
public class AndCompoundTC implements TerminalCondition {
    List<TerminalCondition> tcList;

    public AndCompoundTC(List<TerminalCondition> tcList) {
        this.tcList = tcList;
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, Population population, OptimizationProblem problem) {
        for (TerminalCondition tc:tcList)
        {
            if (!tc.isSatisfied(alg,population,problem))
                return false;
        }
        return true;
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem) {
        for (TerminalCondition tc:tcList)
        {
            if (!tc.isSatisfied(alg,problem))
                return false;
        }
        return true;
    }

    @Override
    public TerminalCondition clone() {
        List<TerminalCondition> tcList = new ArrayList<>();
        for (TerminalCondition tc:this.tcList)
        {
            tcList.add(tc.clone());
        }
        return new AndCompoundTC(tcList);
    }

    @Override
    public void init() {

    }
}
