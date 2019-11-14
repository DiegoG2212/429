package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements QueryComponent {
	// The list of individual terms in the phrase.
	private List<String> mTerms = new ArrayList<>();
	
	/**
	 * Constructs a PhraseLiteral with the given individual phrase terms.
	 */
	public PhraseLiteral(List<String> terms) {
		mTerms.addAll(terms);
	}
	
	/**
	 * Constructs a PhraseLiteral given a string with one or more individual terms separated by spaces.
	 */
	public PhraseLiteral(String terms) {

		mTerms.addAll(Arrays.asList(terms.replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "").split(" ")));

	}

	public List<Posting> getPostings(Index index) {
		//return null;

		List <Posting> result = Collections.emptyList();
		if (mTerms.size() == 2) {
			String temp = mTerms.get(0) + " " + mTerms.get(1);
			return index.getPostings(temp);
		}

		if (!(mTerms.isEmpty())) {
			if (mTerms.size() > 2 ) {
				int i = 0;

				while(i < mTerms.size()) {
					if (i < 2) {

						result = andPos(index.getPositionalPostings(mTerms.get(i)), index.getPositionalPostings(mTerms.get(i+1)));
						i = i + 2;
					}else {
						result = andPos(result, index.getPositionalPostings(mTerms.get(i)));
						i++;
					}
				}


			}else { result = index.getPositionalPostings(mTerms.get(0));}

		}
		return result;
		// TODO: program this method. Retrieve the postings for the individual terms in the phrase,
		// and positional merge them together.
	}
	/*
	@Override
	public List<Posting> getPostings(Index index) {
		//return null;
		
		List <Posting> result = Collections.emptyList();
		if (mTerms.size() == 2) {
			String temp = mTerms.get(0) + " " + mTerms.get(1);
			return index.getPostings(temp);
		}
		
		if (!(mTerms.isEmpty())) {
			if (mTerms.size() > 2 ) {
				int i = 0;
				
				while(i < mTerms.size()) {
					if (i < 2) {
						 
						result = andPos(index.getPostings(mTerms.get(i)), index.getPostings(mTerms.get(i+1)));
						i = i + 2;
						}else {
							result = andPos(result, index.getPostings(mTerms.get(i)));
							i++;
						}
				}
				
				
			}else { result = index.getPostings(mTerms.get(0));}
			
		}		
		return result;
		// TODO: program this method. Retrieve the postings for the individual terms in the phrase,
		// and positional merge them together.
	}
	*/
	
	private List<Posting> andPos(List<Posting>var1, List<Posting>var2){
		List <Posting> result = new ArrayList<>(); // result query is the answer 
		int i = 0;  // pointers that go thru the arrays
		int j = 0;
		
		if ((var1.isEmpty()) || (var2.isEmpty())) { // checking if any of the list are empty so we retunr an empty list
			List<Posting> p = Collections.emptyList();
			return p;
		}
		
		while((i<var1.size()) && (j < var2.size())) { // checking that our pointers are not bigger than the size of the list i am sending
													  // if we are leave the loop and return the result
			
			if (var1.get(i).getDocumentId() == var2.get(j).getDocumentId()) { // if the doc ids of the two posting matches
																		  // bingo we found the answer and added to our result 
				
				int x = 0;
				int y = 0;
				List <Integer> temp1 = var1.get(i).getPos();
				List <Integer> temp2 = var2.get(j).getPos();
				if ((temp1.isEmpty()) || (temp2.isEmpty())) {
					i++;
					j++;
				}else {
				
					while((x < temp1.size()) && (y < temp2.size())) { // if any of the pointers is = or > to the size of the array stop checking
						
						if((temp1.get(x)+1) == temp2.get(y)) { // if they are consecutive to each other u found an answer leave no need to check the rest.
							result.add(var2.get(j));
							x = temp1.size(); 
						}else if(temp1.get(x) < temp2.get(y)) { // other wise check which one is bigger and increase the opposite pointer
							x++;								 // that point to the lesser position
						}else {
							y++;
						}
					}
					i++;
					j++;
				}
				
			}else if (var1.get(i).getDocumentId() < var2.get(j).getDocumentId()) { // other wise check which one is bigger and increase the opposite pointer 
				i++;															   // that point to the lesser doc ID
			}else {
				j++;
			}
			
		}
		
		return result; 
	}
	
	@Override
	public String toString() {
		return "\"" + String.join(" ", mTerms) + "\"";
	}
}
