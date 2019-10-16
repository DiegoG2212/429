package cecs429.index;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import edu.csulb.PositionalInvertedIndexer;

public class DiskIndexWriter {
	
	Index index;	// Index
	Path path; 		//  points to the directory for saving the three index files
	public void WriteIndex(Index x, Path y) throws FileNotFoundException {
		index = x;
		path = y;
		
		// Sub Methods
		writePostings();
		writeVocab();
		writeVocabTable();
	}
	
	// postings.bin
	public void writePostings() throws FileNotFoundException {
		DataOutputStream postingsOut = new DataOutputStream(new FileOutputStream(path + ""));
		
	}
	
	// vocab.bin encoded using UTF-8
	public void writeVocab() throws FileNotFoundException{
		DataOutputStream vocabOut = new DataOutputStream(
				new BufferedOutputStream(
				new FileOutputStream(path + "/vocab.bin")));
		List<String> t = index.getVocabulary();
		// vocabOut.writeUTF
		
	}
	
	// vocabTable.bin
	public void writeVocabTable() throws FileNotFoundException{
		DataOutputStream vtableOut = new DataOutputStream(new FileOutputStream(path + ""));
		
	}

}
