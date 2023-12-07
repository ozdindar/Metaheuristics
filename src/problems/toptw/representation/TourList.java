package problems.toptw.representation;

import representation.base.Representation;

import java.util.Vector;

/**
 * Created by dindar.oz on 8.11.2016.
 */
public class TourList implements Representation {

    public Vector<Tour> tours;

    public TourList(Vector<Tour> tours) {
        this.tours = tours;
    }

    public TourList(TourList other) {
        tours = new Vector<>(other.tours.size());
        for (Tour t: other.tours)
        {
            tours.add(t.clone());
        }
    }


    @Override
    public Representation clone() {
        return new TourList(this);
    }

    @Override
    public double distanceTo(Representation r) {
        return 0;
    }

    public int tourCount() {
        return tours.size();
    }

    public Tour get(int tour) {
        return tours.get(tour);
    }

    public void internalSwap(int tour, int n1, int n2) {
        tours.get(tour).swap(n1,n2);
    }

    public void externalSwap(int tour1, int n1, int tour2, int n2) {
        if (tour1 == tour2)
            internalSwap(tour1,n1,n2);
        else {
            int v1 = tours.get(tour1).get(n1);
            int v2 = tours.get(tour2).get(n2);
            tours.get(tour1).set(n1,v2);
            tours.get(tour2).set(n2,v1);

        }
    }

    @Override
    public String toString() {
        String st ="";
        for (Tour t:tours)
        {
            st += t+ " ";
        }
        return st;
    }

    public void removeNode(int tour, int node) {
        tours.get(tour).remove(node);
    }

    public boolean contains(int i) {
        for (Tour t: tours)
        {
            if (t.contains(i))
                return true;
        }
        return false;
    }

    public void addNode(int tour, int n1) {
        tours.get(tour).add(n1);
    }

    public void insert(int tour, int node, int index) {

        tours.get(tour).insert(node,index);
    }
}
