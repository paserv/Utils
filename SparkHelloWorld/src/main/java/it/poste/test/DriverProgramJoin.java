package it.poste.test;

import java.io.Serializable;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class DriverProgramJoin implements Serializable {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		
		System.setProperty("hadoop.home.dir", "F:\\portableSoftware\\winutils");
		
		SparkConf conf = new SparkConf().setAppName("Join").setMaster("local[10]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		JavaRDD<String> table1 = sc.textFile("src/main/resources/table1.csv");
		JavaRDD<String> table2 = sc.textFile("src/main/resources/table2.csv");
		
		long startTime = System.currentTimeMillis();
		
		JavaPairRDD<String, String> table1Pair = table1.mapToPair(line -> new Tuple2<String, String>(line.split(";")[0], line.split(";")[1]));
		JavaPairRDD<String, String> table2Pair = table2.mapToPair(line -> new Tuple2<String, String>(line.split(";")[0], line.split(";")[1]));
		
		JavaPairRDD<String, Tuple2<String, String>> join = table1Pair.join(table2Pair);
		
		join.saveAsTextFile("src/main/resources/output_join.txt");
		
		JavaPairRDD<String, Tuple2<String, String>> reduce = join.reduceByKey((x, y) -> new Tuple2<String, String>(x._1, x._2 + y._2));
		
		reduce.saveAsTextFile("src/main/resources/output.txt");
		long endTime = System.currentTimeMillis();
		System.out.println("Elapsed Time: " + (endTime - startTime));
		
		sc.close();
		
	}
	
}
