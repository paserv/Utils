package it.poste.test;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class DriverProgram implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		
		
		SparkConf conf = new SparkConf().setAppName("Test").setMaster("local[10]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		JavaRDD<String> lines = sc.textFile("src/main/resources/test.csv");
		
		DriverProgram dp = new DriverProgram();
		
		long startTime = System.currentTimeMillis();
		JavaPairRDD<String, Integer> pairs = lines.mapToPair(dp.new MyPairFunc());
		JavaPairRDD<String, Integer> counts = pairs.reduceByKey(dp.new Sum());
		long endTime = System.currentTimeMillis();
		
		Map<String, Integer> map = counts.collectAsMap();
		Set<String> keys = map.keySet();
		for (String s : keys) {
		    System.out.println(s);
		    System.out.println(map.get(s));
		}
		
		System.out.println("Elapsed Time: " + (endTime - startTime));
		
	}
	
	private class MyPairFunc implements PairFunction<String, String, Integer> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Tuple2<String, Integer> call(String t) throws Exception {
			String[] values = t.split(",");
			String key = values[0] + values[3];
			return new Tuple2<String, Integer>(key, 1);
		}
	}
	
	private class Sum implements Function2<Integer, Integer, Integer> {
		  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Integer call(Integer a, Integer b) { return a + b; }
	}
}
