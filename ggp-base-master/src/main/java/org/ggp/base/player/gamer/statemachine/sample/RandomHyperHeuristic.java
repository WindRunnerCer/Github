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
import java.util.List;
import java.util.Random;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public final class RandomHyperHeuristic extends SampleGamer {

	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException, IOException
	{

	Move selection_real = null;
    //First, Random, Next, Mobility-M, MC
    int selection_order = new Random().nextInt(5);
    System.out.println(selection_order);

    if(selection_order == 0){ //Player First
    	List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		Move selection = moves.get(0);
		selection_real=selection;
		//System.out.println("cer0");
    }
    else if(selection_order == 1){ //Player Random
    	List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		Move selection = (moves.get(new Random().nextInt(moves.size())));
		selection_real=selection;
		//System.out.println("cer1");
    }

    else if(selection_order == 2){// Player Next
    	Move selection = null;
		StateMachine theMachine = getStateMachine();
		List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
		String path = "out2.txt";
		File f = new File(path);
		//f.getParentFile().mkdirs();
		if(!(f.exists() || f.isDirectory())){
			try {
				//System.out.println("Abi dosya yok hemen oluþturuyorum");
				f.createNewFile();
				FileWriter fw = new FileWriter(f.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("0");
				bw.close();
				fw.close();
			} catch (FileAlreadyExistsException e) {
				System.err.println("problem: " + e.getMessage());
				}
			selection = moves.get(0);
			}
		else if(f.exists()&& !f.isDirectory()){
		//System.out.println("cer");
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String line = "0";
		try{
		 line = br.readLine();
		 System.out.println(line);
		 if(Integer.parseInt(line)>= moves.size())
		{selection = moves.get(0);
		 line = "0";}
		 else
			 selection = moves.get(Integer.parseInt(line));
		br.close();
		fr.close();
		}
		catch(FileNotFoundException e){
			System.err.println("Böyle dosya yok ki "+ e);
			}

		PrintWriter writer = new PrintWriter(f);
		int temp = Integer.parseInt(line);
		++temp;
		writer.println(temp);
		//System.out.println(temp);
		writer.close();
			}
		return selection;
    }

    else if(selection_order == 3){//Player Mobility-M
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
		Temp_StateMachine2 = theMachine.getRandomNextState(Temp_StateMachine,getRole(),moves.get(opponent_selection));
		List<Move> temp_moves2= theMachine.getLegalMoves(Temp_StateMachine2,getRole());
		if(temp_moves2.size()==i)
			break;
		//int size = temp_moves2.size();
		//System.out.println(size);
		if(temp_moves2.size()>The_Biggest){
		  	The_Biggest = temp_moves2.size();
			The_Biggest_Index = i;
			}
		}

			//Eðer getNextState ile benim Machine deðiþmiyorsa destructive ile dene machine i kopyala kardeþ !

    		selection = moves.get(The_Biggest_Index);

		}

		selection_real =  selection;
		//System.out.println("cer3");
	}

    else if(selection_order == 4){//Player MC

    	StateMachine theMachine = getStateMachine();
		long start = System.currentTimeMillis();
		long finishBy = timeout - 1000;

		List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
		Move selection = moves.get(0);
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

		long stop = System.currentTimeMillis();

		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		selection_real = selection;
		//System.out.println("cer4");
		}
    return selection_real;

	}


	private int[] depth = new int[1];
	int performDepthChargeFromMove(MachineState theState, Move myMove) {
	    StateMachine theMachine = getStateMachine();
	    try {
            MachineState finalState = theMachine.performDepthCharge(theMachine.getRandomNextState(theState, getRole(), myMove), depth);
            return theMachine.getGoal(finalState, getRole());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
	}
}







