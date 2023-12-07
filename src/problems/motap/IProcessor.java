package problems.motap;

/**
 * Created by dindar.oz on 15.07.2015.
 */
public interface IProcessor extends IHardwareUnit {
    int getTotalMemory();
    int getTotalComputationalResource();
    int getExecutionCost();
}
