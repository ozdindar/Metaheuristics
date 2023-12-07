package problems.raps;

import base.OptimizationProblem;
import representation.IntegerMatrix;
import representation.base.Representation;

import java.util.Arrays;

/**
 * Created by dindar.oz on 6/23/2017.
 */
public class RAPS implements OptimizationProblem {

    int C; //  cost limit;
    int W; // weight limit;
    int Nmax; // # of components;

    Component components[][];

    int k[]; // min # of components required in subsytem i
    private double averageComponentCost;
    private double averageComponentWeight;

    public RAPS(int c, int w, int nmax, Component[][] components, int[] k) {
        C = c;
        W = w;
        Nmax = nmax;
        this.components = components;
        this.k = k;
        calculateAverages();
    }

    private void calculateAverages() {
        int totalCost=0;
        int totalWeight=0;
        int cc=0;
        for (int s = 0; s < components.length; s++) {
            for (int c = 0; c < components[s].length; c++) {
                totalCost+= components[s][c].getCost();
                totalWeight+= components[s][c].getWeight();
                cc++;
            }
        }
        averageComponentCost = (double)totalCost/(double)cc;
        averageComponentWeight = (double)totalWeight/(double)cc;
    }

    @Override
    public boolean isFeasible(Representation i) {
        IntegerMatrix im = ((IntegerMatrix) i);
        int[][] system = im.getMatrix();
        int subSystemCount = im.getRowCount();

        for (int s = 0; s < subSystemCount; s++) {
            int componentCount = componentCountOf(system,s);
            if (componentCount>Nmax)
                return false;
            if (componentCount<k[s])
                return false;
        }

        if (costOf(system)>C)
            return false;
        if (weightOf(system)>W)
            return false;

        return true;
    }

    public double reliabilityOf(int subSystemIndex,int component, int count)
    {
        double r =Math.pow((1-components[subSystemIndex][component].getReliability()),count);
        return 1-r;
    }

    public double reliabilityOf(int[][] system)
    {
        int subSystemCount= system.length;
        double r = 1;

        for (int s = 0; s < subSystemCount; s++) {
            r *= reliabilityOf(s,system[s][0],system[s][1]);
        }
        return r;
    }



    public double costOf(int[][] system)
    {
        int subSystemCount = system.length;
        double cost =0;

        for (int s = 0; s < subSystemCount; s++) {
            cost+= components[s][system[s][0]].getCost()*system[s][1];
        }
        return cost;
    }

    public double weightOf(int[][] system)
    {
        int subSystemCount = system.length;
        double weight =0;

        for (int s = 0; s < subSystemCount; s++) {
            weight+= components[s][system[s][0]].getWeight()*system[s][1];
        }
        return weight;
    }

    @Override
    public double cost(Representation i) {
        RAPSSolution rs = (RAPSSolution) i ;

        double result=0;

        if (rs.getSystemCost()>C)
            result+= rs.getSystemCost()-C;

        if (rs.getSystemWeight()>W)
            result += rs.getSystemWeight()-W;

        int componentCountPenalty = 0;
        for (int s = 0; s < components.length; s++) {
            int cc = rs.componentCountOf(s);
            if (cc<k[s] || cc>Nmax)
                componentCountPenalty++;
        }
        result+= componentCountPenalty;
        return result+(1-rs.getSystemR());
    }

     @Override
    public double maxDistance() {
        return 0;
    }

    public int componentCountOf(int[][] matrix, int subSystem) {
        return Arrays.stream(matrix[subSystem]).sum();
    }

    public int componentTypeCount(int subSystem) {
        return components[subSystem].length;
    }

    public int subSystemCount() {
        return components.length;
    }

    public int getNmax() {
        return Nmax;
    }


    public int getC() {
        return C;
    }

    public int getW() {
        return W;
    }

    public Component getComponent(int s, int c) {
        return components[s][c];
    }

    public double averageComponentCost() {
        return averageComponentCost;
    }

    public double averageComponentWeight() {
        return averageComponentWeight;
    }

    public int getCostliest(RAPSSolution rs) {
        int costliestIndex=0;
        double costliest =  components[0][rs.componentOf(0)].getCost();
        for (int s = 1; s <rs.subSystemCount() ; s++) {
            Component c= components[s][rs.componentOf(s)];
            if (c.getCost()>costliest)
            {
                costliestIndex = s;
                costliest =  c.getWeight();
            }
        }
        return costliestIndex;
    }

    public int getHeaviest(RAPSSolution rs) {
        int heaviestIndex=0;
        double heaviest =  components[0][rs.componentOf(0)].getWeight();
        for (int s = 1; s <rs.subSystemCount() ; s++) {
            Component c= components[s][rs.componentOf(s)];
            if (c.getWeight()>heaviest)
            {
                heaviestIndex = s;
                heaviest =  c.getWeight();
            }
        }
        return heaviestIndex;
    }
}
