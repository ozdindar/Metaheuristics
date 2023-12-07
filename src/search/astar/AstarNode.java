package search.astar;

import search.SearchNode;

/**
 * Created by dindar.oz on 04.06.2015.
 */
public class AstarNode implements Comparable<AstarNode>{

    SearchNode searchNode;
    double fValue;

    public SearchNode getSearchNode() {
        return searchNode;
    }

    public double getfValue() {
        return fValue;
    }

    public AstarNode(SearchNode searchNode, double fValue) {
        this.searchNode = searchNode;
        this.fValue = fValue;
    }


    @Override
    public int compareTo(AstarNode o) {
        return new Double(fValue).compareTo(new Double(o.fValue));
    }
}
