# Programming Exercise 1

> Shaochen (Henry) ZHONG, `sxz517`

---

## FirstAgent.java

Created an agent which is able to create `barrack`, `farm`, and `footman` upon resource availability. Specified the `barrack` location, and made `footman` from the `barrack` location.

## MyCombatAgent

Create an agent which commands two `archer`, two `ballista`, and three `footman` units to combat 5 enemy `footman` units born under the `tower`. One footman was specifically sent to draw enemies from `tower` to reduce casualty from `tower`. The other units are set relatively stationary so that more time can be used on attacking.

Eliminated 3 enemy `footman` at the end.

---

## Experience
Grasped a general understanding of the structure and mechanism of the `SEPIA` platform. Gained basic knowledge on creating new units, commanding units, and change the status of occupying units.


## Confusion

With my groupmate, we are deeply troubled on controlling the `middleStep` loop contents according to the situation of the arena, but not just blindly following the couple loops we placed. We also find it is difficult on parameter tuning -- as many time the general strategy is the same, but a tweak on some coordinate values will have a significant impact on the result. After writing our code, we discovered some "magic numbers" which optimized the performance of our strategy, but lacks fundamental understanding on why it happened.


