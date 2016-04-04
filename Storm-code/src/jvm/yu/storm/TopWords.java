package yu.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.ShellBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
//import udacity.storm.spout.RandomSentenceSpout;
import backtype.storm.utils.Utils;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

//import udacity.storm.spout.RandomSentenceSpout;
import yu.storm.tools.SentimentAnalyzer;


public class TopWords extends BaseRichBolt
{
	private OutputCollector collector;
	private Map<String, Integer> SentimentDistribution;
	private Map<String, Integer> CountMap;
	Integer val;
	double alpha;

	@Override 
	public void prepare(
		Map map,
		TopologyContext topologyContext,
		OutputCollector outputCollector
	)
	{
		collector = outputCollector;
		SentimentDistribution = new HashMap<String, Integer>();
		CountMap = new HashMap<String, Integer>();
		alpha = 0.2;
	}

/*	public double getAvg(String countryName)
	{
		double sum = 0;
		double total_count = 0.001;
		for(int i = -1; i< 2; i++)
		{
			if(SentimentDistribution.containsKey(countryName+" "+String.valueOf(i)))
			{
				sum = sum + (SentimentDistribution.get(countryName+" "+String.valueOf(i))*(i+1.0)/3.0);
				total_count = total_count + SentimentDistribution.get(countryName+" "+String.valueOf(i));
			}
		}
		return sum/total_count;
	}*/
	
	public void execute(Tuple tuple)
	{
		
		String tweet = tuple.getStringByField("original-tweet");
		String geoinfo = tuple.getStringByField("geoinfo");
		String matchedEmoticon = tuple.getStringByField("matchedEmoticon");
		int matchedEmoticonScore = tuple.getIntegerByField("matchedEmoticonScore");
		int personalSentiment = tuple.getIntegerByField("sentiment");
		String countryName = tuple.getStringByField("countryName");
		String sentimentKey = countryName + " " + String.valueOf(personalSentiment);
		double countrySentiment = 0;
		
		// emoticon has higher privilege than word
		if(matchedEmoticonScore != 0){
			personalSentiment = matchedEmoticonScore;
		}

		// count number of sentiment of countries
		if (CountMap.get(countryName) == null) {
			CountMap.put(countryName, 1);
		}
		else {
			Integer val = CountMap.get(countryName);
			CountMap.put(countryName, ++val);
		}

		// count sentiment of country
		if (SentimentDistribution.get(countryName) == null){
			SentimentDistribution.put(countryName, personalSentiment);
		}
		else {
			Integer tmp = SentimentDistribution.get(countryName);
			SentimentDistribution.put(countryName, tmp + personalSentiment);
		}
		
		// because sentiment range is between 0 to 4, to match 5 value to [0, 1]
		countrySentiment = SentimentDistribution.get(countryName) / CountMap.get(countryName) / 4;
		


		System.out.println("\t\tTopWords\tDEBUG EMIT Tweet " + tweet + ", geoinfo" + geoinfo + ", matcedEmoticon: " + matchedEmoticon + ", sentimentKey: " + sentimentKey + ", countrySentiment: " + countrySentiment + ", personalSentiment: " + personalSentiment + ", countryName: " + countryName);
		collector.emit(new Values(tweet, geoinfo, matchedEmoticonScore, matchedEmoticon, countrySentiment, personalSentiment, countryName));

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		
		
		outputFieldsDeclarer.declare(
				new Fields("tweet", "geoinfo", "countrySentiment", "personalSentiment", "countryName"));
	}

}
