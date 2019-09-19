package cecs429.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class InvertedIndex implements Index {

	private HashMap<String, List<Posting>> map;

	public InvertedIndex() {
		map = new HashMap<String, List<Posting>>();

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

	public void addTerm(String term, int docID) {
		if (!map.containsKey(term)) { // If map doesn't contain term
			Posting p = new Posting(docID);	// Add term to map
			List<Posting> l = new ArrayList<Posting>();
			l.add(p);
			map.put(term, l);
		} else {
			List<Posting> temp = map.get(term);

			if (docID == temp.get(temp.size() - 1).getDocumentId()) {
				return;
			} else {
				Posting p = new Posting(docID);
				temp.add(p);
				map.put(term, new ArrayList<>(temp));
			}
		}
	}
	


}
