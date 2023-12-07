package problems.motap.mutation.GRMR;

public class Reassignment {
    int taskId;
    int processor;
    double deltaCost;
    int oldProcessor;

    public Reassignment(int taskId, int processor, double deltaCost, int oldProcessor) {
        this.taskId = taskId;
        this.processor = processor;
        this.deltaCost = deltaCost;
        this.oldProcessor= oldProcessor;
    }

    public int getOldProcessor() { return oldProcessor; }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getProcessor() {
        return processor;
    }

    public void setProcessor(int processor) {
        this.processor = processor;
    }

    public double getDeltaCost() {
        return deltaCost;
    }

    public void setDeltaCost(double deltaCost) {
        this.deltaCost = deltaCost;
    }

    public String toString()
    {
        return taskId + ": "+  oldProcessor+ "->"+processor+ " | " + deltaCost;
    }
}
