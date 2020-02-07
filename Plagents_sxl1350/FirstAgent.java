package eecs_391_sepia_example;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Template.TemplateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

public class FirstAgent extends Agent {

	private static final long serialVersionUID = -7481143097108592969L;

	public FirstAgent(int playernum) {
		super(playernum);
				
		System.out.println("Constructed My First Agent");
	}

	public Map initialStep(StateView newstate, HistoryView statehistory) {
		return middleStep(newstate, statehistory);
	}

	public Map middleStep(StateView newstate, HistoryView statehistory) {
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		
		List<Integer> myUnitIds = newstate.getUnitIds(playernum);
		List<Integer> peasantIds = new ArrayList<Integer>();
		List<Integer> townhallIds = new ArrayList<Integer>();
		List<Integer> farmIds = new ArrayList<Integer>();
		List<Integer> barracksIds = new ArrayList<Integer>();
		List<Integer> footmanIds = new ArrayList<Integer>();
		
		for(Integer unitID : myUnitIds)
		{
			UnitView unit = newstate.getUnit(unitID);	
			String unitTypeName = unit.getTemplateView().getName();	
			if(unitTypeName.equals("TownHall"))
				townhallIds.add(unitID);
			else if(unitTypeName.equals("Peasant"))
				peasantIds.add(unitID);
			else if(unitTypeName.equals("Footman"))
				footmanIds.add(unitID);
			else if(unitTypeName.equals("Farm"))
				farmIds.add(unitID);
			else if(unitTypeName.equals("Barracks"))
				barracksIds.add(unitID);
			else
				System.err.println("Unexpected Unit type: " + unitTypeName);
		}
		
		int currentGold = newstate.getResourceAmount(playernum, ResourceType.GOLD);
		int currentWood = newstate.getResourceAmount(playernum, ResourceType.WOOD);
		
		List<Integer> goldMines = newstate.getResourceNodeIds(Type.GOLD_MINE);
		List<Integer> trees = newstate.getResourceNodeIds(Type.TREE);
		
		for(Integer peasantID : peasantIds)
		{
			Action action = null;
			if(newstate.getUnit(peasantID).getCargoAmount() > 0)
			{
				action = new TargetedAction(peasantID, ActionType.COMPOUNDDEPOSIT, townhallIds.get(0));
			}
			else
			{
				if(currentGold < currentWood)
				{
					action = new TargetedAction(peasantID, ActionType.COMPOUNDGATHER, goldMines.get(0));
				}
				else
				{
					action = new TargetedAction(peasantID, ActionType.COMPOUNDGATHER, trees.get(0));
				}
			}
			actions.put(peasantID, action);
		}	
		if(farmIds.size() < 1)
		{
		        if(currentGold >= 500 && currentWood >= 250)
		        {
		                
		                TemplateView farmTemplate = newstate.getTemplate(playernum, "Farm");
		                int farmTemplateID = farmTemplate.getID();

		               
		                int peasantID = peasantIds.get(0);

		               
		                actions.put(peasantID, Action.createCompoundBuild(peasantID, farmTemplateID, 23,6));
		        }
		}
		
		else if(barracksIds.size() < 1)
		{
		        if(currentGold >= 700 && currentWood >= 400)
		        {
		                
		                TemplateView barrackTemplate = newstate.getTemplate(playernum, "Barracks");
		                int barrackTemplateID = barrackTemplate.getID();
		                int peasantID = peasantIds.get(0);

		               
		                actions.put(peasantID, Action.createCompoundBuild(peasantID, barrackTemplateID, 5,16));
		        }
		}
		else if(footmanIds.size() < 2)
		{
		        if(currentGold >= 600)
		        {
		                TemplateView footmanTemplate = newstate.getTemplate(playernum, "Footman");
		                int footmanTemplateID = footmanTemplate.getID();

		                int barrackID = barracksIds.get(0);

		                actions.put(barrackID, Action.createCompoundProduction(barrackID, footmanTemplateID));
		        }
		}
		
		return actions;
	}

	public void terminalStep(StateView newstate, HistoryView statehistory) {
		System.out.println("Finsihed the episode");
	}

	public void savePlayerData(OutputStream os) {
	}

	public void loadPlayerData(InputStream is) {
	}

}