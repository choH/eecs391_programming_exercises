package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.agent.minimax.AstarAgent.*;
import edu.cwru.sepia.environment.model.state.*;

import java.util.*;

public class GameUnit {
    public int ID;
	public int x, y;
	public int hp;
	public int damage;

	public GameUnit(Unit.UnitView unit_view) {
		UnitTemplate.UnitTemplateView unit_template_view = unit_view.getTemplateView();
        ID = unit_view.getID();
		x = unit_view.getXPosition();
		y = unit_view.getYPosition();
		hp = unit_view.getHP();
		damage = unit_template_view.getBasicAttack();
	}


	public GameUnit(GameUnit another_GameUnit){
        this.ID = another_GameUnit.ID;
		this.x = another_GameUnit.x;
		this.y = another_GameUnit.y;
		this.hp = another_GameUnit.hp;
		this.damage = another_GameUnit.damage;
	}

}
