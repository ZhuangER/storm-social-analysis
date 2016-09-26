package storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

import java.io.*;
import java.util.Properties;


/**
 * Storm Topology: Tweet sentiment analysis
 * @author  Yu Huang
 */
class TweetTopology
{
  public static void main(String[] args) throws Exception
  {
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
    builder.setSpout("tweet-spout", tweetSpout, 1);

    builder.setBolt("regex-bolt", new RegexBolt(), 10).shuffleGrouping("tweet-spout");
    builder.setBolt("count-bolt", new CountBolt(), 10).fieldsGrouping("regex-bolt", new Fields("countryName"));
    
    // gether all tuples
    builder.setBolt("report-bolt", new ReportBolt(), 1).globalGrouping("count-bolt");


    // create the default config object
    Config conf = new Config();

    // set the config in debugging mode
    conf.setDebug(true);

    if (args != null && args.length > 0) {

      // run it in a live cluster

      // set the number of workers for running all spout and bolt tasks
      conf.setNumWorkers(3);

      // create the topology and submit with config
      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());

    } else {

      // run it in a simulated local cluster

      // set the number of threads to run
      conf.setMaxTaskParallelism(4);

      // create the local cluster instance
      LocalCluster cluster = new LocalCluster();

      // submit the topology to the local cluster
      cluster.submitTopology("tweet-word-count", conf, builder.createTopology());

      // let the topology run for 300 seconds. note topologies never terminate!
      Utils.sleep(300000000);

      // Kill the topology
      cluster.killTopology("tweet-word-count");

      // Shutdown the local cluster
      cluster.shutdown();
    }
  }
}
