package cecs429.text;

import java.util.ArrayList;
import java.util.List;

public class BetterTokenProcessor implements TokenProcessor {

	@Override
	public List<String> processToken(String token) {
		
		List<String> a = new ArrayList();
		
		token = token.replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", ""); // this removes any non alphanumeric values at the beginning or end
		token = token.replaceAll("\'+|\"", "");	//this removes " and ' from anywhere in the string
		
		
		if (token.contains("-")) {
			String b[] = token.split("-");
			a.add(token.replaceAll("-",""));
			for (String i : b) {
				if (!(i.isEmpty())){
				a.add(i.toLowerCase());
				}
			}
		}else { a.add(token.toLowerCase());}
		return a;
	}

}
