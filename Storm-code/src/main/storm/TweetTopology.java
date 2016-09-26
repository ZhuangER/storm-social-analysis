package storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

import java.io.*;
import java.util.Properties;

import org.apache.log4j.Logger;

import storm.bolt.CountBolt;
import storm.bolt.RegexBolt;
import storm.bolt.ReportBolt;
import storm.spout.TweetSpout;


/**
 * Storm Topology: Tweet sentiment analysis
 * @author  Yu Huang
 */
public class TweetTopology {
  // private static final int DEFAULT_RUNTIME_IN_SECONDS;
  private static final Logger LOG = Logger.getLogger(TweetTopology.class);
  private static final String spoutId = "tweet-spout";
  private static final String regexId = "regex-bolt";
  private static final String countId = "count-bolt";
  private static final String reportId = "report-bolt";
  private static final String topologyName = "tweet-sentiment-topology";


  public static void main(String[] args) throws Exception {
    // create the topology
    TopologyBuilder builder = new TopologyBuilder();

    // Create the tweet spout with the credentials
    InputStream is = new FileInputStream("src/main/storm/config/TwitterCredentials.properties");
    Properties prop = new Properties();
    prop.load(is);
    TweetSpout tweetSpout = new TweetSpout(
            prop.getProperty("twitter.consuemrKey"),
            prop.getProperty("twitter.consumerSecret"),
            prop.getProperty("twitter.accessToken"),
            prop.getProperty("twitter.accessTokenSecret")
    );

    // set spout with parallelism of 1
    builder.setSpout(spoutId, tweetSpout, 1);

    builder.setBolt(regexId, new RegexBolt(), 10).shuffleGrouping(spoutId);
    builder.setBolt(countId, new CountBolt(), 10).fieldsGrouping(regexId, new Fields("countryName"));
    
    // gether all tuples
    builder.setBolt(reportId, new ReportBolt(), 1).globalGrouping(countId);


    // create the default config object
    Config conf = new Config();

    // set the config in debugging mode
    conf.setDebug(true);

    // in live cluster
    if (args != null && args.length > 0) {
      LOG.info("Running in cluster mode");
      // set the number of workers for running all spout and bolt tasks
      conf.setNumWorkers(3);

      // create the topology and submit with config
      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());

    } 
    else { // in local cluster
      LOG.info("Running in local mode");
      // set the number of threads to run
      conf.setMaxTaskParallelism(4);

      // create the local cluster instance
      LocalCluster cluster = new LocalCluster();

      // submit the topology to the local cluster
      cluster.submitTopology(topologyName, conf, builder.createTopology());
      
      Utils.sleep(300000000);

      cluster.killTopology(topologyName);

      cluster.shutdown();
    }
  }
}
