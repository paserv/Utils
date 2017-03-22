package it.postemen.main;

import java.util.ArrayList;

import it.postemen.bean.Geometry;
import it.postemen.bean.Slice;
import it.postemen.bean.Ties;
import it.postemen.fitness.FitnessCalculator;
import it.postemen.fitness.MaxAreaAlgorithm;
import it.postemen.slice.LeftDownStartingPoint;
import it.postemen.slice.LeftUpStartingPoint;
import it.postemen.slice.RightDownStartingPoint;
import it.postemen.slice.RightUpStartingPoint;
import it.postemen.slice.SliceGenerator;
import it.postemen.utils.GeometryGenerator;
import it.postemen.utils.InputReader;
import it.postemen.utils.MatrixUtils;
import it.postemen.utils.ScoreCalculator;

/**
 * 
 * @author servill7
 * Itera sui vari SliceGenerator
 */
@Deprecated
public class Main2 {

	public static void main(String[] args) {

		runCompleteSession("big");

	}

	private static int runCompleteSession(String prefix) {
		SliceGenerator ruSg = new RightUpStartingPoint();
		SliceGenerator luSg = new LeftUpStartingPoint();
		SliceGenerator ldSg = new LeftDownStartingPoint();
		SliceGenerator rdSg = new RightDownStartingPoint();

		FitnessCalculator maxareaCalc = new MaxAreaAlgorithm();

		ArrayList<Slice> slices = new ArrayList<Slice>();
		
		int score = 0;
		score = runSession(prefix, ldSg, maxareaCalc, slices);
		System.out.println(score);
		
		score = runSession(prefix, luSg, maxareaCalc, slices);
		System.out.println(score);
		
		score = runSession(prefix, rdSg, maxareaCalc, slices);
		System.out.println(score);
		
		score = runSession(prefix, ruSg, maxareaCalc, slices);
		System.out.println(score);
		
		return score;
	}


	private static int runSession(String prefix, SliceGenerator sg, FitnessCalculator sc, ArrayList<Slice> slices) {
		//Carico i vincoli dal file di input
		Ties vincoli = InputReader.getTies("input/" + prefix + ".in");

		//Calcolo tutte le possibili geometrie candidate ad essere dei tagli di pizza
		ArrayList<Geometry> geometries = GeometryGenerator.getCandidateGeometry(vincoli);

		boolean[][] matrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
		MatrixUtils.fillMatrixWithSlices(matrixWithSlice, slices);
		
		//finchè ci sono geometrie non ancora collocate sulla pizza
		//TODO Da parallelizzare
		while (!geometries.isEmpty()) {

			ArrayList<Slice> bestSliceFound = new ArrayList<Slice>();
			float maxFitnessFound = 0;
			Geometry bestGeometryFound = new Geometry();
			boolean[][] bestMatrixWithSlice = new boolean[vincoli.getMatrixRows()][vincoli.getMatrixCols()];

			//Trova la geometria che meglio si adatta
			for (Geometry currGeom : geometries) {
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
			slices.addAll(bestSliceFound);
			geometries.remove(bestGeometryFound);
			matrixWithSlice = bestMatrixWithSlice;
		}

		return ScoreCalculator.getScore(slices);
	}	
}
