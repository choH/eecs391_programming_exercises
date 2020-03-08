// package astar;
package edu.cwru.sepia.agent.minimax;

// Shaochen (Henry) ZHONG and Shiqi (Cathy) Li.

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.*;
import edu.cwru.sepia.util.*;

import java.io.*;
import java.util.*;




public class AstarAgent {

    // Special constructor for MinimaxAlphaBeta project (use in altered findPath()).
    private int xExtent, yExtent;
    public AstarAgent(int xExtent, int yExtent) {
        this.xExtent = xExtent;
        this.yExtent = yExtent;
    }


    class MapLocation {
        public int x, y;
        public MapLocation parent;
        public int g;
        public float h;
        public float f;




        public MapLocation(int x, int y, MapLocation parent, float h) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.h = h;
        }

        public MapLocation(int x, int y, MapLocation parent, float h, int g) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.h = h;
            this.g = g;
        }



        public boolean equals(Object o) {
            if (o != null && o instanceof MapLocation) {
                MapLocation o_holder = (MapLocation) o;
                if (this.x == o_holder.x && this.y == o_holder.y) {
                    return true;
                }
            }
            return false;
        }
    }




    public Stack<MapLocation> findPath(List<ResourceNode.ResourceView> obstacles, GameUnit player, GameUnit enemy) {

        MapLocation startLoc = new MapLocation(player.x, player.y, null, 0);
        MapLocation goalLoc = new MapLocation(enemy.x, enemy.y, null, 0);

        Set<MapLocation> resourceLocations = new HashSet<MapLocation>();
        for (ResourceNode.ResourceView resource : obstacles) {
            resourceLocations.add(new MapLocation(resource.getXPosition(), resource.getYPosition(), null, 0));
        }

        return AstarSearch(startLoc, goalLoc, xExtent, yExtent, null, resourceLocations);
    }







    /**
     * This is the method you will implement for the assignment. Your implementation
     * will use the A* algorithm to compute the optimum path from the start position to
     * a position adjacent to the goal position.
     *
     * Therefore your you need to find some possible adjacent steps which are in range
     * and are not trees or the enemy footman.
     * Hint: Set<MapLocation> resourceLocations contains the locations of trees
     *
     * You will return a Stack of positions with the top of the stack being the first space to move to
     * and the bottom of the stack being the last space to move to. If there is no path to the townhall
     * then return null from the method and the agent will print a message and do nothing.
     * The code to execute the plan is provided for you in the middleStep method.
     *
     * As an example consider the following simple map
     *
     * F - - - -
     * x x x - x
     * H - - - -
     *
     * F is the footman
     * H is the townhall
     * x's are occupied spaces
     *
     * xExtent would be 5 for this map with valid X coordinates in the range of [0, 4]
     * x=0 is the left most column and x=4 is the right most column
     *
     * yExtent would be 3 for this map with valid Y coordinates in the range of [0, 2]
     * y=0 is the top most row and y=2 is the bottom most row
     *
     * resourceLocations would be {(0,1), (1,1), (2,1), (4,1)}
     *
     * The path would be
     *
     * (1,0)
     * (2,0)
     * (3,1)
     * (2,2)
     * (1,2)
     *
     * Notice how the initial footman position and the townhall position are not included in the path stack
     *
     * @param start Starting position of the footman
     * @param goal MapLocation of the townhall
     * @param xExtent Width of the map
     * @param yExtent Height of the map
     * @param resourceLocations Set of positions occupied by resources
     * @return Stack of positions with top of stack being first move in plan
     */

    private Stack<MapLocation> AstarSearch(MapLocation start, MapLocation goal, int xExtent, int yExtent, MapLocation enemyFootmanLoc, Set<MapLocation> resourceLocations) {

        // System.out.print("ININ ASTARSEARCH" + "\n");

        boolean goal_found_flag = false;
        MapLocation start_node = new MapLocation(start.x, start.y, null, 0);
        start_node.g = 0;
        start_node.h = heuristic(start_node, goal);
        start_node.f = start_node.g + start_node.h;


        boolean[][] resource_LUT = new boolean[xExtent][yExtent];
        for (MapLocation a_resource_location : resourceLocations) {
            resource_LUT[a_resource_location.x][a_resource_location.y] = true;
        }


        List<MapLocation> open_list = new ArrayList<MapLocation>();
        List<MapLocation> closed_list = new ArrayList<MapLocation>();



        open_list.add(start_node);
        while (!open_list.isEmpty()) {
            MapLocation current_node = open_list.get(0);

            int current_index, counter;
            current_index = counter = 0;
            for (MapLocation i : open_list) {
                if (i.f < current_node.f) {
                    current_node = i;
                    current_index = counter;
                }
                counter++;
            }
            open_list.remove(current_index);
            closed_list.add(current_node);



            // System.out.print("current_node: (" + current_node.x + ", " + current_node.y + ")\t" + current_node.f + " = " + current_node.g + " + "  + current_node.h + "\n");
            // System.out.print("goal_node: (" + goal.x + ", " + goal.y + ")\n");


            if (current_node.equals(goal)) {
                Stack<MapLocation> goal_path = new Stack<MapLocation>();
                MapLocation cursor = null;
                if (current_node != null) {
                    cursor = current_node.parent;
                    // System.out.print("cursor p: (" + cursor.x + ", " + cursor.y + ")\n");
                }


                while (cursor != null) {
                    goal_path.push(cursor);
                    // System.out.print("cursor: (" + cursor.x + ", " + cursor.y + ")\n");
                    cursor = cursor.parent;
                }

                goal_path.pop();

                // for (MapLocation i : goal_path) {
                //     System.out.print("goal_path: (" + i.x + ", " + i.y + ")\n");
                // }
                return goal_path;
            }

            // System.out.print("before action_list" + "\n");



            List<MapLocation> childern_list = new ArrayList<MapLocation>();
            // MapLocation[] action_list = new MapLocation[8];
            // action_list[0] = new MapLocation(current_node.x - 1, current_node.y + 1, current_node, 0);
            // action_list[1] = new MapLocation(current_node.x, current_node.y + 1, current_node, 0);
            // action_list[2] = new MapLocation(current_node.x + 1, current_node.y + 1, current_node, 0);
            // action_list[3] = new MapLocation(current_node.x - 1, current_node.y, current_node, 0);
            // action_list[4] = new MapLocation(current_node.x + 1, current_node.y, current_node, 0);
            // action_list[5] = new MapLocation(current_node.x - 1, current_node.y - 1, current_node, 0);
            // action_list[6] = new MapLocation(current_node.x, current_node.y - 1, current_node, 0);
            // action_list[7] = new MapLocation(current_node.x + 1, current_node.y - 1, current_node, 0);

            MapLocation[] action_list = new MapLocation[4];
            action_list[0] = new MapLocation(current_node.x, current_node.y + 1, current_node, 0);
            action_list[1] = new MapLocation(current_node.x - 1 , current_node.y, current_node, 0);
            action_list[2] = new MapLocation(current_node.x + 1, current_node.y, current_node, 0);
            action_list[3] = new MapLocation(current_node.x, current_node.y - 1, current_node, 0);


            // System.out.print("after action_list" + "\n");

            for (MapLocation an_action : action_list) {
                if (!is_pos_valid(an_action, xExtent, yExtent, resource_LUT)) {
                    continue;
                }
                MapLocation new_action = new MapLocation(an_action.x, an_action.y, current_node, 0);
                childern_list.add(new_action);
            }

            // System.out.print("after is_pos_valid" + "\n");

            for (MapLocation a_child : childern_list) {
                boolean continue_flag = false;
                for (MapLocation closed_child : closed_list) {
                    if (a_child.equals(closed_child)) {
                        continue_flag = true;
                        break;
                    }
                }

                if (continue_flag) {
                    continue;
                }

                a_child.g = current_node.g + 1;
                a_child.h = heuristic(a_child, goal);
                a_child.f = a_child.g + a_child.h;

                for (MapLocation open_node : open_list) {
                    if (a_child.equals(open_node) && a_child.g > open_node.g) {
                        continue_flag = true;
                        break;
                    }
                }
                if (continue_flag) {
                    continue;
                }
                open_list.add(a_child);
            }

            // System.out.print("after childern_list" + "\n\n\n");

        }
        // System.out.print("No path found" + "\n");
        return null;


        // ///
        //
        // System.out.print("before result" + "\n");
        //
        // Stack<MapLocation> result = new Stack<MapLocation>();
		// while (!goal_path.isEmpty()) {
		// 	result.push(goal_path.pop());
		// }
        //
        // System.out.print("after result" + "\n");
		// return result; // output optimal past as required.
    }

    private boolean is_pos_valid(MapLocation tar_pos, int xExtent, int yExtent, boolean[][] resource_LUT) {
        if ((tar_pos.x >= 0 && tar_pos.x < xExtent) && (tar_pos.y >= 0 && tar_pos.y < yExtent)) {
            if (!resource_LUT[tar_pos.x][tar_pos.y]) {
                return true;
            }
        }
		return false;
	}

    private boolean is_same_pos(MapLocation src, MapLocation dest) {
        if (src == null || dest == null) {
            return false;
        }

		return (src.x == dest.x) && (src.y == dest.y);
	}

    private float heuristic(MapLocation current_pos, MapLocation goal) {
		// if (Math.abs(goal.x - current_pos.x) > Math.abs(goal.y - current_pos.y)) {
        //     return (float) Math.abs(goal.x - current_pos.x);
        // }
        // return (float) Math.abs(goal.y - current_pos.y);
        // return (float) (Math.pow((Math.abs(goal.x - current_pos.x)), 2) +  Math.pow((Math.abs(goal.y - current_pos.y)), 2));
        if (current_pos != null && goal != null) {

            // return DistanceMetrics.chebyshevDistance(current_pos.x, current_pos.y, goal.x, goal.y);
            return Math.abs(goal.x - current_pos.x) + Math.abs(goal.y - current_pos.y) - 2;
        }
        return Float.MAX_VALUE;
	}

}
