package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MinimaxAlphaBeta extends Agent {

    private final int numPlys;

    private static final Comparator<GameStateChild> state_child_comptr = (c1, c2) -> {
    	if (c1.state.getUtility() > c1.state.getUtility()) {
    		return -1;
    	}
        else if (c1.state.getUtility() < c1.state.getUtility()) {
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
        System.err.println("No matched value childern found");
        System.exit(1);
        return node;
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
     *  Both attack > one attack > no attack, we consulted online resources on this.
     * Use this function inside of your alphaBetaSearch method.
     *
     * Include a good comment about what your heuristics are and why you chose them.
     *
     * @param children
     * @return The list of children sorted by your heuristic.
     */
    public List<GameStateChild> orderChildrenWithHeuristics(List<GameStateChild> children) {
        List<GameStateChild> attack_list = new LinkedList<GameStateChild>(children);
        List<GameStateChild> move_list = new LinkedList<GameStateChild>(children);

        // overwritten compare() method from comparator interface with shortcut.
        // Collections.sort(orderedList, new Comparator<GameStateChild>() {
        //     public int compare(GameStateChild child_a, GameStateChild child_b) {
        //         return child_a.state.getUtility().compareTo(child_b.state.getUtility());
        //     }
        // });

        boolean two_attackers_action_found = false;
        for (GameStateChild c : children) {
            int attacker_count = 0;

        	for (Action a : c.action.values()) {
        		if (a.getType().name().equals(GameState.ACTION_ATTACK_NAME)) {
        			attacker_count++;
        		}
        	}

        	if (attacker_count == 2) {
        		attack_list.add(0, c);
        	}
            else if (attacker_count == 1)  {
                attack_list.add(c);
            }
            else {
        		move_list.add(c);
        	}
        }

        move_list.sort(state_child_comptr);
        attack_list.addAll(move_list);
        return attack_list;
    }
}