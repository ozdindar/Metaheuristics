package problems.pcb;


import base.OptimizationProblem;
import metaheuristic.pso.base.PSOProblem;
import representation.IntegerPermutation;
import representation.base.Representation;

import java.util.HashMap;

public class PCBProblem implements OptimizationProblem,PSOProblem {

    private PCBData data;
    private int k;

    public PCBProblem(PCBData data , int k)
    {
        this.data = data;
        this.k = k;
    }

    public PCBData getData() {
        return data;
    }

    public int getN(){
        return data.getN();
    }

    public HashMap<Integer, Component> getComponentMap(){
        return data.getComponentMap();
    }

    public void setK(int k){
        this.k = k;
    }

    public int getK(){
        return k;
    }


    @Override
    public boolean isFeasible(Representation i) {
        return false;
    }

    @Override
    public double cost(Representation r)
    {
        double ret = 0.0, maxTurrTime = Double.MIN_VALUE;

        IntegerPermutation ip = (IntegerPermutation) r;

        HashMap<Integer, Component> cMap = getComponentMap();

        Integer values[] = ip.getValues();
        int nList = values.length;
        Component c, c1;

        int k = getK();

        //System.out.println("k: " + k);

        for(int i=0; i<nList; i++){
            c1 = cMap.get(values[i]);

            maxTurrTime = Double.MIN_VALUE;

            for(int j=1; j<=k; j++){
                c = cMap.get(values[(i+j)%nList]);
                //System.out.println("component to: " + c);
                if(c.getTurretTime() > maxTurrTime)
                    maxTurrTime = c.getTurretTime();
            }


            ret += Math.max(c1.getNeighbourDistances()[values[(i+1)%nList] - 1]/PCBData.boardCarrierSpeed, maxTurrTime);
        }

        return ret;
    }

    @Override
    public double maxDistance() {
        return data.getN();
    }

    @Override
    public int getDimensionCount() {
        return getN();
    }

    @Override
    public double getUpperBound() {
        return getN();
    }

    @Override
    public double getLowerBound() {
        return 0;
    }
}
