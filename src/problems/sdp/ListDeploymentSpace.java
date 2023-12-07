package problems.sdp;

import java.util.List;

/**
 * Created by dindar.oz on 17.05.2017.
 */
public class ListDeploymentSpace implements DeploymentSpace {

    List<Deployment> deploymentList;

    @Override
    public int size() {
        return deploymentList.size();
    }

    @Override
    public Deployment get(int index) {
        return deploymentList.get(index);
    }
}
