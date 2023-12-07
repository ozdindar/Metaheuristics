package search.example;

import representation.IntegerPermutation;
import search.SearchNode;
import search.SearchProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 04.06.2015.
 */
public class NBlockProblem implements SearchProblem{

    IntegerPermutation initialState;
    IntegerPermutation targetState;

    int boardSize;

    public NBlockProblem(IntegerPermutation initialState, IntegerPermutation targetState) {
        this.initialState = initialState;
        this.targetState = targetState;
        boardSize = (int) Math.sqrt(initialState.getLength());
    }

    @Override
    public SearchNode getInitialNode() {
        return new SearchNode(null,0,initialState);
    }

    @Override
    public boolean isTarget(SearchNode node) {
        return node.getState().equals(targetState);
    }

    @Override
    public List<SearchNode> expand(SearchNode node) {

        List<IntegerPermutation> sList = generateSuccessors((IntegerPermutation)node.getState());

        List<SearchNode> successors = new ArrayList<SearchNode>();

        for (IntegerPermutation p:sList)
        {
            successors.add(new SearchNode(node,node.getDistanceToStart()+1,p));
        }

        return successors;
    }

    int getIndex(int x, int y)
    {
        return x+y*boardSize;
    }

    int getBoardValue(IntegerPermutation state, int x, int y )
    {
        return state.getValues()[getIndex(x,y)];
    }

    void setBoardValue(IntegerPermutation state, int x, int y , int v)
    {
        state.getValues()[getIndex(x,y)]= v;
    }

    int boardIndexX(int index)
    {
        return index % boardSize;
    }

    int boardIndexY(int index)
    {
        return index / boardSize;
    }

    private List<IntegerPermutation> generateSuccessors(IntegerPermutation state)
    {
        List<IntegerPermutation> sList = new ArrayList<IntegerPermutation>();

        int emptyIndex = state.firstOf(0);
        int emptyX= boardIndexX(emptyIndex);
        int emptyY= boardIndexY(emptyIndex);

        if (emptyX>0)
        {
            IntegerPermutation s=(IntegerPermutation) state.clone();
            s.swap(getIndex(emptyX-1,emptyY),getIndex(emptyX,emptyY));
            sList.add(s);
        }

        if (emptyY>0)
        {
            IntegerPermutation s=(IntegerPermutation) state.clone();
            s.swap(getIndex(emptyX,emptyY-1),getIndex(emptyX,emptyY));
            sList.add(s);
        }

        if (emptyX<boardSize-1)
        {
            IntegerPermutation s=(IntegerPermutation) state.clone();
            s.swap(getIndex(emptyX+1,emptyY),getIndex(emptyX,emptyY));
            sList.add(s);
        }

        if (emptyY<boardSize-1)
        {
            IntegerPermutation s=(IntegerPermutation) state.clone();
            s.swap(getIndex(emptyX,emptyY+1),getIndex(emptyX,emptyY));
            sList.add(s);
        }


        return sList;
    }

    String printBoard(IntegerPermutation state)
    {
        String st = "";
        for (int y=0;y<boardSize;y++) {
            for (int x = 0; x < boardSize; x++)
            {
                st += getBoardValue(state,x,y)+ " ";
            }
            st+="\n";
        }
        return st;
    }

    String printSolution(SearchNode node)
    {
        if (node ==null)
            return "";

        String st = "";

        st += printSolution(node.getParent());

        st += "\n\n "+(int)node.getDistanceToStart()+"\n" + printBoard((IntegerPermutation)node.getState());

        return st;
    }

}
