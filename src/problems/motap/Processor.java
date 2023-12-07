package problems.motap;

/**
 * Created by oz on 16.07.2015.
 */
public class Processor implements IProcessor {
    private double failureRate;
    private int totalMemory;
    private int totalComputationalResource;
    private int executionCost;
    private String processorName= "";

    public Processor(String processorName,double failureRate, int totalMemory, int totalComputationalResource, int executionCost) {
        this.failureRate = failureRate;
        this.totalMemory = totalMemory;
        this.totalComputationalResource = totalComputationalResource;
        this.executionCost = executionCost;
        this.processorName=processorName;
    }


    public Processor(double failureRate, int totalMemory, int totalComputationalResource, int executionCost) {
        this.failureRate = failureRate;
        this.totalMemory = totalMemory;
        this.totalComputationalResource = totalComputationalResource;
        this.executionCost = executionCost;
    }

    @Override
    public int getTotalMemory() {
        return totalMemory;
    }

    @Override
    public int getTotalComputationalResource() {
        return totalComputationalResource;
    }

    @Override
    public int getExecutionCost() {
        return executionCost;
    }

    @Override
    public double getFailureRate() {
        return failureRate;
    }

    @Override
    public String toString() {
        return processorName +"["+getExecutionCost()+" "+getTotalMemory()+ " " +getTotalComputationalResource()+"]";
    }
}
