package storm.bolt;


import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import junit.framework.TestCase;


/**
 * @author huangyu.wuhan@gmail.com
 *
 */

public class TestCountBolt extends TestCase {

	public void testExecute() throws Exception {
		CountBolt bolt = new CountBolt();
		OutputCollector collector = mock(OutputCollector.class);
		Tuple tuple = mock(Tuple.class);
		String originalTweet = "Hello, This is a Test. @uoit :)";
		String geoInfo = "37.7833,122.4167";
		String matchedEmoticon = ":)";
		int matchedEmoticonScore = 1;
		int sentiment = 2;
		String countryName = "USA";

		bolt.prepare(null, null, collector);



        when(tuple.getStringByField("original-tweet")).thenReturn(originalTweet);
		when(tuple.getStringByField("geoinfo")).thenReturn(geoInfo);
		when(tuple.getStringByField("matchedEmoticon")).thenReturn(matchedEmoticon);
		when(tuple.getIntegerByField("matchedEmoticonScore")).thenReturn(matchedEmoticonScore);
		when(tuple.getIntegerByField("sentiment")).thenReturn(sentiment);
		when(tuple.getStringByField("countryName")).thenReturn(countryName);
        bolt.execute(tuple);
	}
}