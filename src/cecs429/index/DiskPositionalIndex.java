package cecs429.index;

import java.util.List;

public class DiskPositionalIndex implements Index {

	@Override
	public List<Posting> getPostings(String term) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getVocabulary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTerm(List<String> term, int docID, int pos) {
		// TODO Auto-generated method stub
		
	}
	
	// Reads docWeights.bin to skip to an appropriate location
	// to read an 8-byte double for Ld
	public void readDocWeights() {
		
	}

}
