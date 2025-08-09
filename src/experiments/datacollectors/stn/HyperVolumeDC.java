package experiments.datacollectors.stn;

import experiments.datacollectors.AverageDC;
import metrics.HyperVolumeUtils;
import metrics.stn.STN;
import metrics.stn.STNNode;
import metrics.stn.STNUtils;

import java.util.Collection;
import java.util.function.Predicate;

public class HyperVolumeDC extends AverageDC<STN> {

    final Predicate<STNNode> filter;
    final int  dimension;

    public HyperVolumeDC(int dimension) {
        this(x->true,dimension);
    }

    public HyperVolumeDC(Predicate<STNNode> filter, int dimension) {
        this.filter = filter;
        this.dimension= dimension;
    }

    @Override
    protected double _collect(STN stn) {
        Collection<STNNode> nodes = stn.getNodes(filter);
        if (nodes.isEmpty())
            return 0.0;

        double[][] hrect = STNUtils.boundingHyperRectangle(nodes,dimension);
        double hv = HyperVolumeUtils.calculateVolume(hrect);


        return hv;
    }

    @Override
    protected String title() {
        return "HV";
    }
}
