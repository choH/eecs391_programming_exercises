package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.*;


public class Peasant {

    public Position position;
    public Unit.UnitView unitView;
    public int peasant_id;

    public ResourceNode.Type peasant_cargo_type;
    public int peasant_cargo_amount;

    Peasant(Unit.UnitView unitView){
        this.position = new Position(unitView.getXPosition(), unitView.getYPosition());
        this.unitView = unitView;
        this.peasant_id = unitView.getID();
        this.peasant_cargo_amount = unitView.getCargoAmount();
    }

    @Override
    public boolean equals(Object other){
        if (this == other) {
            return true;
        }
        Peasant other_peasant_unit = (Peasant) other;
        return position == other_peasant_unit.position;
    }

    public void load_cargo(ResourceNode.Type cargo_type, int cargo_amount){
        this.peasant_cargo_type = cargo_type;
        this.peasant_cargo_amount = cargo_amount;
    }



}