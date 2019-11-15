package cecs429.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PositionalInvertedIndex implements Index {

	private HashMap<String, ArrayList<Posting>> map;

	public PositionalInvertedIndex() {
		map = new HashMap<String, ArrayList<Posting>>();
	}

	@Override
	public List<Posting> getPostings(String term) {
		if (map.containsKey(term)) {
			return map.get(term);
		} else {
			List<Posting> p = Collections.emptyList();
			return p;
		}
	}

	@Override
	public List<String> getVocabulary() {
		Set<String> key = map.keySet();
		int n = key.size();
		List<String> val = new ArrayList<String>(n);

		for (String s : key) {
			val.add(s);
		}

		Collections.sort(val);

		return val;
	}


	public List<Posting> getPositionalPostings(String term) {
		return Collections.emptyList();
	}

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

	public List<Double> getLds(){
		return null;
	}

	public double getDocLengths(int docID){return 0;}
	public double getByteSizes(int docID){return 0;}
	public double getAvgTFtds(int docID){return 0;}
	public double getDocLengthAvg(){return 0;}

}
