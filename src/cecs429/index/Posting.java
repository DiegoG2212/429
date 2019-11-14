package cecs429.index;

import java.util.ArrayList;
import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	int mDocumentId;
	private ArrayList <Integer> pos;
	private double accumulator;

	/*
	public Posting(int documentId, double acc){
		mDocumentId = documentId;
		accumulator = acc;
	}

	 */

	public Posting(int documentId, int position) {
		mDocumentId = documentId;
		pos = new ArrayList<>();
		pos.add(position);
		
	}

	public double getAccumulator(){ return accumulator;}


	public void addPos (int position) {
			pos.add(position);
	}
	
	public ArrayList<Integer> getPos() {
		return pos;
	}
	
	public int getDocumentId() {
		return mDocumentId;
	}
	
	
}
