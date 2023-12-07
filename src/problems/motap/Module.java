package problems.motap;

import java.util.Arrays;

/**
 * Created by oz on 16.07.2015.
 */
public class Module implements IModule {

    private String moduleName="";
    int executionTimes[] =null;
    private int computationalResourceRequirement;
    private int memoryRequirement;


    public Module(String moduleName,int[] executionTimes, int computationalResourceRequirement, int memoryRequirement) {
        this.executionTimes = executionTimes;
        this.computationalResourceRequirement = computationalResourceRequirement;
        this.memoryRequirement = memoryRequirement;
        this.moduleName = moduleName;
    }


    public Module(int[] executionTimes, int computationalResourceRequirement, int memoryRequirement) {
        this.executionTimes = executionTimes;
        this.computationalResourceRequirement = computationalResourceRequirement;
        this.memoryRequirement = memoryRequirement;
    }

    @Override
    public int[] getAETs() {
        return executionTimes;
    }

    @Override
    public int getAET(int p) {
        return executionTimes[p];
    }

    @Override
    public int getCRR() {
        return computationalResourceRequirement;
    }

    @Override
    public int getMemoryRequirement() {
        return memoryRequirement;
    }


    @Override
    public String toString() {
        return moduleName+"["+ Arrays.toString(executionTimes)+ " " +memoryRequirement+"//"+computationalResourceRequirement+"]";
    }
}
