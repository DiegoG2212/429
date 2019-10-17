package cecs429.index;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import edu.csulb.PositionalInvertedIndexer;

public class DiskIndexWriter {
	Index index;	// Index
	Path path; 		//  points to the directory for saving the three index files
	public void WriteIndex(Index x, Path y) throws IOException {
		index = x;
		path = y;
		// Sub Methods
		
		writePostings();
		writeVocab();
		//writeVocabTable();	
	}
	
	// postings.bin
	void writePostings() throws IOException {
		System.out.println("Writing postings.bin ...");
		DataOutputStream postingsOut = new DataOutputStream(
				new BufferedOutputStream(
				new FileOutputStream(path + "/postings.bin")));	
		
		List<String> t = index.getVocabulary();
		for (String i : t) {	// Go through vocabulary
			for (Posting p : index.getPostings(i)) { // Get posting for term
				postingsOut.writeInt(p.getDocumentId());
				//System.out.println(p.getDocumentId());
				for(int x: p.getPos()) {
					//System.out.println(x);
					postingsOut.writeInt(x);
				}
			}	
		}
		postingsOut.close();
	}
	
	// vocab.bin encoded using UTF-8
	public void writeVocab() throws IOException{
		System.out.println("Writing vocab.bin ...");
		
		DataOutputStream vocabOut = new DataOutputStream(
				new BufferedOutputStream(
				new FileOutputStream(path + "/vocab.bin")));
		
		List<String> t = index.getVocabulary();	// Get vocab from index
		
		int counter = 0;
		for (String i : t) {	// Go through vocabulary, write to vocab.bin
			long position = vocabOut.size();
			
			System.out.println(position);
			System.out.println("Vocab: "+ i);
			vocabOut.writeUTF(i);	// UTF-8 Encoded
			counter++;
			if(counter == 10) { // Stops at 10 terms
				position = vocabOut.size();
				System.out.println(position);
				break;
			}
			
			
		}
		vocabOut.close();
	}
		

	
	// vocabTable.bin
	public void writeVocabTable() throws FileNotFoundException{
		System.out.println("Writing vocabTable.bin ...");
		DataOutputStream vtableOut = new DataOutputStream(
				new BufferedOutputStream(
				new FileOutputStream(path + "/vocabTable.bin")));
		
		
		
		
		
	}

}
