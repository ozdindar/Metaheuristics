package search.astar;

import representation.base.Representation;
import search.SearchProblem;

/**
 * Created by dindar.oz on 04.06.2015.
 */
public interface SearchHeuristic {
    double estimate(SearchProblem p,Representation state);
}
