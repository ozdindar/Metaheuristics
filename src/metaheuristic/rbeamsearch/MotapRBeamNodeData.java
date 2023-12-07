package metaheuristic.rbeamsearch;

public class MotapRBeamNodeData {
    int task;
    int oldProcessor;
    int newProcessor;

    public MotapRBeamNodeData(int task, int oldProcessor, int newProcessor) {
        this.task = task;
        this.oldProcessor = oldProcessor;
        this.newProcessor = newProcessor;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public int getOldProcessor() {
        return oldProcessor;
    }

    public void setOldProcessor(int oldProcessor) {
        this.oldProcessor = oldProcessor;
    }

    public int getNewProcessor() {
        return newProcessor;
    }

    public void setNewProcessor(int newProcessor) {
        this.newProcessor = newProcessor;
    }

    @Override
    public String toString() {
        return task + ": "+  oldProcessor+ "->"+newProcessor;
    }
}
