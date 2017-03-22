package it.postemen.main;

import java.io.IOException;
import java.util.ArrayList;

import it.postemen.bean.Geometry;
import it.postemen.bean.Slice;
import it.postemen.bean.Ties;
import it.postemen.fitness.FitnessCalculator;
import it.postemen.fitness.MaxAreaAlgorithm;
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

public class Main3 {

	public static void main(String[] args) {
		
//		FitnessCalculator fc = new MaxAreaAlgorithm();
		FitnessCalculator fc = new SparsityCalculator(new MineFieldScorer<>(new ThreeNStrategy(), new NaiveScoreStrategy()));
		runCompleteSession(fc);
		
	}
	
	private static int runCompleteSession(FitnessCalculator fc) {
		System.out.println("Thread: " + Thread.currentThread().getId() + " Configuration: " + fc.getClass().toString());

		int score = runSession("example", fc) +	runSession("small", fc) + runSession("medium", fc) + runSession("big", fc);
//		int score = runSession("medium", fc);
		System.out.println("Thread: " + Thread.currentThread().getId() + " Final Score: " + score);

		return score;
	}

	private static int runSession(String prefix, FitnessCalculator sc) {
		int score = 0;
		ArrayList<Slice> result = new ArrayList<Slice>();

		//Carico i vincoli dal file di input
		Ties vincoli = InputReader.getTies("input/" + prefix + ".in");
		
		//Calcolo tutte le possibili geometrie candidate ad essere dei tagli di pizza
		ArrayList<Geometry> geometries = GeometryGenerator.getCandidateGeometry(vincoli);
		
		//Inizializzo la matrice in cui sono rappresentatae le slice considerate OK
		boolean[][] matrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
		
		//finchè ci sono geometrie non ancora collocate sulla pizza
		while (!geometries.isEmpty()) {
		
			ArrayList<Slice> bestSliceFound = new ArrayList<Slice>();
			float maxFitnessFound = 0;
			Geometry bestGeometryFound = new Geometry();
			boolean[][] bestMatrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
			
			//Trova la geometria che meglio si adatta
			for (Geometry currGeom : geometries) {
				SliceGenerator sg;
				sg = fitSlicesIntoMatrix(currGeom, vincoli, matrixWithSlice, sc);
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
			System.out.println("Final Score of " + prefix + ": " + ScoreCalculator.getScore(result));
			score = ScoreCalculator.getScore(result);
			OutputGenerator.writeOutput("output/" + prefix + "." + score + ".out", result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return score;
	}

	private static SliceGenerator fitSlicesIntoMatrix(Geometry currGeom, Ties vincoli, boolean[][] matrixWithSlice, FitnessCalculator sc) {
		SliceGenerator ruSg = new RightUpStartingPoint();
		SliceGenerator luSg = new LeftUpStartingPoint();
		SliceGenerator ldSg = new LeftDownStartingPoint();
		SliceGenerator rdSg = new RightDownStartingPoint();
		
		boolean[][] currentMatrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
		
		ArrayList<Slice> currFoundSlicesRuSg = ruSg.fitSlicesIntoMatrix(currGeom, vincoli, matrixWithSlice);
		currentMatrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
		currentMatrixWithSlice = MatrixUtils.copy(matrixWithSlice);
		MatrixUtils.fillMatrixWithSlices(currentMatrixWithSlice, currFoundSlicesRuSg);
		float currScoreRuSg = sc.getFitness(currentMatrixWithSlice, currFoundSlicesRuSg);
		
		ArrayList<Slice> currFoundSlicesLuSg = luSg.fitSlicesIntoMatrix(currGeom, vincoli, matrixWithSlice);
		currentMatrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
		currentMatrixWithSlice = MatrixUtils.copy(matrixWithSlice);
		MatrixUtils.fillMatrixWithSlices(currentMatrixWithSlice, currFoundSlicesLuSg);
		float currScoreLuSg = sc.getFitness(currentMatrixWithSlice, currFoundSlicesLuSg);
		
		ArrayList<Slice> currFoundSlicesLdSg = ldSg.fitSlicesIntoMatrix(currGeom, vincoli, matrixWithSlice);
		currentMatrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
		currentMatrixWithSlice = MatrixUtils.copy(matrixWithSlice);
		MatrixUtils.fillMatrixWithSlices(currentMatrixWithSlice, currFoundSlicesLdSg);
		float currScoreLdSg = sc.getFitness(currentMatrixWithSlice, currFoundSlicesLdSg);
		
		ArrayList<Slice> currFoundSlicesRdSg = rdSg.fitSlicesIntoMatrix(currGeom, vincoli, matrixWithSlice);
		currentMatrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
		currentMatrixWithSlice = MatrixUtils.copy(matrixWithSlice);
		MatrixUtils.fillMatrixWithSlices(currentMatrixWithSlice, currFoundSlicesRdSg);
		float currScoreRdSg = sc.getFitness(currentMatrixWithSlice, currFoundSlicesRdSg);
		
		if (currScoreRuSg > currScoreLuSg && currScoreRuSg > currScoreLdSg && currScoreRuSg > currScoreRdSg) {
			return ruSg;
		} else if (currScoreLuSg > currScoreRuSg && currScoreLuSg > currScoreLdSg && currScoreLuSg > currScoreRdSg) {
			return luSg;
		} else if (currScoreLdSg > currScoreRuSg && currScoreLdSg > currScoreLuSg && currScoreLdSg > currScoreRdSg ) {
			return ldSg;
		}
		return rdSg;
		
	}	
}
