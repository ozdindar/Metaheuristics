package representation;

import representation.base.Representation;

import java.util.Vector;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class PermutationList implements Representation {

    public Vector<Vector<Integer>> permutationList;



    public PermutationList(Vector<Vector<Integer>> permutationList) {
        this.permutationList = permutationList;
    }

    public PermutationList(PermutationList pl) {
        this.permutationList = new Vector<>();
        for (Vector<Integer> v:pl.permutationList)
        {
            Vector<Integer> permutation = new Vector<>();
            for(Integer i:v)
            {
                permutation.add(i);
            }
            permutationList.add(permutation );
        }

    }

    @Override
    public String toString() {
        String st = permutationList.size()+ "\n";
        for (Vector<Integer> tour:permutationList)
        {
            st += toString(tour)+ "\n";
        }
        return st;
    }

    private String toString(Vector<Integer> tour) {
        String st = "[";
        for (int i =0; i<tour.size()-1;i++)
        {
            st += tour.get(i) + ",";
        }
        st += tour.lastElement()+ "]";

        return st;
    }

    @Override
    public Representation clone() {
        return new PermutationList(this);
    }

    @Override
    public double distanceTo(Representation r) {
        return 0;
    }

    public void externalSwap(int tour1, int n1, int tour2, int n2) {
        Vector<Integer> permutation1 = permutationList.get(tour1);
        Vector<Integer> permutation2 = permutationList.get(tour2);
        int t = permutation1.get(n1);
        permutation1.set(n1,permutation2.get(n2));
        permutation2.set(n2,t);
    }

    public void internalSwap(int tour, int n1, int n2) {
        Vector<Integer> permutation = permutationList.get(tour);
        int t = permutation.get(n1);
        permutation.set(n1,permutation.get(n2));
        permutation.set(n2,t);
    }
}
