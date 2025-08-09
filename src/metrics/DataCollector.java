package metrics;

import experiments.ExperimentCase;

public interface DataCollector<T> {
    void collect(T stn);
    String resultAsString();
    void init(ExperimentCase experimentCase);
}
