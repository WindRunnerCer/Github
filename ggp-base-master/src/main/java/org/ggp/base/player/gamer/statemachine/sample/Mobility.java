package org.ggp.base.player.gamer.statemachine.sample;

import java.util.List;
import java.util.Random;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 *
 * @author Ceren ALPAY
 */
public final class Mobility extends SampleGamer
{
	public Mobility(){

	}

	/**
	 * Employs a Mobility algorithm.
	 */
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		Move selection = null;
	    StateMachine theMachine = getStateMachine();
	    MachineState Temp_StateMachine;
	    MachineState Temp_StateMachine2;
	    List<Move> temp_moves;
	    int The_Biggest = 0;
		int The_Biggest_Index= 0;


		List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
		if(moves.size()!=0){
		for(int i=0;i<moves.size();i++){
		Temp_StateMachine = theMachine.getRandomNextState(getCurrentState(),getRole(),moves.get(i));
		temp_moves= theMachine.getLegalMoves(Temp_StateMachine,getRole());
		int opponent_selection = new Random().nextInt(temp_moves.size());
		Temp_StateMachine2 = theMachine.getRandomNextState(Temp_StateMachine,getRole(),temp_moves.get(opponent_selection));
		List<Move> temp_moves2= theMachine.getLegalMoves(Temp_StateMachine2,getRole());
		int size = temp_moves2.size();
		System.out.println(size);
		if(temp_moves2.size()>The_Biggest){
		  	The_Biggest = temp_moves2.size();
			The_Biggest_Index = i;
			}
		}

			//Eðer getNextState ile benim Machine deðiþmiyorsa destructive ile dene machine i kopyala kardeþ !

    		selection = moves.get(The_Biggest_Index);

		}

		else
			System.out.println("Bye Bye");

		 return  selection;
	}




}