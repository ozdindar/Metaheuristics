package problems.raps;

import representation.base.Representation;
import util.ArrayUtil;

import java.util.Arrays;

/**
 * Created by dindar.oz on 7/4/2017.
 */
public class RAPSSolution implements Representation {
    private static final int COMPONENTTYPE = 0;
    private static final int COMPONENTCOUNT = 1;
    int systemCost;
    int systemWeight;
    double systemR;
    int system[][];
    double reliabilities[];



    public RAPSSolution(int systemCost, int systemWeight, double systemR, int[][] system, double reliabilities[]) {
        this.systemCost = systemCost;
        this.systemWeight = systemWeight;
        this.systemR = systemR;
        this.system = system;
        this.reliabilities = reliabilities;


    }

    public RAPSSolution(RAPS rap, int[][] system) {
        this.system = system;

        systemCost = (int) rap.costOf(system);
        systemWeight = (int) rap.weightOf(system);
        reliabilities = new double[system.length];
        systemR =1;
        for (int s = 0; s < system.length; s++) {
            reliabilities[s] = rap.reliabilityOf(s,system[s][0],system[s][1]);
            systemR *= reliabilities[s];
        }
    }

    @Override
    public Representation clone() {
        return new RAPSSolution(systemCost,systemWeight,systemR,ArrayUtil.cloneArray(system),Arrays.copyOf(reliabilities,reliabilities.length));
    }

    @Override
    public double distanceTo(Representation r) {
        return 0;
    }

    public int getSystemCost() {
        return systemCost;
    }

    public int getSystemWeight() {
        return systemWeight;
    }

    public int componentCountOf(int s) {
        return system[s][COMPONENTCOUNT];
    }

    public double getSystemR() {
        return systemR;
    }

    public int subSystemCount() {
        return system.length;
    }


    public void update(RAPS rap, int subSystem, int component, int count) {
        int oldComponent = system[subSystem][COMPONENTTYPE];
        int oldCount = system[subSystem][COMPONENTCOUNT];
        //if (newCount==oldCount)
        //    return;
        Component oldC = rap.getComponent(subSystem,oldComponent);
        Component newC = rap.getComponent(subSystem,component);


        systemCost = (int) (systemCost- oldC.getCost()*oldCount+newC.getCost()*count);
        systemWeight = (int) (systemWeight - oldC.getWeight()*oldCount+newC.getWeight()*count);



        double oldRsub = rap.reliabilityOf(subSystem,oldComponent,oldCount);
        double newRsub = rap.reliabilityOf(subSystem,component,count);

        reliabilities[subSystem]= newRsub;
        if (oldRsub > 0)
            systemR = systemR*newRsub/oldRsub;
        else systemR = recalculateReliability();

        system[subSystem][COMPONENTTYPE]= component;
        system[subSystem][COMPONENTCOUNT]= count;
    }

    private double recalculateReliability() {
        double res =1;
        for (int s = 0; s < reliabilities.length; s++) {
            res *= reliabilities[s];
            if (res==0)
                return 0;
        }
        return res;
    }

    @Override
    public String toString() {
        return  "Cost=" + systemCost +
                "  Weight=" + systemWeight +
                "  R=" + systemR +
                ", system=" + Arrays.deepToString(system);
    }

    public int[][] getSystem() {
        return system;
    }

    public int componentOf(int subSystem) {
        return system[subSystem][COMPONENTTYPE];
    }

    public double[] getReliabilities() {
        return reliabilities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RAPSSolution that = (RAPSSolution) o;

        return Arrays.deepEquals(system, that.system);

    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(system);
    }
}
