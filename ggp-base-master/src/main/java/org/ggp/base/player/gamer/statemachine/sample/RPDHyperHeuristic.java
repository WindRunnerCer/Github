package org.ggp.base.player.gamer.statemachine.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;



public final class RPDHyperHeuristic extends SampleGamer {

	int point = 0;
    int Heuristic_count = 0;
    List<Integer> selection_order = new ArrayList<Integer>();

	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException, IOException
	{
		Move selection = null;
	    StateMachine theMachine = getStateMachine();


	    if(theMachine.getInitialState().equals(getCurrentState())){
	    	System.out.println("Abi bu daha ilk el ya. Cuma olsa da gitsek !");
	    	int random_player = 0;
	    	random_player = new Random().nextInt(5);
	    	while(selection_order.size()<5){
	    		if(selection_order.isEmpty()){
	    			selection_order.add(random_player);
	    			}

	    		else{
	    			while(selection_order.size() < 5){
	    				if(selection_order.contains(random_player))
	    					random_player = new Random().nextInt(5);
	    				else selection_order.add(random_player);
	    		 		}
	    			}
	    		}

	    	selection = play(selection_order.get(Heuristic_count%5),timeout);
	    	point = performDepthChargeFromMove(getCurrentState(), selection);
	    	System.out.println("ilk el");
	    	System.out.println(Heuristic_count%5);
	    	System.out.println(point);

	    	return selection;
	    }

	    else{
	    selection = play(selection_order.get(Heuristic_count%5),timeout);
	    int temp_point = performDepthChargeFromMove(getCurrentState(), selection);
	    System.out.println("ben temp pointim");
	    System.out.println(temp_point);
	    if(point !=0 && temp_point>=point){
	    	selection = play(selection_order.get(Heuristic_count%5),timeout);
	    	point = temp_point;
	    	System.out.println("ilk else");
	        System.out.println(point);
	    }
	    else {
	    	Heuristic_count++;
	    	selection = play(selection_order.get(Heuristic_count%5),timeout);
	    	point = performDepthChargeFromMove(getCurrentState(), selection);
	    	System.out.println("ikinci else");
	    	System.out.println(point);
	    }

	    	return selection;
	    }

}
	private int[] depth = new int[1];
	private int performDepthChargeFromMove(MachineState theState, Move myMove) {
		    StateMachine theMachine = getStateMachine();
		    try {
	            MachineState finalState = theMachine.performDepthCharge(theMachine.getRandomNextState(theState, getRole(), myMove), depth);
	            return theMachine.getGoal(finalState, getRole());
	        } catch (Exception e) {
	            e.printStackTrace();
	            return 0;
	        }
	  }
	public Move play(int selection_index, long timeout) throws IOException, MoveDefinitionException, TransitionDefinitionException{
		StateMachine theMachine = getStateMachine();
		Move selection = null;
		if(selection_index == 0){//First
   		 List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
		 selection = moves.get(0);
   		 //returnselection;
   	 }
   	 if(selection_index == 1){//Random
   		 List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		 selection = (moves.get(new Random().nextInt(moves.size())));
		 //return selection;
   	 }
   	 if(selection_index == 2){//Next
   		 List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
				String path2 = "out2.txt";
				File f2 = new File(path2);
				//f.getParentFile().mkdirs();
				if(!(f2.exists() || f2.isDirectory())){
					try {
						f2.createNewFile();
						FileWriter fw = new FileWriter(f2.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write("0");
						bw.close();
						fw.close();
					} catch (FileAlreadyExistsException e) {
						System.err.println("problem: " + e.getMessage());
						}
					selection = moves.get(0);
					}
				else if(f2.exists()&& !f2.isDirectory()){
				FileReader fr = new FileReader(f2);
				BufferedReader br = new BufferedReader(fr);
				String line = "0";
				try{
				 line = br.readLine();
				 if(Integer.parseInt(line)>= moves.size()){
					 selection = moves.get(0);
					 line = "0";
					 }
				 else
					 selection = moves.get(Integer.parseInt(line));
				br.close();
				fr.close();
				}
				catch(FileNotFoundException e){
					System.err.println("Böyle dosya yok ki "+ e);
					}

				PrintWriter writer = new PrintWriter(f2);
				int temp = Integer.parseInt(line);
				++temp;
				writer.println(temp);
				writer.close();
					}
   		 //return selection;
   	 }

   	 if(selection_index == 3){//Mobility
   		 //Move selection = null;
   		   // StateMachine theMachine = getStateMachine();
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
   			//int size = temp_moves2.size();
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

   		 //return selection;
   	 }

   	 if(selection_index == 4){
   		 long finishBy = timeout - 2000;
   		 List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
			 selection = moves.get(0);
			 if (moves.size() > 1) {
	    		int[] moveTotalPoints = new int[moves.size()];
	    		int[] moveTotalAttempts = new int[moves.size()];

	    		// Perform depth charges for each candidate move, and keep track
	    		// of the total score and total attempts accumulated for each move.
	    		for (int i = 0; true; i = (i+1) % moves.size()) {
	    		    if (System.currentTimeMillis() > finishBy)
	    		        break;

	    		    int theScore = performDepthChargeFromMove(getCurrentState(), moves.get(i));
	    		    moveTotalPoints[i] += theScore;
	    		    moveTotalAttempts[i] += 1;
	    		}

	    		// Compute the expected score for each move.
	    		double[] moveExpectedPoints = new double[moves.size()];
	    		for (int i = 0; i < moves.size(); i++) {
	    		    moveExpectedPoints[i] = (double)moveTotalPoints[i] / moveTotalAttempts[i];
	    		}

	    		// Find the move with the best expected score.
	    		int bestMove = 0;
	    		double bestMoveScore = moveExpectedPoints[0];
	    		for (int i = 1; i < moves.size(); i++) {
	    		    if (moveExpectedPoints[i] > bestMoveScore) {
	    		        bestMoveScore = moveExpectedPoints[i];
	    		        bestMove = i;
	    		    	}
	    			}
	    		selection = moves.get(bestMove);
				}

   		 //return selection;
   	 	}
	return selection;
	}
}


