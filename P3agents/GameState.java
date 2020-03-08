package edu.cwru.sepia.agent.minimax;

import java.util.*;
import edu.cwru.sepia.action.*;
import edu.cwru.sepia.agent.minimax.AstarAgent.*;
import edu.cwru.sepia.environment.model.state.*;
import edu.cwru.sepia.util.*;


public class GameState {
	public final String ACTION_ATTACK_NAME = Action.createPrimitiveAttack(0, 0).getType().name();

	public final int COE_FOOTMAN_HP = 1;
	public final int COE_ARCHER_HP = -10;
    public final int COE_FOOTMAN_DISTANCE = -2;
	public final int FOOTMAN_RANGE = 1;
	public final int ARCHER_RANGE = 8;

	public List<GameUnit> footmen;
    public List<GameUnit> archers;
	public int footman_unit_idf = 0;
	public int archer_unit_idf = 1;

    public int xExtent = 0;
    public int yExtent = 0;
    public List<ResourceNode.ResourceView> obstacles;
	public AstarAgent a_star_agent;

	public int utility;
	public List<Direction> legal_dir;


    /**
     * You will implement this constructor. It will
     * extract all of the needed state information from the built in
     * SEPIA state view.
     *
     * You may find the following state methods useful:
     *
     * state.getXExtent() and state.getYExtent(): get the map dimensions
     * state.getAllResourceIDs(): returns the IDs of all of the obstacles in the map
     * state.getResourceNode(int resourceID): Return a ResourceView for the given ID
     *
     * For a given ResourceView you can query the position using
     * resource.getXPosition() and resource.getYPosition()
     *
     * You can get a list of all the units belonging to a player with the following command:
     * state.getUnitIds(int playerNum): gives a list of all unit IDs beloning to the player.
     * You control player 0, the enemy controls player 1.
     *
     * In order to see information about a specific unit, you must first get the UnitView
     * corresponding to that unit.
     * state.getUnit(int id): gives the UnitView for a specific unit
     *
     * With a UnitView you can find information about a given unit
     * unitView.getXPosition() and unitView.getYPosition(): get the current location of this unit
     * unitView.getHP(): get the current health of this unit
     *
     * SEPIA stores information about unit types inside TemplateView objects.
     * For a given unit type you will need to find statistics from its Template View.
     * unitView.getTemplateView().getRange(): This gives you the attack range
     * unitView.getTemplateView().getBasicAttack(): The amount of damage this unit type deals
     * unitView.getTemplateView().getBaseHealth(): The initial amount of health of this unit type
     *
     * @param state Current state of the episode
     */
	public GameState(State.StateView stateView){

		this.footmen = new ArrayList<GameUnit>();
		this.archers = new ArrayList<GameUnit>();
		List<Unit.UnitView> footmen_unit_view = stateView.getUnits(footman_unit_idf);
		List<Unit.UnitView> archers_unit_vioew = stateView.getUnits(archer_unit_idf);
		for (Unit.UnitView f : footmen_unit_view) {
			footmen.add(new GameUnit(f));
		}
		for (Unit.UnitView a : archers_unit_vioew) {
			archers.add(new GameUnit(a));
		}

		this.xExtent = stateView.getXExtent();
		this.yExtent = stateView.getYExtent();
		this.obstacles = new ArrayList<>();
		for (ResourceNode.ResourceView r : stateView.getAllResourceNodes()){
			obstacles.add(r);
		}
		a_star_agent = new AstarAgent(xExtent, yExtent);
        this.legal_dir = create_legal_dir_list();
	}



	public GameState(Integer utility) {
		footmen = new ArrayList<GameUnit>();
		archers = new ArrayList<GameUnit>();
        a_star_agent = new AstarAgent(xExtent, yExtent);
        this.utility = utility;
        this.obstacles = new ArrayList<>();
		this.legal_dir = create_legal_dir_list();
	}

    // copy constructor
	public GameState(GameState another_GameState){

        this.xExtent = another_GameState.xExtent;
		this.yExtent = another_GameState.yExtent;
        this.obstacles = new ArrayList<ResourceNode.ResourceView>();
        for (ResourceNode.ResourceView r : another_GameState.obstacles){
			this.obstacles.add(r);
		}
        a_star_agent = new AstarAgent(xExtent, yExtent);

		this.footmen = new ArrayList<GameUnit>();
		for (GameUnit f : another_GameState.footmen) {
			this.footmen.add(new GameUnit(f));
		}
		this.archers = new ArrayList<GameUnit>();
		for (GameUnit a : another_GameState.archers) {
			this.archers.add(new GameUnit(a));
		}

		this.legal_dir = create_legal_dir_list();
	}


	public List<Direction> create_legal_dir_list(){
		List<Direction> legal_dir = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			if (dir == Direction.NORTH || dir == Direction.EAST || dir == Direction.WEST || dir == Direction.SOUTH) {
                legal_dir.add(dir);
            }
		}
		return legal_dir;
	}




////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////



	public void enforce_actions(Map<Integer, Action> actions) {
        for (Map.Entry<Integer, Action> an_action: actions.entrySet()) {
            if (an_action.getValue().getType() == ActionType.COMPOUNDATTACK) {
                TargetedAction an_targeted_action = (TargetedAction) an_action.getValue();
                int attack_footman_id = an_targeted_action.getUnitId();
                int target_archer_id = an_targeted_action.getTargetId();
                GameUnit target_archer = get_unit(target_archer_id);
                target_archer.hp -= get_unit(attack_footman_id).damage;
            }
            else if (an_action.getValue().getType() == ActionType.PRIMITIVEMOVE) {
                DirectedAction an_directed_action = (DirectedAction) an_action.getValue();
                int moving_footman_id = an_directed_action.getUnitId();
                GameUnit moving_footman = get_unit(moving_footman_id);
                Direction moving_footman_direction = an_directed_action.getDirection();
                moving_footman.x += moving_footman_direction.xComponent();
                moving_footman.y += moving_footman_direction.yComponent();
            }
            else {
                System.out.print("Invalid action type" + "\n");
                return;
            }
        }
	}



////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////




	private GameUnit get_unit(int ID) {
        List<GameUnit> game_units = new ArrayList<>(footmen);
		game_units.addAll(archers);

		for (GameUnit an_unit : game_units) {
			if (an_unit.ID == ID) {
				return an_unit;
			}
		}
		return null;
	}

    /**
     * You will implement this function.
     *
     * You should use weighted linear combination of features.
     * The features may be primitives from the state (such as hp of a unit)
     * or they may be higher level summaries of information from the state such
     * as distance to a specific location. Come up with whatever features you think
     * are useful and weight them appropriately.
     *
     * It is recommended that you start simple until you have your algorithm working. Then watch
     * your agent play and try to add features that correct mistakes it makes. However, remember that
     * your features should be as fast as possible to compute. If the features are slow then you will be
     * able to do less plys in a turn.
     *
     * Add a good comment about what is in your utility and why you chose those features:
     * A simple utility calculator based on the distance between footmen and archers, and also if foormen are attacking or not.
     *
     * @return The weighted linear combination of the features
     */
	public int getUtility() {

        int footmen_total_health = 0;
        for (GameUnit f : footmen) {
            footmen_total_health += f.hp;
        }

        int archers_total_health = 0;
        for (GameUnit a : archers) {
            archers_total_health += a.hp;
        }

        int cummulative_distance = 0;
        if (footmen.size() == 2 && archers.size() == 2) {
            cummulative_distance += distance_to_enemey(footmen.get(0), archers.get(0));
            cummulative_distance += distance_to_enemey(footmen.get(1), archers.get(1));
        }
        else if (footmen.size() == 1 && archers.size() == 1) {
            cummulative_distance += distance_to_enemey(footmen.get(0), archers.get(0));
        }
        else if (footmen.size() == 1 && archers.size() == 2) {
            cummulative_distance += distance_to_enemey(footmen.get(0), archers.get(0));
            cummulative_distance += distance_to_enemey(footmen.get(0), archers.get(1));
        }
        else if (footmen.size() == 2 && archers.size() == 1) {
            cummulative_distance += distance_to_enemey(footmen.get(0), archers.get(0));
            cummulative_distance += distance_to_enemey(footmen.get(1), archers.get(0));
        }
        else {
            System.out.print("Invalid quantity of foormen and archers" + "\n");
            System.exit(-1);
        }
        utility = (COE_FOOTMAN_HP * footmen_total_health) + (COE_ARCHER_HP * archers_total_health) + (COE_FOOTMAN_DISTANCE * cummulative_distance);
		return utility;
	}


    private int distance_to_enemey(GameUnit footman, GameUnit archer) {
        // return Math.abs(footman.x - archer.x) + Math.abs(footman.y - archer.y);
        return (int) Math.sqrt(Math.pow(Math.abs(footman.x - archer.x),2)+Math.pow(Math.abs(footman.y - archer.y), 2));
    }




////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////





    /**
     * You will implement this function.
     *
     * This will return a list of GameStateChild objects. You will generate all of the possible
     * actions in a step and then determine the resulting game state from that action. These are your GameStateChildren.
     *
     * It may be useful to be able to create a SEPIA Action. In this assignment you will
     * deal with movement and attacking actions. There are static methods inside the Action
     * class that allow you to create basic actions:
     * Action.createPrimitiveAttack(int attackerID, int targetID): returns an Action where
     * the attacker unit attacks the target unit.
     * Action.createPrimitiveMove(int unitID, Direction dir): returns an Action where the unit
     * moves one space in the specified direction.
     *
     * You may find it useful to iterate over all the different directions in SEPIA. This can
     * be done with the following loop:
     * for(Direction direction : Directions.values())
     *
     * To get the resulting position from a move in that direction you can do the following
     * x += direction.xComponent()
     * y += direction.yComponent()
     *
     * If you wish to explicitly use a Direction you can use the Direction enum, for example
     * Direction.NORTH or Direction.NORTHEAST.
     *
     * You can check many of the properties of an Action directly:
     * action.getType(): returns the ActionType of the action
     * action.getUnitID(): returns the ID of the unit performing the Action
     *
     * ActionType is an enum containing different types of actions. The methods given above
     * create actions of type ActionType.PRIMITIVEATTACK and ActionType.PRIMITIVEMOVE.
     *
     * For attack actions, you can check the unit that is being attacked. To do this, you
     * must cast the Action as a TargetedAction:
     * ((TargetedAction)action).getTargetID(): returns the ID of the unit being attacked
     *
     * @return All possible actions and their associated resulting game state
     */
	public List<GameStateChild> getChildren() {

		int footman_1_ID = footmen.get(0).ID;
		List<Action> footman_1_actions = get_actions(footmen.get(0), archers);

        int footman_2_ID = 0;
        List<Action> footman_2_actions = new ArrayList<>();
        boolean two_footmen = false;
		if (footmen.size() == 2) {
			footman_2_ID = footmen.get(1).ID;
			footman_2_actions = get_actions(footmen.get(1), archers);
			two_footmen = true;
		}

		List<GameStateChild> legal_children = new ArrayList<>();
		Map<Integer, Action> ID_action_map = new HashMap<>();

        // Iterator<Action> footman_1_actions_itr = footman_1_actions.iterator();
        // Iterator<Action> footman_2_actions_itr = footman_2_actions.iterator();
        // while (footman_1_actions_itr.hasNext() && footman_2_actions_itr.hasNext()) {
        //     ID_action_map.clear();
        //     Action a_footman_1_action = footman_1_actions_itr.next()
        //     Action a_footman_2_action = footman_2_actions_itr.next()
        // }
        if (two_footmen) {
            for (Action a_footman_1_action : footman_1_actions) {
                for (Action a_footman_2_action : footman_2_actions) {
                    ID_action_map = new HashMap<>();
                    // ID_action_map.clear();
                    ID_action_map.put(footman_1_ID, a_footman_1_action);
                    ID_action_map.put(footman_2_ID, a_footman_2_action);
                    if (!will_collide(ID_action_map, footman_1_ID, footman_2_ID)) {
                        GameState child_state_twin = new GameState(this);
                        child_state_twin.enforce_actions(ID_action_map);
                        legal_children.add(new GameStateChild(ID_action_map, child_state_twin));
                    }
                }
            }
        }
        else {
            for (Action a_footman_1_action : footman_1_actions) {
                ID_action_map = new HashMap<>();
                // ID_action_map.clear();
                ID_action_map.put(footman_1_ID, a_footman_1_action);
                GameState child_state = new GameState(this);
                child_state.enforce_actions(ID_action_map);
                legal_children.add(new GameStateChild(ID_action_map, child_state));
            }
        }
        return legal_children;
    }









////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////




	private boolean will_collide(Map<Integer, Action> actionMap, int footman_1_ID, int footman_2_ID) {

        GameUnit footman_1_GameUnit = get_unit(footman_1_ID);
        GameUnit footman_2_GameUnit = get_unit(footman_2_ID);
		Action footman_1_action = actionMap.get(footman_1_ID);
		Action footman_2_action = actionMap.get(footman_2_ID);

		if (footman_1_action.getType() == ActionType.PRIMITIVEMOVE && footman_2_action.getType() == ActionType.PRIMITIVEMOVE) {
            DirectedAction footman_1_direct_action = (DirectedAction) footman_1_action;
            DirectedAction footman_2_direct_action = (DirectedAction) footman_2_action;
            int footman_1_x = footman_1_GameUnit.x + footman_1_direct_action.getDirection().xComponent();
            int footman_1_y = footman_1_GameUnit.y + footman_1_direct_action.getDirection().yComponent();
            int footman_2_x = footman_2_GameUnit.x + footman_2_direct_action.getDirection().xComponent();
            int footman_2_y = footman_2_GameUnit.y + footman_2_direct_action.getDirection().yComponent();

            return (footman_1_x == footman_2_x) && (footman_1_y == footman_2_y);
		}
		return false;
	}








////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////







	private List<Action> get_actions(GameUnit a_footman, List<GameUnit> archers_list) {

        List<GameUnit> all_game_units = new ArrayList<>(footmen);
		all_game_units.addAll(archers);
		List<Action> actions = new ArrayList<>();
		if (obstacles.size() > 0 ) {
			Stack<MapLocation> a_star_goal_path = a_star_agent.findPath(obstacles, a_footman, get_closest_archer(a_footman, archers_list));
			if (a_star_goal_path != null && a_star_goal_path.size() > 0) {
				MapLocation next_coord = a_star_goal_path.pop();
				actions.add(Action.createPrimitiveMove(a_footman.ID, get_direction_w_coord(a_footman, next_coord)));
			}
		}
		else {
			for (Direction direction : legal_dir) {
				if (is_move_valid(a_footman.x + direction.xComponent(), a_footman.y + direction.yComponent(), all_game_units)) {
					actions.add(Action.createPrimitiveMove(a_footman.ID, direction));
				}
			}
		}
		for (GameUnit a : get_attackable_archers(a_footman)) {
			actions.add(Action.createCompoundAttack(a_footman.ID, a.ID));
		}
		return actions;
	}



	private Direction get_direction_w_coord(GameUnit an_unit, MapLocation next_coord) {
		if (an_unit.x == next_coord.x) {
			if (an_unit.y - next_coord.y == 1) {
                return Direction.NORTH;
            }
			else if (an_unit.y - next_coord.y == -1) {
                return Direction.SOUTH;
            }
            else {
                return null;
            }
		}
		else if (an_unit.y == next_coord.y) {
			if (an_unit.x - next_coord.x == 1) {
                return Direction.WEST;
            }
            else if (an_unit.x - next_coord.x == -1) {
                return Direction.EAST;
            }
            else {
                return null;
            }
		}
		return null;
	}


    private boolean is_move_valid(int x, int y, List<GameUnit> game_units) {
        int counter = 0;
        if ((x >= 0 && x < xExtent) && (y >= 0 && y < yExtent)) {
            for (GameUnit an_unit : game_units) {
				if (an_unit.x == x && an_unit.y == y) {
                    counter++;
                    if (counter == 2) {
                        return false;
                    }
				}
            }
            return true;
        }
		return false;
	}


    private GameUnit get_closest_archer(GameUnit an_unit, List<GameUnit> archers_list) {
		int min_dist_holder = Integer.MAX_VALUE;

		GameUnit closest_archer_holder = null;
		for (GameUnit a : archers_list) {
            int archer_dist = Math.abs(an_unit.x - a.x) + Math.abs(an_unit.y - a.y);
			if (archer_dist < min_dist_holder){
				min_dist_holder = archer_dist;
				closest_archer_holder = a;
			}
		}
        return closest_archer_holder;
	}

	private List<GameUnit> get_attackable_archers(GameUnit footman_unit) {
		List<GameUnit> attackable_archers_list = new ArrayList<>();
		for (GameUnit a : this.archers) {
			if (FOOTMAN_RANGE >= (Math.abs(footman_unit.x - a.x) + Math.abs(footman_unit.y - a.y))) {
				attackable_archers_list.add(a);
			}
		}
		return attackable_archers_list;
	}


}
