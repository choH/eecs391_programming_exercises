package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.*;
import edu.cwru.sepia.util.*;

public class Move implements StripsAction {

    public Peasant peasant_unit;
    public GameState state;
    public Position dest;


    public Move(Peasant peasant_unit, GameState state, Direction dir){
        this.peasant_unit = peasant_unit;
        this.state = state;
        this.dest = peasant_unit.position.move(dir);
    }

    @Override
    public boolean preconditionsMet(GameState state){
    	return dest.inBounds(state.xExtent, state.yExtent);
    }

    @Override
    public GameState apply(GameState state){
        GameState next_state = new GameState(state);
        Peasant action_peasant_unit = next_state.get_action_peasant(this.peasant_unit);
        action_peasant_unit.position = this.dest;
    	return next_state;
    }