package experiments.datacollectors.stn;

import experiments.ExperimentCase;
import metrics.DataCollector;
import metrics.stn.STN;

public class ConstantDC implements DataCollector<STN> {

    double val;
    String title;

    public ConstantDC(String title,double val) {
        this.val = val;
        this.title= title;
    }

    @Override
    public void collect(STN stn) {
    }

    @Override
    public void init(ExperimentCase experimentCase) {

    }

    @Override
    public String resultAsString() {
        return title+ ":"+val;
    }
}
