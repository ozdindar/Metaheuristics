package experiments.datacollectors;

import experiments.ExperimentCase;
import metrics.DataCollector;

public abstract class AverageDC<T> implements DataCollector<T> {
    double sum;
    int repeat;
    @Override
    public void collect(T data) {
        repeat++;
        sum+= _collect(data);
    }

    protected abstract double _collect(T data);
    protected abstract String title();

    @Override
    public void init(ExperimentCase experimentCase) {
        repeat =0;
        sum=0.0;
    }

    @Override
    public String resultAsString() {
        return title() +":"+ (repeat == 0 ? "INF": ""+ String.format("%.4f",sum/repeat));
    }
}
