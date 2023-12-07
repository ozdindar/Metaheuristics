package metaheuristic.pso;


import metaheuristic.pso.base.Velocity;
import org.apache.commons.math3.util.Pair;
import representation.IntegerPermutation;
import representation.base.Representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dindar.oz on 17.06.2015.
 */
public class IntegerPermutationVelocity implements Velocity {

    List<Pair<Integer,Integer>> moveList= new ArrayList<>();

    public IntegerPermutationVelocity(List<Pair<Integer, Integer>> moveList) {
        this.moveList = moveList;
    }
    public IntegerPermutationVelocity() {

    }

    public Velocity opposite()
    {
        List<Pair<Integer,Integer>> oppList= new ArrayList<>();
        for (int i=moveList.size()-1;i>=0;i--)
        {
            oppList.add(new Pair<>(moveList.get(i).getValue(),moveList.get(i).getKey()));
        }
        return new IntegerPermutationVelocity(oppList);
    }

    @Override
    public Velocity multiply(double c) {
        if (c==0)
            return new IntegerPermutationVelocity();

        if (c<0)
        {
            return opposite().multiply(-c);
        }
        if (c<1)
        {
            int cv = (int)(c*moveList.size());
            List<Pair<Integer,Integer>> mList= new ArrayList<>();
            for (int i=0;i<cv;i++)
            {
                mList.add(moveList.get(i));
            }
            return new IntegerPermutationVelocity(mList);
        }
        else
        {
            int ic = (int)c;
            IntegerPermutationVelocity v= new IntegerPermutationVelocity();
            for (int i=0;i<ic;i++)
            {
                v = (IntegerPermutationVelocity)add(v);
            }
            v.add(multiply(c-ic));
            //v.contract();
            return v;
        }
    }

    @Override
    public Velocity distance(Representation r1, Representation r2) {

        IntegerPermutation ip1 = (IntegerPermutation)r1.clone();
        IntegerPermutation ip2 = (IntegerPermutation)r2.clone();

        List<Pair<Integer,Integer>> mList= new ArrayList<>();

        for (int i=0;i<ip1.getLength();i++)
        {
            if (!ip1.get(i).equals(ip2.get(i)))
            {
                int n = ip2.firstOf(ip1.get(i));
                mList.add(new Pair<>(i+1,n+1));
                ip2.swap(i,n);
            }
        }

        return new IntegerPermutationVelocity(mList);
    }

    @Override
    public void move(Representation r) {
        IntegerPermutation ip = (IntegerPermutation)r ;

        for (Pair<Integer,Integer> p:moveList)
        {
            ip.swap(p.getKey()-1,p.getValue()-1);
        }
    }

    @Override
    public Velocity add(Velocity v) {

        IntegerPermutationVelocity ipv = (IntegerPermutationVelocity)v;
        List<Pair<Integer,Integer>> sumList= new ArrayList<>();
        for (int i=0; i<moveList.size();i++)
        {
            sumList.add(new Pair<>(moveList.get(i).getKey(),moveList.get(i).getValue()));
        }
        for (int i=0; i<ipv.moveList.size();i++)
        {
            sumList.add(new Pair<>(ipv.moveList.get(i).getKey(),ipv.moveList.get(i).getValue()));
        }

        ipv = new IntegerPermutationVelocity(sumList);
        ipv.contract();

        return ipv;
    }

    @Override
    public boolean isNullVelocity() {
        return moveList.isEmpty();
    }

    public IntegerPermutationVelocity clone()
    {
        List<Pair<Integer,Integer>> mList= new ArrayList<>();
        for (int i =0; i<moveList.size();i++)
        {
            mList.add(new Pair<>(moveList.get(i).getKey(),moveList.get(i).getValue()));
        }
        return new IntegerPermutationVelocity(mList);
    }

    public int maxPosition()
    {
        int m=0;
        for (int i=0;i<moveList.size();i++)
        {
            if (moveList.get(i).getKey()>m)
                m =moveList.get(i).getKey();
            if (moveList.get(i).getValue()>m)
                m =moveList.get(i).getValue();

        }
        return m;
    }

    void contract()
    {
        if (moveList.size()<2)
            return;

        int m = maxPosition();
        int permutation[] = new int[m];
        for (int i=0;i<m;i++)
        {
            permutation[i]= i+1;
        }

        IntegerPermutation ip= new IntegerPermutation(permutation);

        HashMap<String,Integer> pHash = new HashMap<>();
        pHash.put(ip.toString(), 0);
        int current =0;

        int swapList[] = new int[moveList.size()];

        while (true)
        {
            if (current>=moveList.size())
                break;

            Pair<Integer,Integer> p= moveList.get(current);

            ip.swap(p.getKey()-1,p.getValue()-1);
            String key = ip.toString();
            if (pHash.containsKey(key))
            {
                swapList[current] = pHash.get(key);
            }
            else{
                pHash.put(key,current+1);
                swapList[current] = -1;
            }

            current++;
        }

        List<Pair<Integer,Integer>> mList= new ArrayList<>();
        int i = swapList.length-1;
        while (i>=0)
        {
            if (swapList[i]>=0)
            {
                i=swapList[i]-1;
                continue;
            }

            mList.add(0, moveList.get(i));
            i--;
        }

        moveList = mList;


        //todo:

    }



    public static void main(String args[])
    {
        testDistance();
   //     testContract();

    }

    private static void testDistance() {
        IntegerPermutation ip1 = new IntegerPermutation(new int[]{1,2,3,4,5,6});
        IntegerPermutation ip2 = new IntegerPermutation(new int[]{6,3,4,5,2,1});

        IntegerPermutationVelocity ipv = new IntegerPermutationVelocity();

        ipv = (IntegerPermutationVelocity)ipv.distance(ip1,ip2);

        int i = ipv.maxPosition();
    }


    private static void testContract() {
        List<Pair<Integer,Integer>> mList= new ArrayList<>();

        mList.add(new Pair<>(7,8));
        //mList.add(new Pair<>(1,3));
        mList.add(new Pair<>(2,4));
        mList.add(new Pair<>(3,1));
        mList.add(new Pair<>(4,2));
        mList.add(new Pair<>(1,3));
        mList.add(new Pair<>(2,4));
        mList.add(new Pair<>(5,6));

        IntegerPermutationVelocity ipv = new IntegerPermutationVelocity(mList);

        ipv.contract();


    }
}
