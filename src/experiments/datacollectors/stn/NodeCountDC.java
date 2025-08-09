package experiments.datacollectors.stn;

import experiments.datacollectors.AverageDC;
import metrics.stn.STN;
import metrics.stn.STNNode;

import java.util.Collection;
import java.util.function.Predicate;

public class NodeCountDC extends AverageDC<STN> {

    final Predicate<STNNode> filter;

    public NodeCountDC()
    {
        this(x->true);
    }

    public NodeCountDC(Predicate<STNNode> filter) {
        this.filter = filter;
    }

    @Override
    protected double _collect(STN stn) {
        Collection<STNNode> nodes = stn.getNodes(filter);
        return nodes.size();
    }

    @Override
    protected String title() {
        return "NC";
    }
}
