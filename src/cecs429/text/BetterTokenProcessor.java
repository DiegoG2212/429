package cecs429.text;

import java.util.ArrayList;
import java.util.List;
import org.tartarus.snowball.ext.englishStemmer;

public class BetterTokenProcessor implements TokenProcessor {

	@Override
	public List<String> processToken(String token) {
		
		List<String> a = new ArrayList();
		englishStemmer stemmer = new englishStemmer();
		
		token = token.replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", ""); // This removes any non alphanumeric values at the beginning or end
		token = token.replaceAll("\'+|\"", "");	// This removes " and ' from anywhere in the string
			
		if (token.contains("-")) {
			String b[] = token.split("[-]+");
			a.add(token.replaceAll("-",""));
			for (String i : b) {
				i = i.replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", ""); // This removes any non alphanumeric values at the beginning or end

				i = i.replaceAll("\'+|\"", "");	
				
				//a.add(i.toLowerCase());
				stemmer.setCurrent(i.toLowerCase());
				if(stemmer.stem()) {
					a.add(stemmer.getCurrent());
				}	
			}
		}
		else { 
			stemmer.setCurrent(token.toLowerCase());
			if(stemmer.stem()) {
				a.add(stemmer.getCurrent());}
			}
		return a;
	}

}
