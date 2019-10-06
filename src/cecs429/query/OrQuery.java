package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An OrQuery composes other QueryComponents and merges their postings with a union-type operation.
 */
public class OrQuery implements QueryComponent {
	// The components of the Or query.
	private List<QueryComponent> mComponents;
	
	public OrQuery(List<QueryComponent> components) {
		mComponents = components;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		List<Posting> result = Collections.emptyList();;
		
		// TODO: program the merge for an OrQuery, by gathering the postings of the composed QueryComponents and
		// unioning the resulting postings.
		
		if (!(mComponents.isEmpty())) {
			int i =0;
			while (i < mComponents.size()) {
				List <Posting> var1 = mComponents.get(i).getPostings(index);
				result = orQuery(var1, result);
				i++;
			}
			
		}
		
		
		return result;
	}
	
	
	private List<Posting> orQuery(List<Posting>var1, List<Posting> var2){
		List <Posting> result = new ArrayList<>(); // result query is the answer 
		int i = 0;  // pointers that go thru the arrays
		int j = 0;
		
		if ((var1.isEmpty()) && (var2.isEmpty())) { // checking if both of the list are empty so we return an empty list
			List<Posting> p = Collections.emptyList();
			return p;
		}else if (var1.isEmpty()) { // if one of the lists are empty retrun the other one
			return var2;
		}else if (var2.isEmpty()) {
			return var1;
		}
		
		while((i<var1.size()) && (j < var2.size())) { // checking that our pointers are not bigger than the size of the list i am sending
													  // if we are leave the loop and return the result
			
			if (var1.get(i).getDocumentId() == var2.get(j).getDocumentId()) { // if the doc ids of the two posting matches
																			  // bingo we found the answer and added to our result 
				result.add(var1.get(i));
				i++;
				j++;
			}else if (var1.get(i).getDocumentId() < var2.get(j).getDocumentId()) { // other wise check which one is bigger and increase the opposite pointer 
				result.add(var1.get(i));
				i++;															   // that point to the lesser doc ID
			}else {
				result.add(var2.get(j));
				j++;
			}
			
		}
		
		if (i == var1.size()) {   // checking for any left over values in the lsit if there is add them to the result and then return the result
			if(j != var2.size()) {
				while(j < var2.size()) {
					result.add(var2.get(j));
					j++;
				}
			}
		}else if (j == var2.size()) {
			while( i < var1.size()) {
				result.add(var1.get(i));
				i++;
			}
		}
		
		
		return result;
	}
	
	
	@Override
	public String toString() {
		// Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]"
		return "(" +
		 String.join(" + ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()))
		 + " )";
	}
}
