package experiments.datacollectors.stn;

import experiments.ExperimentCase;

import metrics.DataCollector;
import metrics.stn.STN;
import metrics.stn.STNVisualizer;

public class STNImageSaverDC implements DataCollector<STN> {

    int imageIndex;
    String fileName;
    ExperimentCase experimentCase;

    public STNImageSaverDC(String fileName) {
        this.fileName= fileName;
    }

    @Override
    public void collect(STN stn) {
        imageIndex++;
        String path = fileName+experimentCase.getTitle()+imageIndex+".png";
        STNVisualizer.exportAsImage(stn,path);
    }

    @Override
    public void init(ExperimentCase experimentCase) {
        imageIndex=1;
        this.experimentCase = experimentCase;
    }

    @Override
    public String resultAsString() {
        return "";
    }
}
