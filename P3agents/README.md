## First Submission

#### Author: Shiqi Li

### Content:


---
## First Week：

### Shiqi (Cathy) LI
* Add content of `alphabetaSearch` function, `getUtility` function and the constructor of `GameState`.

### Shaochen (Henry) ZHONG
* (Done) Implement sketched `orderChildrenWithHeuristics()` method.
* (Working in progress) Working on implementing `getChildren()`.
* (placeholder) Add skeleton for helper method `get_next_moves()`.


---

## Second Week:

### Shiqi (Cathy) LI
* Resolve the inability of getting out of resources in `GameState`。


### Shaochen (Henry) ZHONG
* Implement `MinmaxAlphaBeta`

### Together
* We worked quite extensively on `GameState` due to some unfamiliarity of SEPIA, we look into different resource and found:
    * Implementing a non-SEPIA partial environment helps, but the work is way to extensive.
    * Non-SEPIA partial environment is not good with resources.

We solved the latter problem (with some causes), but we can't do too good of a job on making a non-SEPIA environment while keeping good academic integrity. Therefore we tried to work on an `UnitView` solution (as briefly mentioned in the instruction), and it works relatively fine. We believe our implementation of `MinmaxAlphaBeta` is good, but the `GameState` requires too much edge state to handle.


## Ref.

The references we consulted are:
* https://github.com/clarindaho/eecs391/tree/master/P3agents
* https://github.com/SWhelan/EECS-391
* https://github.com/davidbauer99/EECS391_Assignment2

We also discussed with the Woodman's group, though by the time they haven't got much progress :\



