package problems.pcb.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import representation.base.Array;
import representation.base.Representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dindar.oz on 10.06.2015.
 */
public class GuidedRandomRemoveReinsert implements MutationOperator{

    protected HashMap<Integer, ArrayList<Integer>> guidedMap;

    public GuidedRandomRemoveReinsert(HashMap<Integer, ArrayList<Integer>> guidedMap) {
        this.guidedMap = guidedMap;
    }

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        Array ni = (Array) r.clone();
        List list = ni.getList();

        int endIndexofIndex;

        Integer termHavingBeenRemoved = (Integer)list.remove((int)(list.size()*Math.random()));

        ArrayList endIndexList = guidedMap.get(termHavingBeenRemoved);
        endIndexofIndex = (int)(endIndexList.size()*Math.random());
        endIndexofIndex = (int)(endIndexList.get(endIndexofIndex));

        int indexToBeInserted = list.indexOf(endIndexofIndex);

        list.add(indexToBeInserted, termHavingBeenRemoved);

        ni.setList(list);
        return (Representation)ni;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

}
