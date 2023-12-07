package search.astar;

import representation.base.Representation;
import search.SearchAlgorithm;
import search.SearchNode;
import search.SearchProblem;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by dindar.oz on 04.06.2015.
 */
public class AstarSearchAlgorithm implements SearchAlgorithm {

    SearchHeuristic h;
    HashMap<Representation,Double> visitedNodes = null;
    PriorityQueue<AstarNode> fringe;

    public AstarSearchAlgorithm(SearchHeuristic h) {
        this.h = h;
    }

    @Override
    public SearchNode solve(SearchProblem p )
    {
        fringe = new PriorityQueue<AstarNode>();
        visitedNodes = new HashMap<Representation, Double>();

        SearchNode initialNode  = p.getInitialNode();
        fringe.add(new AstarNode(initialNode,initialNode.getDistanceToStart()+ h.estimate(p,initialNode.getState())));

        visitedNodes.put(initialNode.getState(),initialNode.getDistanceToStart()+ h.estimate(p,initialNode.getState()));

        while (true)
        {
            if (fringe.isEmpty())
                return null;

            AstarNode node= selectFrom(fringe);
            if (p.isTarget(node.getSearchNode()))
                return node.getSearchNode();

            List<SearchNode> successors = p.expand(node.getSearchNode());
            for (SearchNode successor:successors)
            {
                double newF= successor.getDistanceToStart()+h.estimate(p,successor.getState());
                if (visitedNodes.containsKey(successor.getState()))
                {
                    if ( (newF)<visitedNodes.get(successor.getState())) {
                        visitedNodes.remove(successor.getState());
                    }
                    else
                        continue;
                }

                fringe.add(new AstarNode(successor,newF));
                visitedNodes.put(successor.getState(),newF);
            }
        }
    }

    private AstarNode selectFrom(PriorityQueue<AstarNode> fringe) {
        AstarNode n  = fringe.poll();
        return n;
    }


    public static void main(String args[])
    {

    }
}
