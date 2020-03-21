package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.*;
import edu.cwru.sepia.environment.model.state.*;

public class Deposit implements StripsAction {

	public Peasant peasant_unit;;
	public UnitView townhall;
	public GameState state;


	public Deposit(Peasant peasant_unit, GameState state){
		this.peasant_unit = peasant_unit;
		this.state = state;
		this.townhall = state.getTownHall();
	}


	@Override
	public boolean preconditionsMet(GameState state) {
		return !peasant_unit.peasant_cargo_amount == 0 && peasant_unit.position.isAdjacent(new Position(townhall.getXPosition(), townhall.getYPosition());
	}


	@Override
	public GameState apply(GameState state) {
        GameState next_state = new GameState(state);
        Peasant action_peasant_unit = next_state.get_action_peasant(this.peasant_unit);
		if (action_peasant_unit.peasant_cargo_type == ResourceNode.Type.GOLD_MINE){
			// gold + 100
		}
		else{
			// wood + 100
		}
		// action_peasant_unit.peasant_cargo_type = null;
        // action_peasant_unit.peasant_cargo_amount = 0;
        action_peasant_unit.load_cargo(null, 0);
		return next_state;
	}

}