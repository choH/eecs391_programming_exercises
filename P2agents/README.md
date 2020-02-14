## First Submission

#### Author: Shiqi Li

### Content:

Add the selection of direction for every step with the update of f value. Using maze_16x16_config.xml as the map.

### Problems:

Cannot prevent selecting blocked locations (with trees): 

` marked.contains(current) == false &&resourceLocations.contains(current) == false `  cannot work.

The whole agent cannot process since the replan code hasn't been completed.

