package problems.pcb.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import representation.IntegerPermutation;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.*;

/**
 * Created by dindar.oz on 10.06.2015.
 */
public class GuidedTwoOpt implements MutationOperator{

    protected HashMap<Integer, ArrayList<Integer>> guidedMap;

    public GuidedTwoOpt(HashMap<Integer, ArrayList<Integer>> guidedMap) {
        this.guidedMap = guidedMap;
    }

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        IntegerPermutation ip = (IntegerPermutation)r;

        IntegerPermutation np = (IntegerPermutation)ip.clone();

        int len = ip.getLength();
        if (len<2)
            return np;


        int startIndex = (len== 2) ? 0: RandUtil.randInt(len - 2);

        ArrayList endIndexList  = guidedMap.get(ip.get(startIndex));;
        int endIndexofIndex = (int) endIndexList.get(RandUtil.randInt(endIndexList.size()));
        int endIndex =  ip.firstOf(endIndexofIndex);

        int temp;
        if(endIndex < startIndex){
            temp = startIndex;
            startIndex = endIndex;
            endIndex = temp;
        }

        int l = endIndex-startIndex+1;

        if (l<2)
            return np;

        Integer[] sub = Arrays.copyOfRange(np.getValues(), startIndex, startIndex + l);

        List<Integer> subList = new ArrayList<>(Arrays.asList(sub));

        Collections.reverse(subList);


        for (int i=0;i<l;i++)
        {
            np.getValues()[i+startIndex]= subList.get(i);
        }

        return np;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

}
