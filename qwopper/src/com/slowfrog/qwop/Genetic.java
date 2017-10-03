package com.slowfrog.qwop;

import java.awt.Robot;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 
 * @author Laurent Vaucher
 * Extended by Steven Ray
 * 
 * Running this class will begin executing the 
 * genetic algorithm on the population.
 * 
 * For the program to inteface with QWOP and execute 
 * its instructions, the game must fully visible in 
 * your web browser on your monitor
 * 
 * The results of the execution will be saved to an 
 * output file titled "evoOut.txt."
 *
 */

public class Genetic {
  //private static final String NOTES = "QWOPqwop++";  //Encoding 1  
  private static final String NOTES = "ABCDEFGHIJKLMNOP"; //Encoding 2
  private static final int RUN_TIME_LIMIT_MILLIS = 60000;
  private static final String EVO_LOG_FILENAME = "evo_log_2013_26_1min_RND.txt";
  private static final String GEN_LOG_FILENAME = "gen_log_2013_26_1min_RND.txt";
  private static final int MAX_RUNS = 1;
  private static final int MAX_GENERATIONS = 100;
  private static final int POPULATION_MAX_ROWS = 5;
  private static final int POPULATION_MAX_COLS = 6;
  private static final int POPULATION_MAX_SIZE = POPULATION_MAX_ROWS + POPULATION_MAX_COLS;
  private static final int POPULATION_MAX_CONNECTIONS = 4;
  private static final Log LOG = new ConsoleLog();

  /**
   * main method
   * @param args the arguments
   */
  public static void main(String[] args) {
    Genetic g = new Genetic();
  }

  private final Map<String, Individual> population;
  private WrapGrid<Individual> curGen, nextGen; 
  
  private final List<Individual> goodRunnerList;
  private float fitnessSum = 0;
  private Robot rob;
  private Qwopper qwopper;
  private PrintStream evoOut, genOut; //evolution log and generation log streams
  private float genAvgFitness;
  private boolean currentRunnerCrashed;
  private final boolean randomGen0 = true; //set to true is using a random gen stored in runs3.txt

  public Genetic() {
    this.population = new HashMap<String, Individual>();
    if(randomGen0)
    {
    	this.readPopulation("runs3.txt"); //file containing the bad generation 0
    }
    else
    {
    	this.readPopulation("runs2.txt"); //contains an unfiltered list of random runners
    }    
    System.out.println("Population: " + this.population.size() + " individuals");
    int totalRuns = 0;
    float numRuns;
    
    for (Individual iRunner : this.population.values()) {
      totalRuns += iRunner.runs.size();
    }
    System.out.println("Total runs: " + totalRuns);

    if(!randomGen0) //alternate Gen0 is filtered from the randomly generated runner list
    {
    	IFilter<RunInfo> twoMetersNotCrashed = new AndFilter<RunInfo>(
    	        new MinDistFilter(2), new NotFilter<RunInfo>(new CrashedFilter()));
	    IFilter<Individual> individualFilter = new MinRatioFilter(
	        twoMetersNotCrashed);
	    goodRunnerList = this.filter(individualFilter);  
	    System.out.println("Good Runners: ");
		for (Individual aGoodRunnerList : goodRunnerList) {
			System.out.println(aGoodRunnerList.toString());
			System.out.println(aGoodRunnerList.str);
			System.out.println(aGoodRunnerList.runs.get(0).distance);
		}
    }
    else
    {
    	goodRunnerList = new ArrayList<Individual>();
    	//runs3 currently contains 30 random runners. if the flag is set, use this as gen0
		goodRunnerList.addAll(this.population.values());
    }    
    
    //init grid for a population of 30 individuals and 4 connections each
    curGen = new WrapGrid<Individual>(POPULATION_MAX_ROWS, POPULATION_MAX_COLS, POPULATION_MAX_CONNECTIONS);	
    
	//store each individual's string and average distance traveled for manipulation
	  for (Individual aGoodRunnerList : goodRunnerList) {

		  float sumFits = 0;
		  for (int j = 0; j < aGoodRunnerList.runs.size(); j++) {
			  sumFits += aGoodRunnerList.runs.get(j).distance;
		  }
		  numRuns = aGoodRunnerList.runs.size();
		  aGoodRunnerList.fitness = sumFits / numRuns;

		  //add individual with calculated fitness to the grid
		  curGen.add(aGoodRunnerList);
	  }
	
	try {
        rob = new Robot();
        qwopper = new Qwopper(rob, LOG);
        qwopper.findRealOrigin();
		evoOut = new PrintStream(new FileOutputStream(EVO_LOG_FILENAME, true));
		genOut = new PrintStream(new FileOutputStream(GEN_LOG_FILENAME, true));
		  
		//log Gen 0
		evoOut.println("##########  Generation 0  ##########");
		for(int i = 0; i < POPULATION_MAX_SIZE; i++){				    
			evoOut.println(i + ") " + curGen.get(i).str + "    avgFitness: " + curGen.get(i).fitness); 
		}
		evoOut.println("###################################");
		
		for(int genCnt = 1; genCnt<=MAX_GENERATIONS; genCnt++)
	    {			    
			evoOut.println("##########  " + "Generation " + genCnt + "  ##########");
			genOut.println("##########  " + "Generation " + genCnt + "  ##########\n");
			
			genAvgFitness = 0;
			
		
			for(int i = 0; i < POPULATION_MAX_SIZE; i++){
				evoOut.println("----------  " + "Individual " + (i+1) + "  ----------");					
				
				//test string specified number of times, average the distance
				
				//after generation 1, each individual gets tested during reproduction of the previous generation
				if(genCnt == 1) 
				{
					testString(qwopper, curGen.get(i).str, MAX_RUNS, EVO_LOG_FILENAME); 
				    curGen.get(i).fitness = fitnessSum/MAX_RUNS;
				}	
				
			    genOut.println((i+1) + ")  " + curGen.get(i).fitness + "  |  " + curGen.get(i).str);
			    evoOut.println((i+1) + ")  " + curGen.get(i).fitness + "  |  " + curGen.get(i).str);
				
			    genAvgFitness += curGen.get(i).fitness;		//sum up average generational fitness
			}
			
			//log this generation's average fitness				
			LOG.log("Generation " + genCnt + " Average Fitness: " + genAvgFitness/POPULATION_MAX_SIZE);
			evoOut.println("Generation " + genCnt + " Average Fitness: " + genAvgFitness/POPULATION_MAX_SIZE);
			genOut.println("Generation " + genCnt + " Average Fitness: " + genAvgFitness/POPULATION_MAX_SIZE + "\n");
			
			//select and mate the parents, mutate the children, and generate the next generation				
			nextGen = crossoverSteadyStateGrid(curGen);		
			
			curGen = nextGen;
						    
	    }//end generation loop

		evoOut.flush();
		evoOut.close();
			    
        
    } catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (Throwable t) {
		LOG.log("Error: ", t);
	}	
    
  }
  
  //copied from MAIN
  private void testString(Qwopper qwop, String str, 
		  					int count, String filename) {
	  fitnessSum = 0;
	  	
	  for (int i = 0; i < count; ++i) {
		  //LOG.logf("Run #%d\n", i);
	      qwop.startGame();
	      RunInfo info = qwop.playOneGame(str, RUN_TIME_LIMIT_MILLIS);
	      LOG.log(info.toString());
	      LOG.log(info.marshal());
	      evoOut.println(info.toString());
		  evoOut.println(info.marshal());      
	      
	      //info.distance goes into sumfitness
	      fitnessSum += info.distance;	    
	      currentRunnerCrashed = info.crashed;
	      
	  }
  }
  
  //tests a child runner a specified number of times and returns the average fitness
  private float testChild(Qwopper qwop, String str, int runLimit) {
		float fitness = 0;
		evoOut.println("Testing child: " + str);
		LOG.log("Testing child: " + str);
		for (int i = 0; i < runLimit; ++i) {	//currently we only test each child once
			qwop.startGame();
			RunInfo info = qwop.playOneGame(str, RUN_TIME_LIMIT_MILLIS);
			LOG.log(info.toString());
		    LOG.log(info.marshal());
		    evoOut.println(info.toString());
		    evoOut.println(info.marshal());		    
			
			//info.distance goes into sumfitness
			fitness += info.distance;
			currentRunnerCrashed = info.crashed;			
		}
		
		return fitness/runLimit;
  }

  public List<Individual> filter(IFilter<Individual> filter) {
    List<Individual> ret = new ArrayList<Individual>();
    for (Individual individual : this.population.values()) {
      if (filter.matches(individual)) {
        ret.add(individual);
      }
    }
    return ret;
  }
  
  //finds the fittest of the neighbors of the individual located at curPop[curRow][curCol]
  private Individual fittestNeighbor(int curRow, int curCol, WrapGrid<Individual> curPop)
  {
	  Individual up, 
	  			 right, 
	  			 left, 
	  			 down, 
	  			 fittest;
	  
	  up 	= curPop.get(curRow - 1	, curCol	);
	  right = curPop.get(curRow		, curCol + 1);
	  down 	= curPop.get(curRow + 1	, curCol	);
	  left 	= curPop.get(curRow		, curCol - 1);
	  
	  //set fittest to the neighbor with the highest fitness
	  fittest = up;
	  if (fittest.fitness < right.fitness) fittest = right;
	  if (fittest.fitness < down.fitness) fittest = down;
	  if (fittest.fitness < left.fitness) fittest = left;
	  
	  return fittest;
	  
  }
  
  //perform crossover using a 2D wrap-around array of Individuals
  private WrapGrid<Individual> crossoverSteadyStateGrid(WrapGrid<Individual> curPop)
  {
	  WrapGrid<Individual> newPop = new WrapGrid<Individual>(curPop.rows, curPop.cols, curPop.conNum);
	  Random random = new Random(System.currentTimeMillis());	  
	  Individual current, mate;
	  
	  for(int i = 0; i < curPop.rows; i++)
	  {
		  for(int j = 0; j < curPop.cols; j++)
		  {
			  current = curPop.get(i, j);
			  //find the mate for this individual
			  mate = fittestNeighbor(i, j, curPop);
			  
			  LOG.log("Crossover: Finding the fittest neighbor");
			  LOG.log("Runner: " + current.str + "| fitness: " + current.fitness);
			  LOG.log("Fittest Neighbor: " + mate.str + "| fitness: " + mate.fitness);
			  
			  evoOut.println("Crossover: Finding the fittest neighbor");
			  evoOut.println("Runner: " + current.str + "| fitness: " + current.fitness);
			  evoOut.println("Fittest Neighbor: " + mate.str + "| fitness: " + mate.fitness);	
			  
			  //this crossover mechanism should maintain chromosomes to a consistent length
			  int p1 = random.nextInt(current.str.length()/2);
			  int p2 = p1 + random.nextInt(mate.str.length()/2);
			  String child1 = current.str.substring(0, p1) + mate.str.substring(p1, p2) + current.str.substring(p2);
			  String child2 = mate.str.substring(0, p1) + current.str.substring(p1, p2) + mate.str.substring(p2);			  
			  
			  //mutate children here, before testing them for fitness		  
			  mutate(child1);
			  mutate(child2);
			  
			  //test each child
			  float child1Fitness = testChild(qwopper, child1, MAX_RUNS);
			  boolean child1Crashed = currentRunnerCrashed;
			  
			  float child2Fitness = testChild(qwopper, child2, MAX_RUNS);
			  boolean child2Crashed = currentRunnerCrashed;
			  
			  //add the best child to the new population if it performs better than the current runner 
			  if( !child1Crashed && child1Fitness > child2Fitness && child1Fitness > current.fitness ) //is child1 the best?
			  {			  
				  evoOut.println("Child 1 fit enough to join next generation: " + child1 + "|" + child1Fitness 
						  + "\nReplacing: " + current.str + "|" + current.fitness);
				  LOG.log("Child 1 fit enough to join next generation: " + child1 + "|" + child1Fitness 
						  + "\nReplacing: " + current.str + "|" + current.fitness);				  
				  	
				  newPop.add( new Individual(child1, child1Fitness) );
			  }
			  else if( !child2Crashed && child2Fitness > child1Fitness && child2Fitness > current.fitness ) //is child2 the best?
			  {			  
				  evoOut.println("Child 2 fit enough to join next generation: " + child2 + "|" + child2Fitness 
						  + "\nReplacing: " + current.str + "|" + current.fitness);
				  LOG.log("Child 2 fit enough to join next generation: " + child2 + "|" + child2Fitness 
						  + "\nReplacing: " + current.str + "|" + current.fitness);				  
				  
				  newPop.add( new Individual(child2, child2Fitness) );
			  }
			  //neither child is fitter than the current runner, therefore the current runner advances to the next generation
			  else 
			  {
				  newPop.add(current);
			  }			  
		  }		  
	  }
	  
	  return newPop;	  
	  
  }
  
  /** Randomly choose three individuals from the pop, select the best two for crossover,
   *  repeat until a new population of the same size has been generated 
   *  
   *  Tournament Selection, one-point "cut and splice" crossover, 
   *  and single point mutation for the entire population
   *  
   *  This function was used only in Configurations 1 and 2
   **/
/*  public void crossover(){
	  
	  Random random = new Random(System.currentTimeMillis());
	  int rnd;
	  //reset newpop size
	  newPopCnt = 0;	  	  
	  newpop = new String[size];
	  
	  boolean newPopFull = false;	//true when the new generation has been populated
	  String[] inds;
	  float[] fits;	  
	  String[] sel;
	  int weakest = -1;
	  
	  
	  while(!newPopFull){
		  //pick three individual strings from current population, select the best two
		  inds = new String[3];
		  fits = new float[3];		  
		  sel = new String[2];
		  
		  rnd = random.nextInt(size);		  
		  inds[0] = indivs[rnd];
		  fits[0] = avgFits[rnd];
		  
		  rnd = random.nextInt(size);
		  inds[1] = indivs[rnd];
		  fits[1] = avgFits[rnd];
		  
		  rnd = random.nextInt(size);
		  inds[2] = indivs[rnd];
		  fits[2] = avgFits[rnd];
		  
		  LOG.log("Crossover: 3 Runners picked at random");
		  LOG.log("1: " + inds[0] + "| fitness: " + fits[0]);
		  LOG.log("2: " + inds[1] + "| fitness: " + fits[1]);
		  LOG.log("3: " + inds[2] + "| fitness: " + fits[2]);
		  
		  
		  evoOut.println("Crossover: 3 Runners picked at random");
		  evoOut.println("1: " + inds[0] + "| fitness: " + fits[0]);
		  evoOut.println("2: " + inds[1] + "| fitness: " + fits[1]);
		  evoOut.println("3: " + inds[2] + "| fitness: " + fits[2]);
		  		  
		  if ( (fits[0] >= fits[1])
			      || (fits[0] >= fits[2]))
			    {
			      if (fits[1] > fits[2])
			        { sel[0] = inds[0]; sel[1] = inds[1]; weakest = 2;}
			      else
			        { sel[0] = inds[0]; sel[1] = inds[2]; weakest = 1; }
			    }
			    else
			    {
			      if ( (fits[1] >= fits[0])
			        || (fits[1] >= fits[2]))
			      {
			        if (fits[0] > fits[2])
			        { sel[0] = inds[1]; sel[1] = inds[0]; weakest = 2; }
			        else
			        { sel[0] = inds[1]; sel[1] = inds[2]; weakest = 0;}
			      }
			      else
			      {
			        if ( (fits[2] >= fits[0])
			          || (fits[2] >= fits[1]))
			        {
			          if (fits[0] > fits[1])
			          { sel[0] = inds[2]; sel[1] = inds[0]; weakest = 1;}
			          else
			          { sel[0] = inds[2]; sel[1] = inds[1]; weakest = 0;}
			        }
			      }
			    }
		  
		  LOG.log("Crossover: Parents selected\n");
		  LOG.log("1: " + sel[0]);
		  LOG.log("2: " + sel[1]);
		  
		  evoOut.println("Crossover: Parents selected\n");
		  evoOut.println("1: " + sel[0]);
		  evoOut.println("2: " + sel[1]);
		  
		  //best two selected. perform one-point "cut and splice" crossover.
		  
		  //two random numbers, each within the size of a parent
		  int p1 = random.nextInt(sel[0].length());
		  int p2 = random.nextInt(sel[1].length());
		  
		  String child1 = sel[0].substring(0, p1) + sel[1].substring(p2);
		  String child2 = sel[1].substring(0, p2) + sel[0].substring(p1);		  
		  
		  //mutate children here, before adding to newpop		  
		  mutate(child1);
		  mutate(child2);
		  
		  //test each child once
		  float child1Fitness = testChild(qwop, child1, MAX_RUNS);
		  
		  //add each child to the new population if it doesn't crash at less than 3m, 
		  //and if it performs better than the weakest potential parent 
		  if( (!crashed || child1Fitness >= 3.0) && child1Fitness > fits[weakest])
		  {			  
			  evoOut.println("Child 1 fit enough to join next generation: " + child1 + "|" + child1Fitness 
					  + "\nReplacing: " + inds[weakest] + "|" + fits[weakest]);
			  LOG.log("Child 1 fit enough to join next generation: " + child1 + "|" + child1Fitness 
					  + "\nReplacing: " + inds[weakest] + "|" + fits[weakest]);
			  newpop[newPopCnt] = child1;
	          newPopCnt++;
		  }
		  //if new population is full, exit loop
		  if(newPopCnt == size)
		  {			  
			  newPopFull = true;
			  continue;
		  }
		  
		  float child2Fitness = testChild(qwop, child2, MAX_RUNS);
			  
		  if((!crashed || child1Fitness >= 3.0) && child2Fitness > fits[weakest])
		  {
			  evoOut.println("Child 2 fit enough to join next generation: " + child2 + "|" + child2Fitness 
					  + "\nReplacing: " + inds[weakest] + "|" + fits[weakest]);
			  LOG.log("Child 2 fit enough to join next generation: " + child2 + "|" + child2Fitness 
					  + "\nReplacing: " + inds[weakest] + "|" + fits[weakest]);
			  newpop[newPopCnt] = child2;
	          newPopCnt++;
		  }		  
          //if new population is full, exit loop
		  if(newPopCnt == size)
			  newPopFull = true;
	  }
	  
	  //the next generation has been created
	  indivs = newpop;	  
	  
  }*/  
  
  private String mutate(String runner){
	  Random random = new Random(System.currentTimeMillis());
	  int mutateLocation = random.nextInt(runner.length());
	  int mutationIndex = random.nextInt(NOTES.length());
      String theMutation = NOTES.substring(mutationIndex, mutationIndex + 1);
      
      return runner.substring(0, mutateLocation) + theMutation + runner.substring(mutateLocation + 1);
  }  

  private void readPopulation(String filename) {
    try {
      BufferedReader input = new BufferedReader(new FileReader(filename));
      try {
        String line;
        int lineNum = 0;
        while ((line = input.readLine()) != null) {
          lineNum++;
          if (line.length() > 0) {
            try {
              RunInfo info = RunInfo.unmarshal(line);
              Individual indiv = this.population.get(info.string);	//gets a string from pop
              if (indiv == null) {
                indiv = new Individual(info.string, null);
                this.population.put(info.string, indiv);
              }
              indiv.runs.add(info);
            } catch (RuntimeException e) {
              System.out.println("Error on line " + lineNum);
              throw e;
            }
          }
        }

      } finally {
        try {
          input.close();
        } catch (IOException e) {
          throw new RuntimeException("Error closing file: " + filename);
        }
      }

    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found: " + filename, e);
    } catch (IOException e) {
      throw new RuntimeException("Error reading file: " + filename, e);
    }
  }
}
