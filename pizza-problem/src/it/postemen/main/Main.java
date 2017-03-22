package it.postemen.main;

import java.io.IOException;
import java.util.ArrayList;

import it.postemen.bean.Geometry;
import it.postemen.bean.Slice;
import it.postemen.bean.Ties;
import it.postemen.fitness.FitnessCalculator;
import it.postemen.fitness.MaxAreaAlgorithm;
import it.postemen.fitness.RandomCalculator;
import it.postemen.fitness.SparsityCalculator;
import it.postemen.slice.LeftDownStartingPoint;
import it.postemen.slice.LeftUpStartingPoint;
import it.postemen.slice.RightDownStartingPoint;
import it.postemen.slice.RightUpStartingPoint;
import it.postemen.slice.SliceGenerator;
import it.postemen.utils.GeometryGenerator;
import it.postemen.utils.InputReader;
import it.postemen.utils.MatrixUtils;
import it.postemen.utils.OutputGenerator;
import it.postemen.utils.ScoreCalculator;
import it.postemen.utils.sparsity.MineFieldScorer;
import it.postemen.utils.sparsity.NaiveScoreStrategy;
import it.postemen.utils.sparsity.ThreeNStrategy;
import it.postemen.utils.sparsity.WholeHoleScorer;

public class Main {

	public static void main(String[] args) {
		SliceGenerator ruSg = new RightUpStartingPoint();
		SliceGenerator luSg = new LeftUpStartingPoint();
		SliceGenerator ldSg = new LeftDownStartingPoint();
		SliceGenerator rdSg = new RightDownStartingPoint();
		
		FitnessCalculator maxareaCalc = new MaxAreaAlgorithm();//new SparsityCalculator(new MineFieldScorer<>(new FourNStrategy2(), new NaiveScoreStrategy()));
		FitnessCalculator minefieldCalc = new SparsityCalculator(new MineFieldScorer<Boolean>(new ThreeNStrategy(), new NaiveScoreStrategy()));
//		FitnessCalculator wholeHoleCalc = new SparsityCalculator(new WholeHoleScorer<Boolean>(new ThreeNStrategy(), new NaiveScoreStrategy()));
		FitnessCalculator randomCalc = new RandomCalculator();
		
		new Thread(() -> runCompleteSession(ruSg, minefieldCalc)).start();
		new Thread(() -> runCompleteSession(luSg, minefieldCalc)).start();
		new Thread(() -> runCompleteSession(ldSg, minefieldCalc)).start();
		new Thread(() -> runCompleteSession(rdSg, minefieldCalc)).start();
		
//		new Thread(() -> runCompleteSession(ruSg, wholeHoleCalc)).start();
//		new Thread(() -> runCompleteSession(luSg, wholeHoleCalc)).start();
//		new Thread(() -> runCompleteSession(ldSg, wholeHoleCalc)).start();
//		new Thread(() -> runCompleteSession(rdSg, wholeHoleCalc)).start();
		
		new Thread(() -> runCompleteSession(ruSg, maxareaCalc)).start();
		new Thread(() -> runCompleteSession(luSg, maxareaCalc)).start();
		new Thread(() -> runCompleteSession(ldSg, maxareaCalc)).start();
		new Thread(() -> runCompleteSession(rdSg, maxareaCalc)).start();
			
		for (int i = 0; i < 10; i++) {
			new Thread(() -> runCompleteSession(rdSg, randomCalc)).start();
		}
		
	}
	
	private static int runCompleteSession(SliceGenerator sg, FitnessCalculator fc) {
		System.out.println("Thread: " + Thread.currentThread().getId() + " Configuration: " + sg.getClass().toString() + " - " + fc.getClass().toString());

		int score = runSession("example", sg, fc) +	runSession("small", sg, fc) + runSession("medium", sg, fc) + runSession("big", sg, fc);

		System.out.println("Thread: " + Thread.currentThread().getId() + " Final Score: " + score);

		return score;
	}

	private static int runSession(String prefix, SliceGenerator sg, FitnessCalculator sc) {
		int score = 0;
		ArrayList<Slice> result = new ArrayList<Slice>();

		//Carico i vincoli dal file di input
		Ties vincoli = InputReader.getTies("input/" + prefix + ".in");
		
		//Calcolo tutte le possibili geometrie candidate ad essere dei tagli di pizza
		ArrayList<Geometry> geometries = GeometryGenerator.getCandidateGeometry(vincoli);
		
		//Inizializzo la matrice in cui sono rappresentatae le slice considerate OK
		boolean[][] matrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
		
		//finch� ci sono geometrie non ancora collocate sulla pizza
		//TODO Da parallelizzare
		while (!geometries.isEmpty()) {
		
			ArrayList<Slice> bestSliceFound = new ArrayList<Slice>();
			float maxFitnessFound = 0;
			Geometry bestGeometryFound = new Geometry();
			boolean[][] bestMatrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
			
			//Trova la geometria che meglio si adatta
			for (Geometry currGeom : geometries) {
//				SliceGenerator currSg = new RightUpStartingPoint();
				ArrayList<Slice> currFoundSlices = sg.fitSlicesIntoMatrix(currGeom, vincoli, matrixWithSlice);
				
				boolean[][] currentMatrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
				currentMatrixWithSlice = MatrixUtils.copy(matrixWithSlice); 
				MatrixUtils.fillMatrixWithSlices(currentMatrixWithSlice, currFoundSlices);
				
				float currSparsity = sc.getFitness(currentMatrixWithSlice, currFoundSlices);
				if (currSparsity >= maxFitnessFound) {
					maxFitnessFound = currSparsity;
					bestGeometryFound = currGeom;
					bestSliceFound =  new ArrayList<Slice>();
					bestSliceFound.addAll(currFoundSlices);
					bestMatrixWithSlice = currentMatrixWithSlice;
				}
				
			}
			result.addAll(bestSliceFound);
			geometries.remove(bestGeometryFound);
			matrixWithSlice = bestMatrixWithSlice;
			
		}
		
		try {
			score = ScoreCalculator.getScore(result);
			OutputGenerator.writeOutput("output/" + prefix + "." + score + ".out", result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return score;
	}	
}
