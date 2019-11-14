package cecs429.index;

import java.util.List;

/**
 * An Index can retrieve postings for a term from a data structure associating terms and the documents
 * that contain them.
 */
public interface Index {
	/**
	 * Retrieves a list of Postings of documents that contain the given term.
	 */
	List<Posting> getPostings(String term);

	/**
	 *  Retrieves postings with the positions of the term.
	 */
	List<Posting> getPositionalPostings(String term);

	/**
	 * A (sorted) list of all terms in the index vocabulary.
	 */
	List<String> getVocabulary();
	
	/**
	 * 
	 * Adds term into index
	 */
	void addTerm(List<String> term, int docID, int pos);
}
