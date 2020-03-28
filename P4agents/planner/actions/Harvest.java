package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.planner.*;
import edu.cwru.sepia.environment.model.state.*;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.util.*;

public class Harvest implements StripsAction {

    Position currentPosition;

    UnitView unit;
    Action sepiaAction;
    GameState parent;
    Boolean forGold;

	public Harvest (UnitView unit, Position currentPosition, GameState parent, Boolean forGold) {
        this.unit = unit;
        this.currentPosition = currentPosition;
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
		int x = currentPosition.x;
		int y = currentPosition.y;

		if (x >= state.getxExtent() || x < 0 || y >= state.getyExtent() || y < 0) {
			return false;
		}

		else {
            if (this.forGold == true) {
                if (state.getGoldMap()[x][y] > 0 && unit.getCargoAmount() == 0) {
    				return true;
    			}
            }
            if (this.forGold == false) {
                if (state.getWoodMap()[x][y] > 0 && unit.getCargoAmount() == 0) {
    				return true;
    			}
            }

			return false;
		}
	}

	@Override
	public GameState apply(GameState state) {

		GameState result = new GameState(state);
		int x = currentPosition.x;
		int y = currentPosition.y;
        int amount = 0;
        if (this.forGold == true) {
            amount = result.getGoldMap()[x][y];
        }
        if (this.forGold == false) {
            amount = result.getWoodMap()[x][y];
        }

		UnitView originalUnit = result.findUnit(unit.getID(), result.getPlayerUnits());
		UnitTemplate currentTemplate = new UnitTemplate(originalUnit.getID());
        currentTemplate.setCanGather(true);

		Unit currentUnit = new Unit(currentTemplate, originalUnit.getID());
		currentUnit.setxPosition(unit.getXPosition());
		currentUnit.setyPosition(unit.getYPosition());

		ResourceView originalResource = result.findResource(x, y, result.getResourceNodes());
		ResourceNode currentResource = null;

        if (this.forGold == true) {
    		if (amount < 100) {
    			result.getGoldMap()[x][y] = 0;
    			currentUnit.setCargo(ResourceType.GOLD, amount);
    			amount = 0;
    		}
            else {
    			result.getGoldMap()[x][y] -= 100;
    			currentUnit.setCargo(ResourceType.GOLD, 100);
    			amount -= 100;
    		}
            currentResource = new ResourceNode(ResourceNode.Type.GOLD_MINE, x, y, amount, originalResource.getID());
        }

        if (this.forGold == false) {
            if (amount < 100) {
    			result.getWoodMap()[x][y] = 0;
    			currentUnit.setCargo(ResourceType.WOOD, amount);
                amount = 0;
    		}
    		else {
    			result.getWoodMap()[x][y] -= 100;
    			currentUnit.setCargo(ResourceType.WOOD, 100);
    			amount -= 100;
    		}
            currentResource = new ResourceNode(ResourceNode.Type.TREE, x, y, amount, originalResource.getID());
        }
        // currentUnit.setxPosition(unit.getXPosition());
        // currentUnit.setyPosition(unit.getYPosition());
        // currentUnit.clearCargo();
        result.getPlayerUnits().remove(originalUnit);
        result.getResourceNodes().remove(originalResource);
        result.getPlayerUnits().add(new UnitView(currentUnit));
        result.getResourceNodes().add(new ResourceView(currentResource));

        result.addCost(1);
        result.heuristic();
        result.addPlan(this);

        sepiaAction = Action.createPrimitiveGather(originalUnit.getID(), getDirection(originalUnit.getXPosition(), currentUnit.getyPosition(), x, y));
        return result;
	}


	@Override
	public Action createSEPIAaction() {
		return sepiaAction;
	}


}