package storm.spout;

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

import twitter4j.conf.ConfigurationBuilder;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StallWarning;
import twitter4j.URLEntity;

import storm.tools.SentenceSentiment;
import storm.tools.TweetExtractor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * TweetSpout
 * A spout that uses Twitter streaming API for continuously
 * getting tweets
 */
public class TweetSpout extends BaseRichSpout
{
  private String custkey, custsecret;
  private String accesstoken, accesssecret;
  private SpoutOutputCollector collector;
  // Twitter4j - twitter stream to get tweets
  private TwitterStream twitterStream;
  // Shared queue for getting buffering tweets received
  private LinkedBlockingQueue<String> queue = null;

  private static Logger LOG = Logger.getLogger(TweetSpout.class);

  private class TweetListener implements StatusListener {
    // The callback function when a tweet arrives
    @Override
    public void onStatus(Status status) {
      // add the tweet into the queue buffer
      String geoInfo = "37.7833,122.4167";
      String urlInfo = "n/a";
      String countryName = "";
      if(status.getGeoLocation() != null)
      {
        geoInfo = String.valueOf(status.getGeoLocation().getLatitude()) + "," + String.valueOf(status.getGeoLocation().getLongitude());
        countryName = String.valueOf(status.getPlace().getCountryCode());
          if(status.getURLEntities().length > 0)
          {
            for(URLEntity urlE: status.getURLEntities())
            {
              urlInfo = urlE.getURL();
            }         
          }
           queue.offer(status.getText() + "DELIMITER" + geoInfo + "DELIMITER" + urlInfo + "DELIMITER" + countryName);
      }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice sdn) {}

    @Override
    public void onTrackLimitationNotice(int i) {}

    @Override
    public void onScrubGeo(long l, long l1) {}

    @Override
    public void onStallWarning(StallWarning warning){}

    @Override
    public void onException(Exception e) {
      e.printStackTrace();
    }
  };

  /**
   * Constructor for tweet spout that accepts the credentials
   */
  public TweetSpout( String key, String secret, String token, String tokensecret) {
    custkey = key;
    custsecret = secret;
    accesstoken = token;
    accesssecret = tokensecret;
  }

  @Override
  public void open( Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
    queue = new LinkedBlockingQueue<String>(1000);
    SentenceSentiment.init();
    collector = spoutOutputCollector;

    // build the config with credentials for twitter 4j
    ConfigurationBuilder config = new ConfigurationBuilder()
                                     .setOAuthConsumerKey(custkey)
                                     .setOAuthConsumerSecret(custsecret)
                                     .setOAuthAccessToken(accesstoken)
                                     .setOAuthAccessTokenSecret(accesssecret);

    TwitterStreamFactory fact = new TwitterStreamFactory(config.build());

    // get an instance of twitter stream
    twitterStream = fact.getInstance();    
    
    //filter non-english tweets
    FilterQuery tweetFilterQuery = new FilterQuery(); 
    tweetFilterQuery.language(new String[]{"en"});
    twitterStream.addListener(new TweetListener());
    twitterStream.filter(tweetFilterQuery);

    // start the sampling of tweets
    twitterStream.sample();
  }

  @Override
  public void nextTuple() {

    try {
      // try to pick a tweet from the buffer
      String ret = queue.poll();
      String geoInfo;
      String originalTweet;
      String extractedTweet;
      // if no tweet is available, wait for 50 ms and return
      if (ret == null) {
        Utils.sleep(50);
        return;
      } 
      else {
          geoInfo = ret.split("DELIMITER")[1];
          originalTweet = ret.split("DELIMITER")[0];
      }
      
      if(geoInfo != null && !geoInfo.equals("n/a")) {
          LOG.debug("\t DEBUG SPOUT: BEFORE EXTRACTOR \n");
          LOG.debug("\t " + originalTweet + "\n");
          extractedTweet = TweetExtractor.tweetRemover(originalTweet);
          LOG.debug("\t DEBUG SPOUT: AFTER EXTRACTOR \n");
          LOG.debug("\t " + extractedTweet);
          LOG.debug("\t DEBUG SPOUT: BEFORE SENTIMENT \n");
          int sentiment = SentenceSentiment.findSentiment(extractedTweet);
          LOG.debug("\t DEBUG SPOUT: AFTER SENTIMENT " +  Integer.toString(sentiment) + " for \t" + originalTweet + "\n" );
          collector.emit(new Values(ret, sentiment));
      }
    }
    catch (Exception e) {
      LOG.debug("\t DEBUG SPOUT:\tTweetSpout Exception in Execute function\n");
      LOG.debug(e);
    }

  }

  @Override
  public void close() {
    // shutdown the stream - when we are going to exit
    twitterStream.shutdown();
  }

  /**
   * Component specific configuration
   */
  @Override
  public Map<String, Object> getComponentConfiguration() {
    Config ret = new Config();
    ret.setMaxTaskParallelism(1);
    return ret;
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    outputFieldsDeclarer.declare(new Fields("tweet", "sentiment"));
  }
}
