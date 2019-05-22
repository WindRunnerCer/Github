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

/**
 * @author ALPAY
 */

public final class WMC extends SampleGamer
{
	/**
	 * Employs a "Weighted Monte Carlo" algorithm.
	 * @throws IOException
	 * @throws GoalDefinitionException
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 */

	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException, IOException
	{

		Move selection_real = null;
		//Constructor ekle, durumunu al adamýn ona göre devam etsin
	    int Heuristics[] = {0,1,2,3,4};
	    //StateMachine theMachine;
	    //First, Random, Next, Mobility-M, MC
	    int selection_order = WeightedMonteCarlo(Heuristics, timeout);
	    System.out.println(selection_order);
	    if(selection_order == 0){ //Player First
	    	List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
			Move selection = moves.get(0);
			selection_real=selection;
	    }
	    else if(selection_order == 1){ //Player Random
	    	List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
			Move selection = (moves.get(new Random().nextInt(moves.size())));
			selection_real=selection;
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
			// System.out.println(line);
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
			System.out.println(temp);
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
    	}
	    return selection_real;
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


	int  WeightedMonteCarlo (int heuristics[], long timeout) throws IOException, GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
			//int random = new Random().nextInt(5);
			//System.out.println(random);
			// þimdilik böyle sonradan dinamik olmalý dizinin boyutu
			//long start = System.currentTimeMillis();
			long finishBy = timeout - 2000;
			int Game_statistics[] = new int[5];
			int MC_statistics[] = new int[5];
			int Decision_Array[] = new int[5];
			//StateMachine theMachine = getStateMachine();

			String path = "out.txt";
			File f = new File(path);
			//f.getParentFile().mkdirs();
			if(!f.exists() || f.isDirectory()){
			try {
	            f.createNewFile();
	            FileWriter fw = new FileWriter(f.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				for(int i=0;i<5;i++){
					bw.write("0");
					bw.newLine();
				}
				bw.close();
				fw.close();
	        } catch (FileAlreadyExistsException e) {
	            System.err.println("problem: " + e.getMessage());
	        }
		}
			else{
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				for(int i=0;i<5;i++){
				String line = br.readLine();
				Game_statistics[i] = Integer.parseInt(line);
				System.out.println(Game_statistics[i]);
				}
				//System.out.println(Game_statistics[0]);
				br.close();
				fr.close();
	}
			 //burda karar versin

for (int j = 0; j < 5; j++){
	 		if (System.currentTimeMillis() > finishBy)  //Finish by iþini ayarla!
	        break;

			StateMachine theMachine = getStateMachine();
			//long start = System.currentTimeMillis();
			//long finishBy = timeout - 1000;
			Move selection = null;

			//// Buraya benim adamý yazýcaz hepsinde hangisi dönüyosa onu seç
			if(j == 0){

			List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
			selection = moves.get(0);

			}

			if(j == 1){
				List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
				selection = (moves.get(new Random().nextInt(moves.size())));

			}

			if (j == 2){
				//Move selection = null;
				//StateMachine theMachine = getStateMachine();
				List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
				String path2 = "out2.txt";
				File f2 = new File(path2);
				//f.getParentFile().mkdirs();
				if(!(f2.exists() || f2.isDirectory())){
					try {
						//System.out.println("Abi dosya yok hemen oluþturuyorum");
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
				//System.out.println("cer");
				FileReader fr = new FileReader(f2);
				BufferedReader br = new BufferedReader(fr);
				String line = "0";
				try{
				 line = br.readLine();
				 System.out.println(line);
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
				System.out.println(temp);
				writer.close();
					}
			}

			if(j == 3){
			    //StateMachine theMachine = getStateMachine();
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
			}

			if(j==4){
			//StateMachine theMachine = getStateMachine();
			//long start = System.currentTimeMillis();
			//long finishBy = timeout - 1000;

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

			//long stop = System.currentTimeMillis();
			//notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
			}
	    		//int[] moveTotalPoints = new int[moves.size()];
	    		//int[] moveTotalAttempts = new int[moves.size()];

	    		// Perform depth charges for each candidate move, and keep track
	    		// of the total score and total attempts accumulated for each move.

			if(selection != null){
				int theScore = performDepthChargeFromMove(getCurrentState(), selection);//mymove yaparsýn
				MC_statistics[j] = theScore;
				//System.out.println(MC_statistics[j]);
				//System.out.println("cer bunu kaç kere yazmalý");
			}
	    		   //ÞIMDI DOYADAKI ISTATISTIKLE ÇAPR PUNLARI
	    		   //int[] moveTotalAttempts[i] = 1;
	}
	int Total = 0;
	for (int i=0;i<5;i++){
		Total = Game_statistics[i] + Total;
	}
	for(int i=0;i<5;i++){
		if(!(Total == 0) && Game_statistics[i]/Total!=0){
			Decision_Array [i] = Game_statistics[i]/Total * MC_statistics[i];
		}

		else{
			System.out.println("gerçek hesaplama else");
			Decision_Array[i] = MC_statistics[i];
		}
	}

	int Biggest = 0;
	int Biggest_Index = 0;
	for(int i=0; i<5; i++){
		if(Biggest < Decision_Array[i])
			Biggest = Decision_Array[i];
			Biggest_Index = i; //The heuristic number
	}

	PrintWriter writer = new PrintWriter(f);
	for (int i = 0; i<5;i++){
		if(i == Biggest_Index){
			writer.println(Game_statistics[i]+1);
		}
		else writer.println(Game_statistics[i]);
	}
	writer.close();

	//long stop = System.currentTimeMillis();

	//notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
	System.out.println("this hands end");
	return Biggest_Index;



	}
}
