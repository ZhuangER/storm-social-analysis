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

public class TestReportBolt extends TestCase {

	public void testExecute() throws Exception {
		CountBolt bolt = new CountBolt();
		OutputCollector collector = mock(OutputCollector.class);
		Tuple tuple = mock(Tuple.class);
		String tweet = "Hello, This is a Test. @uoit :)";
		String geoInfo = "37.7833,122.4167";
		int personalSentiment = 2;
		double countrySentiemnt = 2.32321;
		String countryName = "USA";

		bolt.prepare(null, null, collector);



        when(tuple.getStringByField("tweet")).thenReturn(tweet);
		when(tuple.getStringByField("geoinfo")).thenReturn(geoInfo);
		when(tuple.getIntegerByField("personalSentiment")).thenReturn(personalSentiment);
		when(tuple.getDoubleByField("countrySentiment")).thenReturn(countrySentiemnt);
		when(tuple.getStringByField("countryName")).thenReturn(countryName);
        bolt.execute(tuple);
	}
}