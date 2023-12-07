package problems.rap;

import base.OptimizationProblem;
import representation.IntegerMatrix;
import representation.base.Representation;

import java.util.Arrays;

/**
 * Created by dindar.oz on 6/23/2017.
 */
public class RAP implements OptimizationProblem {

    int C; //  cost limit;
    int W; // weight limit;
    int Nmax; // # of components;

    Component components[][];

    int k[]; // min # of components required in subsytem i
    private double averageComponentCost;
    private double averageComponentWeight;

    public RAP(int c, int w, int nmax, Component[][] components, int[] k) {
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

    public double reliabilityOf(int subSystemIndex,int[] subSystem)
    {
        double r =1;

        int componentCount = subSystem.length;
        for (int c = 0; c < componentCount; c++) {

            try {
                r *= Math.pow((1-components[subSystemIndex][c].getReliability()),subSystem[c]);
            } catch (Exception e) {
                System.out.println(subSystemIndex+"-"+c+"-"+subSystem.length);;
            }
        }

        return 1-r;
    }

    public double reliabilityOf(int[][] system)
    {
        int subSystemCount= system.length;
        double r = 1;

        for (int s = 0; s < subSystemCount; s++) {
            r *= reliabilityOf(s,system[s]);
        }
        return r;
    }



    public double costOf(int[][] system)
    {
        int subSystemCount = system.length;
        double cost =0;

        for (int s = 0; s < subSystemCount; s++) {
            int componentCount = system[s].length;
            for (int c = 0; c < componentCount; c++) {
                cost += components[s][c].getCost()*system[s][c];
            }
        }
        return cost;
    }

    public double weightOf(int[][] system)
    {
        int subSystemCount = system.length;
        double weight =0;

        for (int s = 0; s < subSystemCount; s++) {
            int componentCount = system[s].length;
            for (int c = 0; c < componentCount; c++) {
                weight += components[s][c].getWeight()*system[s][c];
            }
        }
        return weight;
    }

    @Override
    public double cost(Representation i) {
        RAPSolution rs = (RAPSolution) i ;

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

    public double deltaCost(double cost, int system[][], int subSystem, int component, int newCount)
    {
        double oldR= Math.ceil(cost)- cost;
        double oldCost = cost-(1-oldR);
        int deltaComponent= newCount- system[subSystem][component];
        Component comp =  components[subSystem][component];
        double deltaCost = (comp.cost+comp.weight)* (deltaComponent);


        if (deltaComponent>0 )
        {
            int cc = componentCountOf(system,subSystem);
            if (cc>Nmax && (cc-deltaComponent<=Nmax))
                deltaCost += 1; // CC penalty
        }

        double oldRsub = reliabilityOf(subSystem,system[subSystem]);
        double newRsub = 1- (1-oldRsub)*Math.pow((1-comp.getReliability()),deltaComponent);

        double newR= oldR*newRsub/oldRsub;

        double newCost = oldCost+deltaCost+(1-newR);

        return newCost-cost;

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
}
