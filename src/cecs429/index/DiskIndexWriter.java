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
		//writePostings();
		writeVocab();
		//writeVocabTable();
	}
	
	// postings.bin
	public void writePostings() throws IOException {
		DataOutputStream postingsOut = new DataOutputStream(new FileOutputStream(path + ""));
		
	}
	
	// vocab.bin encoded using UTF-8
	public void writeVocab() throws IOException{
		System.out.println("Writing vocab.bin ...");
		
		DataOutputStream vocabOut = new DataOutputStream(
				new BufferedOutputStream(
				new FileOutputStream(path + "/vocab.bin")));
		
		List<String> t = index.getVocabulary();
		for (String i : t) {	// Go through vocabulary, write to vocab.bin
			vocabOut.writeUTF(i);
		}
		vocabOut.close();
	}
	
	// vocabTable.bin
	public void writeVocabTable() throws FileNotFoundException{
		DataOutputStream vtableOut = new DataOutputStream(new FileOutputStream(path + ""));
		
	}

}
