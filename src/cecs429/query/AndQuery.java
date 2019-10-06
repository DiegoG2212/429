package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An AndQuery composes other QueryComponents and merges their postings in an intersection-like operation.
 */
public class AndQuery implements QueryComponent {
	private List<QueryComponent> mComponents;
	
	public AndQuery(List<QueryComponent> components) {
		mComponents = components;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		List<Posting> result = Collections.emptyList();;
		
		if (mComponents.size() >= 2) {
			int i = 0;
			while(i < mComponents.size()) {
				
				if (i < 2) {
					 
					result = andQuery(mComponents.get(i).getPostings(index), mComponents.get(i+1).getPostings(index) );
					i = i + 2;
					}else {
						List <Posting> temp =  mComponents.get(i).getPostings(index);
						result = andQuery(result, temp);
						i++;
					}
			}
		}
		
		// TODO: program the merge for an AndQuery, by gathering the postings of the composed QueryComponents and
		// intersecting the resulting postings.
		
		return result;
	}
	
	
	
	private List<Posting> andQuery(List<Posting>var1, List<Posting> var2){
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
				result.add(var1.get(i));
				i++;
				j++;
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
		return
		 String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}
}
