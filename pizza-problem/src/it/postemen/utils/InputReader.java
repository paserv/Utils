package it.postemen.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import it.postemen.bean.Ties;

public class InputReader {
	
	public static void main(String[] args) {
		Ties vincoli = InputReader.getTies("input/big.in");
		OutputGenerator.printMatrix(vincoli.getMatrix());
	}
	
	public static Ties getTies(String inputPath) {
		Ties vincoli = new Ties();
		try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
			String firstLine = br.readLine();
			String[] firstLineData = firstLine.split(" ");
			vincoli.setMatrixRows(Integer.valueOf(firstLineData[0]));
			vincoli.setMatrixCols(Integer.valueOf(firstLineData[1]));
			Hashtable<Character, Integer> minIngr = new Hashtable<Character, Integer>();
			minIngr.put('T', Integer.valueOf(firstLineData[2]));
			minIngr.put('M', Integer.valueOf(firstLineData[2]));
			vincoli.setMinIngredient(minIngr);
			vincoli.setMaxSliceArea(Integer.valueOf(firstLineData[3]));
			
			String sCurrentLine;
			char[][] matrix = new char[vincoli.getMatrixRows()][vincoli.getMatrixCols()];
			int i = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				
				char[] currentCharLine = sCurrentLine.toCharArray();
				for (int j = 0; j < vincoli.getMatrixCols(); j++) {
					matrix[i][j] = currentCharLine[j];
				}
				i++;
			}
			vincoli.setMatrix(matrix);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vincoli;
	}
	
}