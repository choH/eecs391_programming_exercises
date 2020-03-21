## Programming 4



### Week 1 Commit

#### Shiqi Li's Contribution (early submission on 3/13/2020)
Add content of constructor of GameState, heuristic, compareTo, equals.

Create new method getOptimalResources.


#### Henry Zhong's Contribution

* Plan to implement `StripsAction` modules `Deposit`, `Harvest`, and `Move`.
* Actually implement basic inherit methods in `Deposit` and `Move`.
* Create `Peasant` with from its `unitView`, and implement some basic coupling method within `GameState` class.
* (Maybe) plan to implement townhall and goldmine class, or maybe a wrapper class of these two -- as the `Position` class includes couple of useful method and it is painful to strips to `UnitView` to do geo-checks/`ResourceType` checks.


#### Henry Zhong's Confusion
* Nothing yet, but it seems heavy to understand all the provided helper methods. Also due to the `Peasant` class I created, I might have to refactor my `AstarAgent` from P3agents to a large degree.