package it.postemen.utils;

import java.util.ArrayList;

import it.postemen.bean.Slice;

public class ScoreCalculator {

	
	/**
	 * 
	 * @param slices fette che soddisfano i vincoli. Dalla traccia del problema:
	 * "The submission gets a score equal to the total number of cells in all slices."
	 * @return area coperta dalle slice
	 */
	public static int getScore(ArrayList<Slice> slices) {
		int score = 0;
		
		for (Slice s:slices) {
			score += s.getArea(); 
		}
		
		return score;
	}
}
