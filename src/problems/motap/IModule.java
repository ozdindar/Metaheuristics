package problems.motap;

import java.io.Serializable;

/**
 * Created by dindar.oz on 15.07.2015.
 */
public interface IModule extends Serializable{
    int[] getAETs();
    int getAET(int p);
    int getCRR(); //Computational Resource Requirement
    int getMemoryRequirement();
}
