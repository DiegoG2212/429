package cecs429.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.mapdb.btree.*;
public class DiskPositionalIndex implements Index {

	private BTreeMap<String, ArrayList<Posting>> T;
	
	// Maps docIDs to terms appearing in that doc
	private HashMap<Integer, HashMap<String, Integer>> docTerms;
	@Override
	public List<Posting> getPostings(String term) {
		if (T.containsKey(term)) {
			return T.get(term);
		} else {
			List<Posting> p = Collections.emptyList();
			return p;
		}
	}

	@Override
	public List<String> getVocabulary() {
		List<String> vocab = new ArrayList<String>();
		for (Map.Entry<String, ArrayList<Posting>> entry : T.entrySet()) {
			vocab.add(entry.getKey());
		}
		return vocab;
	}

	@Override
	public void addTerm(List<String> term, int docID, int pos) {
		for (String i : term) {
			if (!T.containsKey(i)) { // If map doesn't contain term
				Posting p = new Posting(docID, pos); // Add term to map
				ArrayList<Posting> l = new ArrayList<Posting>();
				l.add(p);
				T.put(i, l);
			} else {
				List<Posting> temp = T.get(i);

				if (docID == temp.get(temp.size() - 1).getDocumentId()) {
					temp.get(temp.size() - 1).addPos(pos);
					return;
				} else {
					Posting p = new Posting(docID, pos);
					temp.add(p);
					T.put(i, new ArrayList<>(temp));
				}
			}
		}
		
	}
	
	public void addDocTerms(HashMap<String, Integer> terms, int docID) {
		docTerms.put(docID, terms);	
	}
	
	public HashMap<Integer, HashMap<String, Integer>> getdocTerms(){
		return docTerms;
	}
	
	// Reads docWeights.bin to skip to an appropriate location
	// to read an 8-byte double for Ld
	public void readDocWeights(String binLocation) {
		try (InputStream in = new FileInputStream(binLocation);) {
			int byteRead;

			while ((byteRead = in.read()) != -1) {

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
