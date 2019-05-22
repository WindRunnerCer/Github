package org.ggp.base.player.gamer.statemachine.sample;

import java.io.IOException;
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

/**
 * @author ALPAY
 */

public class Select_Next extends SampleGamer {

	/**
	 * Employs a "Select Next" algorithm.
	 * @throws IOException
	 * @throws GoalDefinitionException
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 */
	   @Override
		public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
		{
			Move selection = null;
		    StateMachine theMachine = getStateMachine();
		    MachineState Temp_StateMachine;
		    List<Move> temp_moves;
		    int The_Biggest = 0;
			int The_Biggest_Index= 0;


			List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
			if(moves.size()!=0){
			for(int i=0;i<moves.size();i++){
			Temp_StateMachine = theMachine.getRandomNextState(getCurrentState(),getRole(),moves.get(i));
			temp_moves= theMachine.getLegalMoves(Temp_StateMachine,getRole());
			int size = temp_moves.size();
			System.out.println(size);
			if(temp_moves.size()>The_Biggest){
			  	The_Biggest = temp_moves.size();
				The_Biggest_Index = i;
				}
			}

				//Eðer getNextState ile benim Machine deðiþmiyorsa destructive ile dene machine i kopyala kardeþ !
	    		// Perform depth charges for each candidate move, and keep track
	    		// of the total score and total attempts accumulated for each move.

	    		selection = moves.get(The_Biggest_Index);

			}

			return selection;
		}




	}

