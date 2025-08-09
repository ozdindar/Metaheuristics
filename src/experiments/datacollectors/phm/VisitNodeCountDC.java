package experiments.datacollectors.phm;

import experiments.datacollectors.AverageDC;
import metrics.populationheatmap.PHM;
import metrics.populationheatmap.PHMNode;

import java.util.Collection;
import java.util.function.Predicate;

public class VisitNodeCountDC extends AverageDC<PHM> {

    final Predicate<PHMNode> filter;

    public VisitNodeCountDC()
    {
        this(x->true);
    }

    public VisitNodeCountDC(Predicate<PHMNode> filter) {
        this.filter = filter;
    }

    @Override
    protected double _collect(PHM phm) {

        Collection<PHMNode> nodes = phm.getNodes(filter.and(x->x.getVisitCount()>0));
        return nodes.size();
    }

    @Override
    protected String title() {
        return "PHM-VNC";
    }
}
