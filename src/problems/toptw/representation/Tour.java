package problems.toptw.representation;

import java.util.Arrays;
import java.util.Vector;

/**
 * Created by dindar.oz on 8.11.2016.
 */
public class Tour {
    private Vector<Integer> nodes;
    public double tourTimes[];


    public double waitTimes[];


    boolean profitCalculated;
    boolean feasibilityCalculated;
    boolean tourTimeCalculated;

    double profit;
    double  tourTime;
    boolean feasible;
    int  infeasibilityCount;
    private boolean empty;


    public int getInfeasibilityCount() {
        return infeasibilityCount;
    }


    public boolean isFeasibilityCalculated() {
        return feasibilityCalculated;
    }

    public boolean isTourTimeCalculated() {
        return tourTimeCalculated;
    }

    public boolean isFeasible() {
        return feasible;
    }



    public double getTourTime() {
        return tourTime;
    }



    public Tour(Vector<Integer> nodes) {
        this.nodes = nodes;
        tourTimes = new double[nodes.size()];
        makeDirty(true);
    }



    void swap(int m, int n)
    {
        int t = nodes.get(m);
        nodes.set(m,nodes.get(n));
        nodes.set(n,t);

        makeDirty(true);
    }

    private void makeDirty(boolean b) {
        profitCalculated = !b;
        feasibilityCalculated = !b;
        tourTimeCalculated = !b;
    }

    protected Tour clone()  {
        Vector<Integer> nodes = new Vector<>(this.nodes.size());
        for (Integer i:this.nodes)
        {
            nodes.add(i.intValue());
        }


        Tour t= new Tour(nodes);
        t.profitCalculated = profitCalculated;
        t.tourTimeCalculated = tourTimeCalculated;
        t.feasibilityCalculated = feasibilityCalculated;



        if (t.tourTimeCalculated) {
            t.tourTimes = Arrays.copyOf(tourTimes,tourTimes.length);
            t.waitTimes = Arrays.copyOf(waitTimes,waitTimes.length);
        }


        t.profit = profit;
        t.tourTime = tourTime;
        t.feasible = feasible;
        t.infeasibilityCount = infeasibilityCount;



        return t;
    }

    public int size() {
        return nodes.size();
    }



    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
        profitCalculated = true;
    }

    public int get(int i) {
        return nodes.get(i);
    }


    public boolean isProfitCalculated() {
        return profitCalculated;
    }

    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
        feasibilityCalculated = true;
    }

    public void setTourTime(double tourTime) {
        this.tourTime = tourTime;
        tourTimeCalculated = true;
    }

    public int lastElement() {
        return nodes.lastElement();
    }

    public void set(int index, int node) {
        nodes.set(index,node);
        makeDirty(true);
    }

    @Override
    public String toString() {
        if (nodes.isEmpty())
            return "Tour{}";
        String st =  "Tour{";

        for (int i=0; i<nodes.size()-1; i++)
        {
            st += nodes.get(i) + ",";
        }

        st += nodes.lastElement() +"}";

        return st;
    }

    public void remove(int node) {
        nodes.remove(node);
        makeDirty(true);
    }

    public boolean contains(int i) {
        return nodes.contains(new Integer(i));
    }

    public void setInfeasibilityCount(int infeasibilityCount) {
        this.infeasibilityCount = infeasibilityCount;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public void add(int n1) {
        nodes.add(n1);
        makeDirty(true);
    }

    public void setTourTime(int i, double tourTime, double waitTime) {
        tourTimes[i] = tourTime;
        waitTimes[i] = waitTime;
    }

    public void insert(Integer node,int index) {
        if (index<0 || index>=nodes.size())
            return;

        nodes.add(new Integer(nodes.lastElement()));
        for (int i = nodes.size()-2; i>index;i--)
        {
                nodes.set(i,nodes.get(i-1).intValue());
        }
        nodes.set(index,node);
        makeDirty(true);

    }

    public double getTourTime(int i) {
        return tourTimes[i];
    }

    public double getWaitTime(int i) {
            return waitTimes[i];
    }

    public void resetTourTimes() {
        tourTimes = new double[nodes.size()];
        tourTimeCalculated =false;
    }



}
