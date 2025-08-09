package experiments.datacollectors.stn;

import experiments.datacollectors.AverageDC;
import metrics.stn.STN;
import metrics.stn.STNNode;

import java.util.Collection;

public class StayRateDC extends AverageDC<STN> {

    @Override
    protected double _collect(STN stn) {
        Collection<STNNode> nodes = stn.getNodes();

        long total =  nodes.stream().mapToLong(STNNode::getTotalStay).sum();

        return (double)total/stn.iteration() ;
    }

    @Override
    protected String title() {
        return "StayRate";
    }
}
