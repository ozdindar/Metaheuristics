package problems.rap;

import representation.base.Representation;
import util.ArrayUtil;

import java.util.Arrays;

/**
 * Created by dindar.oz on 7/4/2017.
 */
public class RAPSolution implements Representation {

    int systemCost;
    int systemWeight;
    double systemR;
    int system[][];
    int[] componentCounts;



    public RAPSolution(int systemCost, int systemWeight, double systemR, int[][] system,int[] componentCounts) {
        this.systemCost = systemCost;
        this.systemWeight = systemWeight;
        this.systemR = systemR;
        this.system = system;
        this.componentCounts = componentCounts ;
    }

    public RAPSolution(RAP rap, int[][] system) {
        this.system = system;

        systemCost = (int) rap.costOf(system);
        systemWeight = (int) rap.weightOf(system);
        systemR = rap.reliabilityOf(system);
        componentCounts = new int[system.length];
        for (int s = 0; s < system.length; s++) {
            componentCounts[s] = rap.componentCountOf(system,s);

        }
    }

    @Override
    public Representation clone() {
        return new RAPSolution(systemCost,systemWeight,systemR,ArrayUtil.cloneArray(system),Arrays.copyOf(componentCounts,componentCounts.length));
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
        return componentCounts[s];
    }

    public double getSystemR() {
        return systemR;
    }

    public int subSystemCount() {
        return system.length;
    }

    public int get(int subSystem, int component) {
        return system[subSystem][component];
    }

    public void update(RAP rap, int subSystem, int component, int newCount) {
        int oldCount = system[subSystem][component];
        //if (newCount==oldCount)
        //    return;
        Component c = rap.getComponent(subSystem,component);
        int deltaComponent = (newCount-oldCount);
        systemCost += deltaComponent*c.getCost();
        systemWeight += deltaComponent*c.weight;

        componentCounts[subSystem] += deltaComponent;

        double oldRsub = rap.reliabilityOf(subSystem,system[subSystem]);
        double newRsub = 1- (1-oldRsub)*Math.pow((1-c.getReliability()),deltaComponent);

        systemR = systemR*newRsub/oldRsub;

        system[subSystem][component]= newCount;
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
}
