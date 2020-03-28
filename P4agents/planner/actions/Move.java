package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.*;
import edu.cwru.sepia.environment.model.state.*;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.util.*;

public class Move implements StripsAction {

    Position currentPosition;

    UnitView unit;
    Action sepiaAction;
    GameState parent;
    // Boolean forGold;

	public Move (UnitView unit, Position currentPosition, GameState parent) {
		this.unit = unit;
		this.currentPosition = currentPosition;
		this.parent = parent;
	}

    @Override
	public GameState getParent() {
		return this.parent;
	}

	@Override
	public boolean preconditionsMet(GameState state) {
		int x = currentPosition.x;
		int y = currentPosition.y;
		if (x < state.getxExtent() && x > 0 && y < state.getyExtent() && y > 0
				&& !state.getMap()[x][y] ) {
			for (UnitView u : state.getPlayerUnits()) {
				if (x == u.getXPosition() && y == u.getYPosition()) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}

	}

	@Override
	public GameState apply(GameState state) {

		GameState result = new GameState(state);
		int x = currentPosition.x;
		int y = currentPosition.y;

        UnitView originalUnit = result.findUnit(unit.getID(), result.getPlayerUnits());
		UnitTemplate currentTemplate = new UnitTemplate(originalUnit.getID());
		currentTemplate.setCanGather(true);

		Unit currentUnit = new Unit(currentTemplate, originalUnit.getID());
        currentUnit.setxPosition(unit.getXPosition());
		currentUnit.setyPosition(unit.getYPosition());

		if (unit.getCargoAmount() > 0) {
			currentUnit.setCargo(unit.getCargoType(), unit.getCargoAmount());
		}

        result.getPlayerUnits().remove(originalUnit);

		result.getPlayerUnits().add(new UnitView(currentUnit));

        Position originalPosition = new Position(unit.getXPosition(), unit.getYPosition());
        double dist = originalPosition.chebyshevDistance(currentPosition);
        result.addCost(dist);
		result.heuristic();
		result.addPlan(this);

		sepiaAction = Action.createCompoundMove(originalUnit.getID(), x, y);

		return result;
	}

	@Override
	public Action createSEPIAaction() {
		return sepiaAction;
	}


}