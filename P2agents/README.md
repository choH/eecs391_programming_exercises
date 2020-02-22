## First Submission

#### Author: Shiqi Li

### Content:

Add the selection of direction for every step with the update of f value. Using maze_16x16_config.xml as the map.

### Heuristic
## Shiqi (Cathy) LI
* Implement 1st version of A* search with rather inefficient approach.
* Implement 1st version of heuristic calculation.
* Implement almost all helper method to A* search regarding location comparison.

Update to support pythagorean theorem as heuristic.


### Problems:

Cannot prevent selecting blocked locations (with trees):

` marked.contains(current) == false &&resourceLocations.contains(current) == false `  cannot work.

The whole agent cannot process since the replan code hasn't been completed.

Will debug this before the final commit.
