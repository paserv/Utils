package it.postemen.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import it.postemen.bean.Coordinate;
import it.postemen.bean.Slice;

public class OutputGenerator {

	private String path;
	private static final String RECORD_SEPARATOR = " ";

	/**
	 * Scrive il file di output da sottomettere a Google
	 */
	public static void writeOutput(String path, List<Slice> slices) throws IOException {
		int slicesCount = slices.size();
		Files.write(Paths.get(path), (Iterable<String>) IntStream.of(slicesCount).mapToObj(String::valueOf)::iterator,
				StandardCharsets.ISO_8859_1);

		Stream<String> stream = slices.stream()
				.map(x -> String.join(RECORD_SEPARATOR, String.valueOf(x.getLeftUpCorner().getX()),
						String.valueOf(x.getLeftUpCorner().getY()), String.valueOf(x.getRightDownCorner().getX()),
						String.valueOf(x.getRightDownCorner().getY())));

		Files.write(Paths.get(path), (Iterable<String>) stream::iterator, StandardCharsets.ISO_8859_1,
				StandardOpenOption.APPEND);
	}

	public static void printMatrix(char[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j]);
			}
			System.out.println();
		}
	}
	
	public static void printMatrix(boolean[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j] ? 1 : 0);
			}
			System.out.println();
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static void main(String[] args) throws IOException {
		List<Slice> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			list.add(new Slice(new Coordinate(i, i), new Coordinate(i * 2, i * 2)));
		}
		
		OutputGenerator.writeOutput("./big.out", list);
	}

}
