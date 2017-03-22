package it.postemen.slice;

import java.util.ArrayList;
import java.util.Hashtable;

import it.postemen.bean.Geometry;
import it.postemen.bean.Slice;
import it.postemen.bean.Ties;

public abstract class SliceGenerator {
	
	public abstract ArrayList<Slice> fitSlicesIntoMatrix(Geometry brick, Ties vincoli, boolean[][] matrixWithSlice);
	
	public boolean checkSliceOk(Slice slice, Ties vincoli, boolean[][] matrixWithSlice) {
		Hashtable<Character, Integer> ingredientCounter = new Hashtable<Character, Integer>();
		for (Character key : vincoli.getMinIngredient().keySet()) {
			ingredientCounter.put(key, 0);
		}
		for (int i = slice.getLeftUpCorner().getX(); i <= slice.getRightDownCorner().getX(); i++) {
			for (int j = slice.getLeftUpCorner().getY(); j <= slice.getRightDownCorner().getY(); j++) {
				if (matrixWithSlice[i][j]) {
					return false;
				} else {
					char ingredient = vincoli.getMatrix()[i][j];
					int ingredientCount = ingredientCounter.get(ingredient);
					ingredientCounter.put(ingredient, ingredientCount + 1);
				}
				
			}
		}
		for (Character key : ingredientCounter.keySet()) {
			if (ingredientCounter.get(key) < vincoli.getMinIngr(key)) {
				return false;
			}
		}
		return true;
	}
}
