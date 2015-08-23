package org.ggp.base.player.gamer.statemachine.sample;
//package org.ggp.base.player.gamer.statemachine.sample;

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

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public final class NextPlayer extends SampleGamer{

	@Override
		public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException, IOException
		{

			Move selection = null;
			StateMachine theMachine = getStateMachine();
			List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
			String path = "C:\\Users\\MTRC\\Desktop\\eclipse\\out2.txt";
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
			System.out.println(temp);
			writer.close();
				}
			return selection;
		}
	}

