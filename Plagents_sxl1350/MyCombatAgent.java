package eecs_391_sepia_example;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.LocatedAction;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

public class MyCombatAgent extends Agent {
	
	private int enemyPlayerNum = 1;

	public MyCombatAgent(int playernum, String[] otherargs) {
		super(playernum);
		
		if(otherargs.length > 0)
		{
			enemyPlayerNum = new Integer(otherargs[0]);
		}
		
		System.out.println("Constructed MyCombatAgent");
	}

	@Override
	public Map<Integer, Action> initialStep(StateView newstate,
			HistoryView statehistory) {

		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		
		List<Integer> myUnitIDs = newstate.getUnitIds(playernum);
		List<Integer> footmanIDs = new ArrayList<Integer>();
		List<Integer> archersIDs = new ArrayList<Integer>();
		List<Integer> ballistaIDs = new ArrayList<Integer>();


		List<Integer> enemyUnitIDs = newstate.getUnitIds(enemyPlayerNum);
		
		if(enemyUnitIDs.size() == 0)
		{
				return actions;
		}
		
		for(Integer myUnitID : myUnitIDs)
		{
			UnitView unit = newstate.getUnit(myUnitID);
			String unitTypeName = unit.getTemplateView().getName();
			
			if(unitTypeName.equals("Footman"))
				footmanIDs.add(myUnitID);
			else if(unitTypeName.equals("Archer"))
				archersIDs.add(myUnitID);
			else if(unitTypeName.equals("Ballista"))
				ballistaIDs.add(myUnitID);
			else
				System.err.println("Unexpected Unit type: " + unitTypeName);

		}
		
		actions.put(footmanIDs.get(0), Action.createCompoundMove(footmanIDs.get(0),17,8));
			return actions;
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate,
			HistoryView statehistory) {

		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		
		List<Integer> myUnitIDs = newstate.getUnitIds(playernum);
		List<Integer> footmanIDs = new ArrayList<Integer>();
		List<Integer> archersIDs = new ArrayList<Integer>();
		List<Integer> ballistaIDs = new ArrayList<Integer>();

		List<Integer> enemyUnitIDs = newstate.getUnitIds(enemyPlayerNum);
		
		for(Integer myUnitID : myUnitIDs)
		{
			UnitView unit = newstate.getUnit(myUnitID);
			String unitTypeName = unit.getTemplateView().getName();
			
			if(unitTypeName.equals("Footman"))
				footmanIDs.add(myUnitID);
			else if(unitTypeName.equals("Archer"))
				archersIDs.add(myUnitID);
			else if(unitTypeName.equals("Ballista"))
				ballistaIDs.add(myUnitID);
			else
				System.err.println("Unexpected Unit type: " + unitTypeName);

		}
		if(enemyUnitIDs.size() == 0)
		{
			return actions;
		}
		
		int currentStep = newstate.getTurnNumber();
			

		for(ActionResult feedback : statehistory.getCommandFeedback(playernum, currentStep-1).values())
		{
			if(feedback.getFeedback() != ActionFeedback.INCOMPLETE)
			{	

				for (int i=0;i<footmanIDs.size();i++) {
					actions.put(footmanIDs.get(0), Action.createCompoundMove(footmanIDs.get(0),11-i,8+i));
				}
				for (int i=0;i<archersIDs.size();i++) {
					actions.put(archersIDs.get(i), Action.createCompoundMove(archersIDs.get(i), 5-i,13+i));
				}
				
				for (int i=0;i<ballistaIDs.size();i++) {
					actions.put(ballistaIDs.get(i), Action.createCompoundMove(ballistaIDs.get(i), 7-i,11+i));
				}

			}
		}
		
		
		for(ActionResult feedback : statehistory.getCommandFeedback(playernum, currentStep-1).values())
		{
			if(feedback.getFeedback() != ActionFeedback.INCOMPLETE)
			{					
				for (int i=footmanIDs.size()-1;i>0;i--) {
					actions.put(footmanIDs.get(i), Action.createCompoundAttack(footmanIDs.get(i),enemyUnitIDs.get(0)));
				}
				for(Integer archersID : archersIDs)
					actions.put(archersID, Action.createCompoundAttack(archersID,enemyUnitIDs.get(0)));
				}	
			
				for(Integer ballistaID : ballistaIDs) {
					actions.put(ballistaID, Action.createCompoundAttack(ballistaID,enemyUnitIDs.get(0)));
				}

		}
		
		for(ActionResult feedback : statehistory.getCommandFeedback(playernum, currentStep-1).values())
		{
			if(feedback.getFeedback() != ActionFeedback.INCOMPLETE)
			{	
				 int unitID = feedback.getAction().getUnitId();
                 actions.put(unitID, Action.createCompoundAttack(unitID, enemyUnitIDs.get(0)));
			}
		}


		return actions;
		
}

	@Override
	public void terminalStep(StateView newstate, HistoryView statehistory) {
		System.out.println("Finished the episode");
	}

	@Override
	public void savePlayerData(OutputStream os) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadPlayerData(InputStream is) {
		// TODO Auto-generated method stub

	}

}