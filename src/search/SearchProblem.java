package search;

import java.util.List;

/**
 * Created by dindar.oz on 04.06.2015.
 */
public interface SearchProblem {

    SearchNode getInitialNode();
    boolean isTarget(SearchNode state);
    List<SearchNode> expand(SearchNode state);


}
