package search;

import representation.base.Representation;

/**
 * Created by dindar.oz on 04.06.2015.
 */
public class SearchNode {
    SearchNode parent;
    double distanceToStart;
    Representation state;

    public SearchNode getParent() {
        return parent;
    }

    public double getDistanceToStart() {
        return distanceToStart;
    }

    public Representation getState() {
        return state;
    }

    public SearchNode(SearchNode parent, double distanceToStart, Representation state) {
        this.parent = parent;
        this.distanceToStart = distanceToStart;
        this.state = state;
    }
}
