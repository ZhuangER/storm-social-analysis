package storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import storm.tools.EmoticonSentiment;

/**
 * A bolt that matches emoticons and emoji in the tweet
 */
public class RegexBolt extends BaseRichBolt
{
  private OutputCollector collector;
  private Map<String, String> emoticonAndScore;
  
  private static final Charset UTF_8 = Charset.forName("UTF-8");


  @Override
  public void prepare( Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
    collector = outputCollector;
  }

  @Override
  public void execute(Tuple tuple) {
	  try {
      // get the word from the 1st column of incoming tuple

      String originalTweet = tuple.getStringByField("tweet").split("DELIMITER")[0];
      String geoinfo = tuple.getStringByField("tweet").split("DELIMITER")[1];
      int sentiment = tuple.getIntegerByField("sentiment");
      String countryName = tuple.getStringByField("tweet").split("DELIMITER")[3];
      
      emoticonAndScore = EmoticonSentiment.getScoreIfEmoticonPresent(originalTweet);
      int matchedEmoticonScore = Integer.parseInt(emoticonAndScore.get("score"));
      String matchedEmoticon = emoticonAndScore.get("emoticon");
      
      // DEBUG
      // System.out.println("Emotion: " + matchedEmoticon + " Score: " + emoticonAndScore.get("score"));

    	collector.emit(new Values(originalTweet,geoinfo,matchedEmoticonScore, matchedEmoticon, sentiment, countryName));
	  } 
    catch(Exception e) {
		  e.printStackTrace();
	  }
  }
    
  
  
  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
  {
	  outputFieldsDeclarer.declare(new Fields("original-tweet", "geoinfo", "matchedEmoticonScore", "matchedEmoticon", "sentiment", "countryName"));
  }
}
