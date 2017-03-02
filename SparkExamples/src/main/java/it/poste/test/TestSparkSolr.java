package it.poste.test;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;

import com.lucidworks.spark.util.SolrSupport;

import scala.Tuple2;

public class TestSparkSolr {
	public static String zkHost = "10.24.4.253:2181,10.24.4.107:2181,10.24.5.171:2181,10.24.5.178:2181,10.24.5.242:2181/solr";
	public static String collection = "test_spark_solr";
	public static int queueSize = 1000;
	public static int numRunners = 2;
	public static int pollQueueTime = 20;

	public static void main(String[] args) throws Exception, IOException {
		System.setProperty("hadoop.home.dir", "F:\\portableSoftware\\winutils");

		SparkConf conf = new SparkConf().setAppName("TestSparkSolr").setMaster("local[4]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		DriverProgram dp = new DriverProgram();

		//		JavaRDD<String> table1 = sc.textFile("src/main/resources/table1.csv");
		//		JavaRDD<String> table2 = sc.textFile("src/main/resources/table2.csv");

		JavaRDD<String> table1 = sc.textFile("hdfs://master1.localdomain:8020/tmp/spark/table1.csv");
		JavaRDD<String> table2 = sc.textFile("hdfs://master1.localdomain:8020/tmp/spark/table2.csv");

		long startTime = System.currentTimeMillis();
		//		JavaPairRDD<String, String> table1Pair = table1.mapToPair(line -> new Tuple2<String, String>(line.split(";")[0], line));
		//		JavaPairRDD<String, String> table2Pair = table2.mapToPair(line -> new Tuple2<String, String>(line.split(";")[0], line));

		JavaPairRDD<String, String> table1Pair = table1.mapToPair(new PairFunction<String, String, String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Tuple2<String, String> call(String line) throws Exception {
				return new Tuple2<String, String>(line.split(";")[0], line);
			}

		});

		JavaPairRDD<String, String> table2Pair = table2.mapToPair(new PairFunction<String, String, String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Tuple2<String, String> call(String line) throws Exception {
				return new Tuple2<String, String>(line.split(";")[0], line);
			}

		}

				);

		JavaPairRDD<String, Tuple2<String, String>> join = table1Pair.join(table2Pair);

		//		JavaPairRDD<String, SolrInputDocument> pairs = join.mapToPair( (x, (y, z)) ->  new Tuple2<String, SolrInputDocument>() );

		JavaPairRDD<String, SolrInputDocument> pairs = join.mapToPair(new PairFunction<Tuple2<String,Tuple2<String,String>>, String, SolrInputDocument>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Tuple2<String, SolrInputDocument> call(Tuple2<String, Tuple2<String, String>> mannaggia) throws Exception {
				SolrInputDocument doc = new SolrInputDocument();
				String idProdotto = mannaggia._2._2.split(";")[3];
				String idDistinta = mannaggia._1;
				String dataConsegna = mannaggia._2._2.split(";")[2];
				String id = idDistinta + "_" + idProdotto + "_" + dataConsegna;
				doc.setField("id", id);
				String value = mannaggia._2._1 + mannaggia._2._2;
				doc.setField("munnezz", value);
				return new Tuple2<String, SolrInputDocument>((String)doc.getFieldValue("id"), doc);
			}

		});
		//		pairs.saveAsTextFile("src/main/resources/pairs");

		//SolrSupport.streamDocsIntoSolr(zkHost, collection, "id", pairs, queueSize, numRunners, pollQueueTime);
		SolrSupport.indexDocs(zkHost, collection, 100, pairs.values().rdd());
		CloudSolrClient cloudSolrClient = SolrSupport.getCachedCloudClient(zkHost);
		cloudSolrClient.setDefaultCollection(collection);
		cloudSolrClient.commit(true, true);
		long endTime = System.currentTimeMillis();

	}
}
