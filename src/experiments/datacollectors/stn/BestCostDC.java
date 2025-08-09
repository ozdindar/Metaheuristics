package experiments.datacollectors.stn;

import experiments.datacollectors.AverageDC;
import metrics.stn.STN;

public class BestCostDC extends AverageDC<STN> {


    @Override
    protected double _collect(STN stn) {
        return stn.getBestCost();
    }

    @Override
    protected String title() {
        return "BC";
    }
}
