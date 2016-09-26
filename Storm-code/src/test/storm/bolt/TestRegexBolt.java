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

public class TestRegexBolt extends TestCase {

	public void testExecute() throws Exception {
		CountBolt bolt = new CountBolt();
		OutputCollector collector = mock(OutputCollector.class);
		Tuple tuple = mock(Tuple.class);
		String tweet = "Hello, This is a Test. @uoit :)DELIMITER37.7833,122.4167DELIMITERUSA";
		int sentiment = 2;

		bolt.prepare(null, null, collector);



        when(tuple.getStringByField("tweet")).thenReturn(tweet);
		when(tuple.getIntegerByField("sentiment")).thenReturn(sentiment);
        bolt.execute(tuple);
	}
}