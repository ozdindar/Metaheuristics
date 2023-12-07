package problems.motap;


import representation.IntegerAssignment;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dindar.oz on 15.07.2015.
 */
public class DCS implements Serializable {

    List<IProcessor> processors;

    // Processor Interaction Graph
    private ICommunicationLink[][] pig;
    private double MemoryViolationFactor= 1000;
    private double ComputationViolationFactor= 1000;

    public List<IProcessor> getProcessors() {
        return processors;
    }

    public void setProcessors(List<IProcessor> processors) {
        this.processors = processors;
    }


    public void setPig(ICommunicationLink[][]pig) {
        this.pig = pig;
    }

    public double getMemoryViolationFactor() {
        return MemoryViolationFactor;
    }

    public void setMemoryViolationFactor(double memoryViolationFactor) {
        MemoryViolationFactor = memoryViolationFactor;
    }

    public double getComputationViolationFactor() {
        return ComputationViolationFactor;
    }

    public void setComputationViolationFactor(double computationViolationFactor) {
        ComputationViolationFactor = computationViolationFactor;
    }

    ICommunicationLink getLink(int p1, int p2)
    {
        if (p1==p2)
            return CommunicationLink.FreeLink;
        ICommunicationLink link = pig[p1][p2];
        link = (link==null) ? CommunicationLink.NullLink:link;
        return link;
    }

    int processTimeOf(int processor , Task t, IntegerAssignment allocation)
    {
        int elapsed =0;
        for (int i=0 ; i< t.getModules().size();i++)
        {
            elapsed += (allocation.get(i)==processor)? t.getModules().get(i).getAET(processor):0;
        }
        return elapsed;
    }

    int executionCostOf(int processor , Task task, IntegerAssignment allocation)
    {
        return processors.get(processor).getExecutionCost()*processTimeOf(processor, task, allocation);
    }

    int executionCostOf(Task task, IntegerAssignment allocation)
    {
        int totalCost=0;
        for (int processor=0;processor<processors.size();processor++)
        {
            totalCost += executionCostOf(processor,task,allocation);
        }
        return totalCost;
    }

    double communicationTimeOf(int processor1, int processor2, Task t, IntegerAssignment allocation)
    {
        double elapsed =0;
        ICommunicationLink link = getLink(processor1,processor2);

        for (int i=0 ; i< t.getModuleCount()-1;i++)
        {
            for (int j=i+1 ; j< t.getModuleCount();j++) {

                int c_ij = t.getCommunication(i,j);
                if ( ( allocation.get(i)==processor1 && allocation.get(j)==processor2) ||
                     ( allocation.get(i)==processor2 && allocation.get(j)==processor1))

                    elapsed +=  (double)c_ij/(double)link.getTransmissionRate();
            }
        }

        return elapsed;
    }

    double communicationCostOf(Task task, IntegerAssignment allocation)
    {
        double totalCost=0;

        for (int m1 =0;m1<task.getModuleCount();m1++)
        {
            for (int m2 = m1+1;m2<task.getModuleCount(); m2++)
            {
                ICommunicationLink link = getLink(allocation.get(m1),allocation.get(m2));
                double communicationAmount = task.getCommunication(m1,m2);
                double communicationTime = ( communicationAmount / link.getTransmissionRate());
                totalCost +=  link.getCommunicationCost()* communicationTime;
            }
        }

        return totalCost;
    }

    double systemCostOf(Task task, IntegerAssignment allocation)
    {
        return executionCostOf(task,allocation) + communicationCostOf(task,allocation);
    }

    double processReliabilityCostOf(int processor, Task task, IntegerAssignment allocation)
    {
        return processors.get(processor).getFailureRate()*processTimeOf(processor,task,allocation);
    }

    public double processReliabilityCostOf(Task task, IntegerAssignment allocation)
    {
        double r=0.0;
        for (int processor=0;processor<processors.size();processor++)
        {
            r += processReliabilityCostOf(processor, task, allocation);
        }
        return r;
    }


    public double communicationReliabilityCostOf(Task task, IntegerAssignment allocation)
    {
        double r=0.0;

        for (int m1 =0;m1<task.getModuleCount()-1;m1++)
        {
            for (int m2 = m1+1;m2<task.getModuleCount(); m2++)
            {
                ICommunicationLink link = getLink(allocation.get(m1),allocation.get(m2));
                double communicationAmount = task.getCommunication(m1,m2);
                double communicationTime = ( communicationAmount / link.getTransmissionRate());
                r+=  link.getFailureRate()* communicationTime;
            }
        }

        return r;
    }


    double reliabilityCostOf(Task task, IntegerAssignment allocation)
    {
        return processReliabilityCostOf(task,allocation)+ communicationReliabilityCostOf(task,allocation);
    }

    double reliabilityOf(Task task, IntegerAssignment allocation) {
        return Math.exp(-1*reliabilityCostOf(task,allocation));

    }

    public int memoryUsageOf(int processor, Task task, IntegerAssignment allocation)
    {
        int memoryUsage=0;
        for (int i=0;i<task.getModules().size();i++)
        {
            if (allocation.get(i)==processor)
                memoryUsage += task.getModules().get(i).getMemoryRequirement();
        }
        return memoryUsage;
    }

    public int computationUsageOf(int processor, Task task, IntegerAssignment allocation)
    {
        int memoryUsage=0;
        for (int i=0;i<task.getModules().size();i++)
        {
            if (allocation.get(i)==processor)
                memoryUsage += task.getModules().get(i).getCRR();
        }
        return memoryUsage;
    }

    double infeasibilityFactorOf(Task task, IntegerAssignment allocation)
    {
        double totalMemoryViolation =0;
        double totalComputationViolation =0;

        for (int k=0;k<processors.size();k++)
        {
            totalMemoryViolation += Math.max(0,memoryUsageOf(k,task,allocation)-processors.get(k).getTotalMemory());
            totalComputationViolation += Math.max(0,computationUsageOf(k, task, allocation)-processors.get(k).getTotalComputationalResource());
        }

        return MemoryViolationFactor*totalMemoryViolation + ComputationViolationFactor*totalComputationViolation;
    }

    double communication_cost_of(int p1, int p2, int m1, int m2, Task task)
    {
        if(p1==p2)
            return 0;

        return  (double)pig[p1][p2].getCommunicationCost()*((double)task.getCommunication(m1,m2)/(double)pig[p1][p2].getTransmissionRate());
    }

    double execution_cost_of(int p, int m, Task task)
    {
        return  task.getModules().get(m).getAET(p)*processors.get(p).getExecutionCost();
    }


    double costDifference(Task task, int m, int p1, int p2, IntegerAssignment allocation, double costFactor, double reliabilityFactor )
    {
        double execDifference = task.getModules().get(m).getAET(p2)*processors.get(p2).getExecutionCost() - task.getModules().get(m).getAET(p1)*processors.get(p1).getExecutionCost();
        double processReliabilityDifference = task.getModules().get(m).getAET(p2)*processors.get(p2).getFailureRate() - task.getModules().get(m).getAET(p1)*processors.get(p1).getFailureRate();

        double commDifference =0;
        double commReliabilityDifference =0;

        IntegerAssignment allocation2 = (IntegerAssignment)allocation.clone();
        allocation2.set(m,p2);

        for (int m2 =0;m2<task.getModules().size();m2++)
        {
            if (m2 == m )
                continue;

            double communicationAmount = (double)task.getCommunication(m,m2);

            int pOther = allocation.get(m2);

            ICommunicationLink link1 = getLink(p1,pOther);
            ICommunicationLink link2 = getLink(p2,pOther);
            double communicationTime1 = ( communicationAmount / link1.getTransmissionRate());
            double communicationTime2 = ( communicationAmount / link2.getTransmissionRate());
            commDifference += (link2.getCommunicationCost() * communicationTime2 )  - ( link1.getCommunicationCost() * communicationTime1);

            commReliabilityDifference+= (link2.getFailureRate() * communicationTime2 ) - (link1.getFailureRate() * communicationTime1 );

        }

        /**/
        double memoryInfeasibilityDifference =0;
        double memoryInfeasibility1 = memoryUsageOf(p1,task,allocation)-processors.get(p1).getTotalMemory();
        if (memoryInfeasibility1>0)
        {
            memoryInfeasibilityDifference -= Math.min(memoryInfeasibility1,task.getModule(m).getMemoryRequirement())*MemoryViolationFactor;
        }
        double memoryInfeasibility2 = memoryUsageOf(p2,task,allocation)-processors.get(p2).getTotalMemory();
        if (memoryInfeasibility2>0)
        {
            memoryInfeasibilityDifference += task.getModule(m).getMemoryRequirement()*MemoryViolationFactor;
        }
        else{
            memoryInfeasibility2 = task.getModule(m).getMemoryRequirement() + memoryInfeasibility2;
            if (memoryInfeasibility2>0)
                memoryInfeasibilityDifference += memoryInfeasibility2*MemoryViolationFactor;
        }
        /**/

        /**/
        double compInfeasibilityDifference =0;
        double compInfeasibility1 = computationUsageOf(p1,task,allocation)-processors.get(p1).getTotalComputationalResource();
        if (compInfeasibility1>0)
        {
            compInfeasibilityDifference -= Math.min(compInfeasibility1,task.getModule(m).getCRR())*ComputationViolationFactor;
        }
        double compInfeasibility2 = computationUsageOf(p2,task,allocation)-processors.get(p2).getTotalComputationalResource();
        if (compInfeasibility2>0)
        {
            compInfeasibilityDifference += task.getModule(m).getCRR()*ComputationViolationFactor;
        }
        else{
            compInfeasibility2 = task.getModule(m).getCRR() + compInfeasibility2;
            if (compInfeasibility2>0)
                compInfeasibilityDifference += compInfeasibility2*ComputationViolationFactor;
        }
        /**/
        //double memoryInfeasibilityDifference =  MemoryViolationFactor*Math.max(0,memoryUsageOf(p1,task,allocation)-task.getModules().get(m).getMemoryRequirement()-processors.get(p1).getTotalMemory())- Math.max(0,memoryUsageOf(p1,task,allocation)-processors.get(p1).getTotalMemory());
        //memoryInfeasibilityDifference += MemoryViolationFactor*Math.max(0,memoryUsageOf(p2,task,allocation)+task.getModules().get(m).getMemoryRequirement()-processors.get(p2).getTotalMemory())- Math.max(0,memoryUsageOf(p2,task,allocation) - processors.get(p2).getTotalMemory());

        //double compInfeasibilityDifference =  ComputationViolationFactor*Math.max(0,computationUsageOf(p1, task,allocation)-task.getModules().get(m).getCRR()-processors.get(p1).getTotalComputationalResource())- Math.max(0,computationUsageOf(p1,task,allocation) - processors.get(p1).getTotalComputationalResource());
        //compInfeasibilityDifference += ComputationViolationFactor*Math.max(0,computationUsageOf(p2, task,allocation)+task.getModules().get(m).getCRR()-processors.get(p2).getTotalComputationalResource())- Math.max(0,computationUsageOf(p2,task,allocation)-processors.get(p2).getTotalComputationalResource());


        double totalDifference= costFactor*(execDifference+commDifference) + reliabilityFactor*(processReliabilityDifference+ commReliabilityDifference) + memoryInfeasibilityDifference+ compInfeasibilityDifference;
        return totalDifference;
    }


    @Override
    public String toString() {
        String res="";
        for (int p =0;p<processors.size();p++)
        {
            IProcessor pr  = processors.get(p);
            res += pr+"\n";
        }
        return res;
    }


    public int getProcessorCount() {
        return processors.size();
    }
}
