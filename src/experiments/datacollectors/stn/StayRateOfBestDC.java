package experiments.datacollectors.stn;

import experiments.datacollectors.AverageDC;
import metrics.stn.STN;
import metrics.stn.STNNode;

import java.util.Collection;
import java.util.Comparator;

public class StayRateOfBestDC extends AverageDC<STN> {

    @Override
    protected double _collect(STN stn) {
        Collection<STNNode> nodes = stn.getNodes();

        long stay =  nodes.stream().max(Comparator.comparingDouble(STNNode::getbCost).reversed()).get().getTotalStay();

        return (double)stay/stn.iteration() ;
    }

    @Override
    protected String title() {
        return "StayRateBest";
    }
}
