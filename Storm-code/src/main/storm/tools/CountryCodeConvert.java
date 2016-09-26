package storm.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Locale;


/**
 *  Convert country code: alpha-2 to alpha-3
 */
public class CountryCodeConvert {
	private Map<String, Locale> localeMap;

	// singleton
	public static void initCountryCodeMapping() {
	    String[] countries = Locale.getISOCountries();
	    localeMap = new HashMap<String, Locale>(countries.length);
	    for (String country : countries) {
	        Locale locale = new Locale("", country);
	        localeMap.put(locale.getISO3Country().toUpperCase(), locale);
	    }
	}

	public static String iso2CountryCodeToIso3CountryCode(String iso2CountryCode){
	    Locale locale = new Locale("", iso2CountryCode);
	    return locale.getISO3Country();
	}

}