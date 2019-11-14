package cecs429.index;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class DiskPositionalIndex implements Index {


	private HashMap<String, ArrayList<Posting>> map;

	// Maps docIDs to terms appearing in that doc
	private HashMap<Integer, HashMap<String, Integer>> docTerms;

	@Override
	public List<Posting> getPostings(String term) {
		if (map.containsKey(term)) {
			return map.get(term);
		} else {
			List<Posting> p = Collections.emptyList();
			return p;
		}
	}

    public List<Posting> getPositionalPostings(String term) {
        return Collections.emptyList();
    }

	@Override
	public List<String> getVocabulary() {
		List<String> vocab = new ArrayList<String>();
		for (Map.Entry<String, ArrayList<Posting>> entry : map.entrySet()) {
			vocab.add(entry.getKey());
		}
		return vocab;
	}

    @Override
    public void addTerm(List<String> term, int docID, int pos) {
        for (String i : term) {
            if (!map.containsKey(i)) { // If map doesn't contain term
                Posting p = new Posting(docID, pos); // Add term to map
                ArrayList<Posting> l = new ArrayList<Posting>();
                l.add(p);
                map.put(i, l);
            } else {
                List<Posting> temp = map.get(i);

                if (docID == temp.get(temp.size() - 1).getDocumentId()) {
                    temp.get(temp.size() - 1).addPos(pos);
                    return;
                } else {
                    Posting p = new Posting(docID, pos);
                    temp.add(p);
                    map.put(i, new ArrayList<>(temp));
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
    public void readDocWeights() {

    }

}