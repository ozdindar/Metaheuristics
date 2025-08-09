package experiments.datacollectors.stn;

import experiments.datacollectors.AverageDC;
import metrics.stn.STN;
import metrics.stn.STNNode;

import java.util.Collection;
import java.util.Comparator;

public class VisitRateMostDC extends AverageDC<STN> {

    @Override
    protected double _collect(STN stn) {
        Collection<STNNode> nodes = stn.getNodes();

        long visit =  nodes.stream().max(Comparator.comparingLong(STNNode::getVisitCount)).get().getVisitCount();

        return (double)visit/stn.iteration() ;
    }

    @Override
    protected String title() {
        return "VisitRateOfMost";
    }
}
