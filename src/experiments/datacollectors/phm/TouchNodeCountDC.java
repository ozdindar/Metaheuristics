package experiments.datacollectors.phm;

import experiments.datacollectors.AverageDC;
import metrics.populationheatmap.PHM;
import metrics.populationheatmap.PHMNode;

import java.util.Collection;
import java.util.function.Predicate;

public class TouchNodeCountDC extends AverageDC<PHM> {

    final Predicate<PHMNode> filter;

    public TouchNodeCountDC()
    {
        this(x->true);
    }

    public TouchNodeCountDC(Predicate<PHMNode> filter) {
        this.filter = filter;
    }

    @Override
    protected double _collect(PHM phm) {
        Collection<PHMNode> nodes = phm.getNodes(filter);
        return nodes.size();
    }

    @Override
    protected String title() {
        return "PHM-TNC";
    }
}
