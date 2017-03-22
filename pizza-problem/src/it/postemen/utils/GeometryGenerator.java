package it.postemen.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import it.postemen.bean.Geometry;
import it.postemen.bean.Ties;

public class GeometryGenerator {

	/**
	 * 
	 * @param ties vincoli riguardanti Area massima slice e minimo numero ingredienti per slice (dal quale ricavare area minima)
	 * @return le geometrie di slice utili alla risoluzione del problema
	 */
	public static ArrayList<Geometry> getCandidateGeometry(Ties ties) {
		HashSet<Geometry> set = new HashSet<>();
		ArrayList<Geometry> returnVal = new ArrayList<>();
		int minArea = Integer.valueOf(ties.getMinIngr('T')) * 2;
		
		for (int i = minArea; i <= ties.getMaxSliceArea(); i++) {
			set.addAll(findSlices(i));
		}
		for (Geometry geometry : set) {
			returnVal.add(geometry); 
		}
		
		return returnVal;
	}
	
	private static List<Integer> primeFactors(int numbers) {
		int n = numbers;
		List<Integer> factors = new ArrayList<Integer>();
		for (int i = 2; i <= n / i; i++) {
			while (n % i == 0) {
				factors.add(i);
				n /= i;
			}
		}
		if (n > 1) {
			factors.add(n);
		}
		return factors;
	}

	public static int[][] combineFactors(List<Integer> factors, Integer target) {
		int[][] result = new int[factors.size()][2];

		for (int r = 0; r < result.length; r++) {
			result[r][0] = 1;
			result[r][1] = 1;
			for (int i = 0; i <= r; i++)
				result[r][0] *= factors.get(i);
			for (int j = r + 1; j < factors.size(); j++)
				result[r][1] *= factors.get(j);
		}
		return result;
	}

	private static ArrayList<Geometry> findSlices(int target) {
		ArrayList<Geometry> result = new ArrayList<Geometry>();
		List<Integer> factors = primeFactors(target);
		int[][] combinations = combineFactors(factors, target);
		for (int i = 0; i < combinations.length; i++) {
			result.add(new Geometry(combinations[i][0], combinations[i][1]));
			if (combinations[i][1] != combinations[i][0])
				result.add(new Geometry(combinations[i][1], combinations[i][0]));
		}
		
		return result;
	}
}
