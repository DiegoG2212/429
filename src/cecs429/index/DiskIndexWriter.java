package cecs429.index;

import java.nio.file.Paths;

public class DiskIndexWriter {
	// path is to the directory for saving the three index files
	public void WriteIndex(Index w, Paths path) {
		writePostings();
		writeVocab();
		writeVocabTable();
	}
	
	public void writePostings() {
		
	}
	public void writeVocab() {
		
	}
	public void writeVocabTable() {
		
	}

}
