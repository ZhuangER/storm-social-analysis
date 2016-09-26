package storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Map;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

import storm.tools.CountryCodeConvert;

import org.apache.log4j.Logger;

/**
 * A bolt that prints the word and count to redis
 */
public class ReportBolt extends BaseRichBolt {
  // place holder to keep the connection to redis
  transient RedisConnection<String,String> redis;
  private static Logger LOG = Logger.getLogger(ReportBolt.class);
  private CountryCodeConvert converter;
  @Override
  public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
    // instantiate a redis connection
    RedisClient client = new RedisClient("localhost",6379);
    redis = client.connect();
    converter = CountryCodeConvert.getInstance();
  }


  @Override
  public void execute(Tuple tuple) {
	  String tweet = tuple.getStringByField("tweet");
    String geoinfo = tuple.getStringByField("geoinfo");
    int personalSentiment = tuple.getIntegerByField("personalSentiment");
    double countrySentiment = tuple.getDoubleByField("countrySentiment");
    String countryName = tuple.getStringByField("countryName");
    LOG.debug("\t\t\tDEBUG ReportBolt: " + "Tweet countrySentiment:" + String.valueOf(countrySentiment));

    countryName = converter.iso2CountryCodeToIso3CountryCode(countryName);

    redis.publish("WordCountTopology", geoinfo + "DELIMITER" + tweet + "DELIMITER" + String.valueOf(personalSentiment) + "DELIMITER" + countryName + "DELIMITER" + String.valueOf(countrySentiment));
  }

  public void declareOutputFields(OutputFieldsDeclarer declarer)
  {
    // final bolt
  }
}
