package metaheuristic.sos;

import metaheuristic.IterationEvent;
import metaheuristic.MetaHeuristicListener;

/**
 * Created by dindar.oz on 25.06.2015.
 */
public class ConsolSOSListener implements MetaHeuristicListener {
    @Override
    public void onIterationEvent(IterationEvent event) {
        SOSIterationEvent sosEvent = (SOSIterationEvent) event;

        System.out.println("IC: "+sosEvent.getIterationCount()+"  NC: "+sosEvent.getNeighboringCount()+"  ChildCount: "+ sosEvent.childPopulationCount + "  Cost: "+ sosEvent.getBestCost() );
    }
}
