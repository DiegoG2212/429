package cecs429.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BiwordIndex implements Index {
	private HashMap<String, ArrayList<Posting>> map;

	public String t1;
	public String t2;

	public BiwordIndex() {
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
	
	public void addT1(List<String> term) {
		for (String i : term)
			this.t1 = i;
	}
	
	public void addT2(List<String> term) {
		for (String i : term)
			this.t2 = i;
	}

	public void addTerms(int docID, int pos) {
		if (!map.containsKey(this.t1 + " " + this.t2)) { // If map doesn't contain term
			Posting p = new Posting(docID, pos); // Add term to map
			ArrayList<Posting> l = new ArrayList<Posting>();
			l.add(p);
			map.put(this.t1 + " " + this.t2, l);
		} else {
			List<Posting> temp = map.get(this.t1 + " " + this.t2);

			if (docID == temp.get(temp.size() - 1).getDocumentId()) {
				temp.get(temp.size() - 1).addPos(pos);
				return;
			} else {
				Posting p = new Posting(docID, pos);
				temp.add(p);
				map.put(this.t1 + " " + this.t2, new ArrayList<>(temp));
			}
		}
	}
}
