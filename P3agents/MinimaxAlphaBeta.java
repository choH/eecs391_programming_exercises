package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


public class MinimaxAlphaBeta extends Agent {

    private final int numPlys;

    private static final Comparator<GameStateChild> state_child_comptr = (c1, c2) -> {
    	if (c1.state.getUtility() > c2.state.getUtility()) {
    		return -1;
    	}
        else if (c2.state.getUtility() < c1.state.getUtility()) {
    		return 1;
    	}
        else {
    		return 0;
    	}
    };

    public MinimaxAlphaBeta(int playernum, String[] args)
    {
        super(playernum);

        if(args.length < 1)
        {
            System.err.println("You must specify the number of plys");
            System.exit(1);
        }

        numPlys = Integer.parseInt(args[0]);
    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView newstate, History.HistoryView statehistory) {
        return middleStep(newstate, statehistory);
    }

    @Override
    public Map<Integer, Action> middleStep(State.StateView newstate, History.HistoryView statehistory) {
        GameStateChild bestChild = alphaBetaSearch(new GameStateChild(newstate),
                numPlys,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY);

        return bestChild.action;
    }

    @Override
    public void terminalStep(State.StateView newstate, History.HistoryView statehistory) {

    }

    @Override
    public void savePlayerData(OutputStream os) {

    }

    @Override
    public void loadPlayerData(InputStream is) {

    }

    /**
     * You will implement this.
     *
     * This is the main entry point to the alpha beta search. Refer to the slides, assignment description
     * and book for more information.
     *
     * Try to keep the logic in this function as abstract as possible (i.e. move as much SEPIA specific
     * code into other functions and methods)
     *
     * @param node The action and state to search from
     * @param depth The remaining number of plys under this node
     * @param alpha The current best value for the maximizing node from this node to the root
     * @param beta The current best value for the minimizing node from this node to the root
     * @return The best child of this node with updated values
     */
    public GameStateChild alphaBetaSearch(GameStateChild node, int depth, double alpha, double beta) {
        double result_value = get_min_max_value(node, depth, alpha, beta, true);
        List<GameStateChild> childrens = node.state.getChildren();
        for (GameStateChild child : childrens) {
            if (child.state.getUtility() == result_value) {
                return child;
            }
        }
        // System.err.println("No matched value childern found");
        // System.exit(1);
        return childrens.get(0);
    }




    private double get_min_max_value (GameStateChild node, int depth, double alpha, double beta, boolean max_flag){
        if (depth <= 0) {
            return node.state.getUtility();
        }
        double result_value = 0;
        double max_value = Double.NEGATIVE_INFINITY;
        double min_value = Double.POSITIVE_INFINITY;
		for (GameStateChild child : orderChildrenWithHeuristics(node.state.getChildren())) {
            if (max_flag) {
                max_value = Math.max(max_value, get_min_max_value(child, depth - 1, alpha, beta, false));
                if(max_value >= beta){
        			return max_value;
        		}
        		alpha = Math.max(alpha, max_value);
                result_value = max_value;
            }
            else {
                min_value = Math.min(min_value, get_min_max_value(child, depth - 1, alpha, beta, true));
                if(min_value <= alpha){
    				return min_value;
    			}
    			beta = Math.min(min_value, beta);
                result_value = min_value;
            }
    	}
        return result_value;

    }


    /**
     * You will implement this.
     *
     * Given a list of children you will order them according to heuristics you make up.
     * See the assignment description for suggestions on heuristics to use when sorting.
     * Sort base on utility.
     * Use this function inside of your alphaBetaSearch method.
     *
     * Include a good comment about what your heuristics are and why you chose them.
     *
     * @param children
     * @return The list of children sorted by your heuristic.
     */
    public List<GameStateChild> orderChildrenWithHeuristics(List<GameStateChild> children) {
        List<GameStateChild> sorted_children = new ArrayList<>();
        for (GameStateChild c : children){
        	sorted_children.add(c);
        }
        sorted_children.sort(state_child_comptr);
        return sorted_children;
    }
}