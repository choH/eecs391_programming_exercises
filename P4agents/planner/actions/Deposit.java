package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.*;
import edu.cwru.sepia.environment.model.state.*;
import edu.cwru.sepia.util.*;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;


public class Deposit implements StripsAction {

	Position townHallPosition;
	Position currentPosition;

    UnitView unit;
	Action sepiaAction;
	GameState parent;
    Boolean forGold;

	public Deposit (UnitView unit, Position currentPosition, Position townHallPosition, GameState parent, Boolean forGold) {
		this.unit = unit;
		this.currentPosition = currentPosition;
		this.townHallPosition = townHallPosition;
		this.parent = parent;
        this.forGold = forGold;
	}


    private Direction getDirection(int originalX, int originalY, int currentX, int currentY) {
        for (Direction d : Direction.values()) {
            if((currentX - originalX) == d.xComponent() && (currentY - originalY) == d.yComponent()) {
                return d;
            }
        }
        return null;
    }


    @Override
    public GameState getParent() {
		return this.parent;
	}

	@Override
	public boolean preconditionsMet(GameState state) {
        if (this.forGold == true) {
            if ((unit.getCargoAmount() > 0 && unit.getCargoType() == ResourceType.GOLD && currentPosition.equals(townHallPosition))) {
    			return true;
    		}
            return false;
        }

        if (this.forGold == false) {
            if ((unit.getCargoAmount() > 0 && unit.getCargoType() == ResourceType.WOOD && currentPosition.equals(townHallPosition))) {
    			return true;
    		}
            return false;
        }

        return false;

	}

	@Override
	public GameState apply(GameState state) {

        GameState result = new GameState(state);
        UnitView originalUnit = result.findUnit(unit.getID(), result.getPlayerUnits());
        Unit currentUnit = new Unit(new UnitTemplate(originalUnit.getID()), originalUnit.getID());

        currentUnit.setxPosition(unit.getXPosition());
        currentUnit.setyPosition(unit.getYPosition());
        currentUnit.clearCargo();
        result.getPlayerUnits().remove(originalUnit);
        result.getPlayerUnits().add(new UnitView(currentUnit));

        if (this.forGold == true) {
            result.addGold(unit.getCargoAmount());
        }
        else {
            result.addWood(unit.getCargoAmount());
        }
        result.addCost(1);
        result.heuristic();
        result.addPlan(this);


        sepiaAction = Action.createPrimitiveDeposit(originalUnit.getID(), getDirection(currentUnit.getxPosition(),currentUnit.getyPosition(), townHallPosition.x, townHallPosition.y));
        return result;
	}


	@Override
	public Action createSEPIAaction() {
		return sepiaAction;
	}






}