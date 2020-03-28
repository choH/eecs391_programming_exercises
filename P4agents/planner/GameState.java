package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.agent.planner.actions.*;
import edu.cwru.sepia.environment.model.state.*;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.environment.model.state.UnitTemplate;
import edu.cwru.sepia.util.*;

import java.util.*;


/**
 * This class is used to represent the state of the game after applying one of the avaiable actions. It will also
 * track the A* specific information such as the parent pointer and the cost and heuristic function. Remember that
 * unlike the path planning A* from the first assignment the cost of an action may be more than 1. Specifically the cost
 * of executing a compound action such as move can be more than 1. You will need to account for this in your heuristic
 * and your cost function.
 *
 * The first instance is constructed from the StateView object (like in PA2). Implement the methods provided and
 * add any other methods and member variables you need.
 *
 * Some useful API calls for the state view are
 *
 * state.getXExtent() and state.getYExtent() to get the map size
 *
  * Note that SEPIA saves the townhall as a unit. Therefore when you create a GameState instance,
 * you must be able to distinguish the townhall from a peasant. This can be done by getting
 * the name of the unit type from that unit's TemplateView:
 * state.getUnit(id).getTemplateView().getName().toLowerCase(): returns "townhall" or "peasant"
 *
 * You will also need to distinguish between gold mines and trees.
 * state.getResourceNode(id).getType(): returns the type of the given resource
 *
 * You can compare these types to values in the ResourceNode.Type enum:
 * ResourceNode.Type.GOLD_MINE and ResourceNode.Type.TREE
 *
 * You can check how much of a resource is remaining with the following:
 * state.getResourceNode(id).getAmountRemaining()
 *
 * I recommend storing the actions that generated the instance of the GameState in this class using whatever
 * class/structure you use to represent actions.
 */
public class GameState implements Comparable<GameState> {

	State.StateView state;
	private int playerNum;
	private int requiredGold, requiredWood, currentGold, currentWood;
	private boolean buildPeasants;
	private int xExtent, yExtent;

	private List<ResourceView> resourceNodes;
	private boolean[][] map;
	private int[][] goldMap, woodMap;

	private List<UnitView> allUnits;
    private List<UnitView> playerUnits = new ArrayList<UnitView>();
    private UnitView townHall;

	private double cost;
	private List<StripsAction> plan;
	private GameState parent;

	public int getPlayerNum() {
		return playerNum;
	}
	public int getxExtent() {
		return xExtent;
	}
	public int getyExtent() {
		return yExtent;
	}
    public int getCurrentGold() {
		return currentGold;
	}
	public int getCurrentWood() {
		return currentWood;
	}

	public List<ResourceView> getResourceNodes() {
		return resourceNodes;
	}
	public boolean[][] getMap() {
		return map;
	}
	public int[][] getGoldMap() {
		return this.goldMap;
	}
	public int[][] getWoodMap() {
		return this.woodMap;
	}

	public List<UnitView> getAllUnits() {
		return allUnits;
	}
	public List<UnitView> getPlayerUnits() {
		return playerUnits;
	}


	public GameState getParent() {
		return parent;
	}
    public StripsAction getPreviousAction() {
    	return plan.get(0);
    }
	public List<StripsAction> getPlan() {
		return this.plan;
	}

    /**
     * Construct a GameState from a stateview object. This is used to construct the initial search node. All other
     * nodes should be constructed from the another constructor you create or by factory functions that you create.
     *
     * @param state The current stateview at the time the plan is being created
     * @param playernum The player number of agent that is planning
     * @param requiredGold The goal amount of gold (e.g. 200 for the small scenario)
     * @param requiredWood The goal amount of wood (e.g. 200 for the small scenario)
     * @param buildPeasants True if the BuildPeasant action should be considered
     */
	public GameState(State.StateView state, int playernum, int requiredGold, int requiredWood, boolean buildPeasants) {
        // TODO: Implement me!s
		this.state = state;
		this.playerNum = playernum;
		this.buildPeasants = buildPeasants;
		this.xExtent = state.getXExtent();
		this.yExtent = state.getYExtent();
		this.allUnits = state.getAllUnits();
		for (UnitView u : allUnits) {
			if (u.getTemplateView().getPlayer() == playerNum) {
				if (u.getTemplateView().getName().toLowerCase().equals("peasant")) {
					playerUnits.add(u);
				} else if (u.getTemplateView().getName().toLowerCase().equals("townhall")) {
					this.townHall = u;
				}
			}
		}

		this.map = new boolean[xExtent][yExtent];
		this.goldMap = new int[xExtent][yExtent];
		this.woodMap = new int[xExtent][yExtent];
		for (int x = 0; x < xExtent; x++) {
			for (int y = 0; y < yExtent; y++) {
				map[x][y] = false;
				goldMap[x][y] = 0;
				woodMap[x][y] = 0;
			}
		}
		map[townHall.getXPosition()][townHall.getYPosition()] = true;

		this.resourceNodes = state.getAllResourceNodes();
		for (ResourceView r : resourceNodes) {
			map[r.getXPosition()][r.getYPosition()] = true;
			if (r.getType() == ResourceNode.Type.GOLD_MINE) {
				goldMap[r.getXPosition()][r.getYPosition()] = r.getAmountRemaining();
			}
			if (r.getType() == ResourceNode.Type.TREE) {
				woodMap[r.getXPosition()][r.getYPosition()] = r.getAmountRemaining();
			}
		}
		this.requiredGold = requiredGold;
		this.requiredWood = requiredWood;
		this.currentGold = state.getResourceAmount(playernum, ResourceType.GOLD);
		this.currentWood = state.getResourceAmount(playernum, ResourceType.WOOD);

        this.plan = new ArrayList<StripsAction>();
		this.cost = getCost();
		this.parent = null;
		heuristic();
	}

    // Copy for parent to "offspring."
	public GameState(GameState copy) {
		this.parent = copy;
		this.state = copy.state;
		this.playerNum = copy.playerNum;
        this.xExtent = copy.xExtent;
		this.yExtent = copy.yExtent;
        this.buildPeasants = copy.buildPeasants;

		this.requiredGold = copy.requiredGold;
		this.requiredWood = copy.requiredWood;

		this.currentGold = copy.currentGold;
		this.currentWood = copy.currentWood;


		List<ResourceView> cResourceNode = new ArrayList<ResourceView>();
		for (ResourceView r : copy.resourceNodes) {
			ResourceView rv = new ResourceView(new ResourceNode(r.getType(), r.getXPosition(),
					r.getYPosition(), r.getAmountRemaining(), r.getID()));
			cResourceNode.add(rv);
		}
		this.resourceNodes = cResourceNode;

		boolean[][] originalMap = copy.map;
		boolean[][] cMap = new boolean[originalMap.length][originalMap[0].length];
		for (int i = 0; i < originalMap.length; i++) {
			for (int j = 0; j < originalMap[0].length; j++) {
				cMap[i][j] = originalMap[i][j];
			}
		}
		this.map = cMap;

		int[][] originalGmap = copy.goldMap;
		int[][] cGmap = new int[originalGmap.length][originalGmap[0].length];
		for (int i = 0; i < originalGmap.length; i++) {
			for (int j = 0; j < originalGmap[0].length; j++) {
				cGmap[i][j] = originalGmap[i][j];
			}
		}
		this.goldMap = cGmap;

		int[][] originalWmap = copy.woodMap;
		int[][] cWmap = new int[originalWmap.length][originalWmap[0].length];
		for (int i = 0; i < originalWmap.length; i++) {
			for (int j = 0; j < originalWmap[0].length; j++) {
				cWmap[i][j] = originalWmap[i][j];
			}
		}
		this.woodMap = cWmap;

		List<UnitView> cUnits = new ArrayList<UnitView>();
		for (UnitView uv : copy.allUnits) {
			Unit unit = new Unit(new UnitTemplate(uv.getID()), uv.getID());
			unit.setxPosition(uv.getXPosition());
			unit.setyPosition(uv.getYPosition());
            unit.setCargo(uv.getCargoType(), uv.getCargoAmount());
			cUnits.add(new UnitView(unit));
		}
		this.allUnits = cUnits;


		List<UnitView> cPlayerUnits = new ArrayList<UnitView>();
		for (UnitView uv : copy.playerUnits) {
			Unit unit = new Unit(new UnitTemplate(uv.getID()), uv.getID());
			unit.setxPosition(uv.getXPosition());
			unit.setyPosition(uv.getYPosition());
            unit.setCargo(uv.getCargoType(), uv.getCargoAmount());
			cPlayerUnits.add(new UnitView(unit));
		}
		this.playerUnits = cPlayerUnits;

		this.cost = getCost();
		List<StripsAction> copiedList = new ArrayList<StripsAction>();
		for (StripsAction element : copy.getPlan()) {
			copiedList.add(element);
		}
		this.plan = copiedList;
		this.townHall = copy.townHall;
		heuristic();
	}


    /**
     * Unlike in the first A* assignment there are many possible goal states. As long as the wood and gold requirements
     * are met the peasants can be at any location and the capacities of the resource locations can be anything. Use
     * this function to check if the goal conditions are met and return true if they are.
     *
     * @return true if the goal conditions are met in this instance of game state.
     */
	public boolean isGoal() {
		return (this.getCurrentGold() >= this.requiredGold && this.getCurrentWood() >= this.requiredWood);
	}

    /**
     * The branching factor of this search graph are much higher than the planning. Generate all of the possible
     * successor states and their associated actions in this method.
     *
     * @return A list of the possible successor states and their associated actions
     */
	public List<GameState> generateChildren() {
		List<GameState> result = new ArrayList<>();
		for (UnitView u : playerUnits) {
			for (Direction d : Direction.values()) {

				int x = u.getXPosition() + d.xComponent();
				int y = u.getYPosition() + d.yComponent();
				Position newPosition = new Position(x, y);

				Position townHallPosition = new Position(townHall.getXPosition(), townHall.getYPosition());

				Deposit depositGold = new Deposit(u, newPosition, townHallPosition, this, true);
				if (depositGold.preconditionsMet(this)) {
					result.add(depositGold.apply(this));
				}

				Deposit depositWood = new Deposit(u, newPosition, townHallPosition, this, false);
				if (depositWood.preconditionsMet(this)) {
					result.add(depositWood.apply(this));
				}

				Harvest harvestGold = new Harvest(u, newPosition, this, true);
				if (harvestGold.preconditionsMet(this)) {
					result.add(harvestGold.apply(this));
				}

				Harvest harvestWood = new Harvest(u, newPosition, this, false);
				if (harvestWood.preconditionsMet(this)) {
					result.add(harvestWood.apply(this));
				}
			}

			Position bestGold = findMostGold(new Position(u.getXPosition(), u.getYPosition()));
			bestMove(result, u, bestGold);

			Position bestWood = findMostWood(new Position(u.getXPosition(), u.getYPosition()));
			bestMove(result, u, bestWood);

			Position th = new Position(townHall.getXPosition(), townHall.getYPosition());
			bestMove(result, u, th);
		}

		return result;
	}

	private void bestMove(List<GameState> children, UnitView unit, Position position) {
		for (Direction d : Direction.values()) {
			int x = position.x + d.xComponent();
			int y = position.y + d.yComponent();
			Move move = new Move(unit, new Position(x, y), this);
			if (move.preconditionsMet(this)) {
				children.add(move.apply(this));
			}
		}
	}

    public UnitView findUnit(int unitId, List<UnitView> units) {
        for (UnitView u : units) {
            if (u.getID() == unitId) {
                return u;
            }
        }
        return null;
    }


    public ResourceView findResource(int x, int y, List<ResourceView> resources) {
        for (ResourceView r : resources) {
            if (r.getXPosition() == x && r.getYPosition() == y) {
                return r;
            }
        }
        return null;
    }

	private Position findBestResource(Position currentPosition) {
		Position best = null;
		int resource = 0;
		int dist = 0;
		int goldDiff = requiredGold - currentGold;
		int woodDiff = requiredWood - currentWood;
		int requiredAmount;
		int currentAmount;
		int[][] map;
		if(goldDiff < woodDiff) {
			requiredAmount = requiredGold;
			currentAmount = currentGold;
			map = goldMap;
		}
		else {
			requiredAmount = requiredWood;
			currentAmount = currentWood;
			map = woodMap;
		}

		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[i].length; j ++) {
				int currentBest = map[i][j];
				if(currentBest > 0) {
					int distance = currentPosition.chebyshevDistance(new Position(i, j));;
					if(best == null) {
						best = new Position(i, j);
						dist = distance;
						resource = currentBest;
					}
					else {
						if(distance <= dist) {
							if(currentBest >= resource || currentBest >= requiredAmount - currentAmount) {
								best = new Position(i, j);
								dist = distance;
								resource = currentBest;
							}
						}
						else {
							if(resource >= 100) {
								if(currentBest >= resource && currentBest >= 100 && resource < requiredAmount - currentAmount) {
									best = new Position(i, j);
									dist = distance;
									resource = currentBest;
								}
							}

						}
					}
				}
			}
		}

		return best;

	}

	public Position findMostGold(Position currentPosition) {
		Position result = null;
		int resource = 0;
		int dist = 0;
		int requiredAmount = requiredGold;
		int currentAmount = currentGold;
		int [][] map = goldMap;
		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[i].length; j ++) {
				int currentBest = map[i][j];
				if(currentBest > 0) {
					int distance = currentPosition.chebyshevDistance(new Position(i, j));
					if(result == null) {
						result = new Position(i, j);
						dist = distance;
						resource = currentBest;
					}
					else {
						if(distance <= dist) {
							if(currentBest >= resource || currentBest >= requiredAmount - currentAmount) {
								result = new Position(i, j);
								dist = distance;
								resource = currentBest;
							}
						}
					}
				}
			}
		}

		return result;
	}

	public Position findMostWood(Position currentPosition) {
		Position result = null;
		int resource = 0;
		int dist = 0;
		int requiredAmount = requiredWood;
		int currentAmount = currentWood;
		int [][] map = woodMap;
		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[i].length; j ++) {
				int currentBest = map[i][j];
				if(currentBest > 0) {
					int distance = currentPosition.chebyshevDistance(new Position(i, j));
					if(result == null) {
						result = new Position(i, j);
						dist = distance;
						resource = currentBest;
					}
					else {
						if(distance <= dist) {
							if(currentBest >= resource || currentBest >= requiredAmount - currentAmount) {
								result = new Position(i, j);
								dist = distance;
								resource = currentBest;
							}
						}
					}
				}
			}
		}

		return result;
	}



    /**
     * Write your heuristic function here. Remember this must be admissible for the properties of A* to hold. If you
     * can come up with an easy way of computing a consistent heuristic that is even better, but not strictly necessary.
     *
     * Add a description here in your submission explaining your heuristic.
     *
     * @return The value estimated remaining cost to reach a goal state from this state.
     */
	public double heuristic() {
    	int goldDiff = requiredGold - currentGold;
    	int woodDiff = requiredWood - currentWood;
        double result = goldDiff + woodDiff;


    	UnitView peasant = playerUnits.get(0);
		Position unitPosition = new Position(peasant.getXPosition(), peasant.getYPosition());


    	if (peasant.getCargoAmount() > 0) {
    		Position th = new Position(townHall.getXPosition(), townHall.getYPosition());
			result -= peasant.getCargoAmount() * 0.5;
			double disFromTh =th.chebyshevDistance(unitPosition);
			result += disFromTh;
    	}
    	else {
    		Position bestResource = findBestResource(unitPosition);
			result += bestResource.chebyshevDistance(unitPosition) * 0.5;
    		double harvestAmount = 0;
    		for(Direction d : Direction.values()) {
    			int x = peasant.getXPosition() + d.xComponent();
    			int y = peasant.getYPosition() + d.yComponent();
    			int goldAmount = goldMap[x][y];
    			int woodAmount = woodMap[x][y];
    			if (goldAmount > 0) {
    				if (requiredGold - currentGold > 0) {
    					if (goldAmount > 100) {
    						harvestAmount = 100;
    					}
    					else {
    						harvestAmount = goldMap[x][y];}
    				}
    			}
    			if (woodAmount > 0) {
    				if (requiredWood - currentWood > 0) {
    					if (woodAmount > 100) {
    						harvestAmount = 100;
    					}
    					else {
    						harvestAmount = woodMap[x][y];}
    					}
    				}
    			}

				result -= harvestAmount * 0.3;
    	}


        return result;
	}


	/**
	 * Write the function that computes the current cost to get to this node. This
	 * is combined with your heuristic to determine which actions/states are better
	 * to explore.
	 *
	 * @return The current cost to reach this goal
	 */
	public double getCost() {
		return this.cost;
	}
	public void addGold(int n) {
		currentGold += n;
	}
	public void addWood(int n) {
		currentWood += n;
	}
	public void addCost(double n) {
		this.cost += n;
	}
	public void addPlan(StripsAction action) {
		plan.add(action);
	}

    /**
     * This is necessary to use your state in the Java priority queue. See the official priority queue and Comparable
     * interface documentation to learn how this function should work.
     *
     * @param o The other game state to compare
     * @return 1 if this state costs more than the other, 0 if equal, -1 otherwise
     */
	@Override
	public int compareTo(GameState o) {
		double thisTotalCost = this.getCost() + this.heuristic();
		double totalCost = o.getCost() + o.heuristic();

		return (int) ((int) thisTotalCost - totalCost);
	}

    /**
     * This will be necessary to use the GameState as a key in a Set or Map.
     *
     * @param o The game state to compare
     * @return True if this state equals the other state, false otherwise.
     */
    @Override
	public boolean equals(Object o) {
		if (o instanceof GameState) {
			if (this.currentGold == ((GameState) o).getCurrentGold()
				&& this.currentWood == ((GameState) o).getCurrentWood()
				&& this.allUnits.equals(((GameState) o).getAllUnits())
				&& this.heuristic() == ((GameState) o).heuristic()) {

                return true;
			}
		}
		return false;
	}




    /**
     * This is necessary to use the GameState as a key in a HashSet or HashMap. Remember that if two objects are
     * equal they should hash to the same value.
     *
     * @return An integer hashcode that is equal for equal states.
     */
	@Override
	public int hashCode() {
		return 0; // since we did equals() this seems trivial?
	}




}