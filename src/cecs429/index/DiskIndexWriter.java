package cecs429.index;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.csulb.PositionalInvertedIndexer;

public class DiskIndexWriter {

	public void WriteIndex(Index x, Path y) throws IOException {

		// Sub Methods

		writeVocabTable(y,x);
	}
	
	// postings.bin
	private List<Long> writePostings(Path path, Index index) throws IOException {
		System.out.println("Writing postings.bin ...");
		List<Long> docPos = new ArrayList<>();

		DataOutputStream postingsOut = new DataOutputStream(
				new BufferedOutputStream(
				new FileOutputStream(path + "/postings.bin")));	
		
		List<String> t = index.getVocabulary();
		long position = 0;
		for (String i : t) {// Go through vocabulary

			position = postingsOut.size() - position;
			docPos.add(position);
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

		return docPos;
	}
	
	// vocab.bin encoded using UTF-8
	private List<Long> writeVocab(Path path, Index index) throws IOException{
		System.out.println("Writing vocab.bin ...");
		List<Long> vocabPos = new ArrayList<>();
		DataOutputStream vocabOut = new DataOutputStream(
				new BufferedOutputStream(
				new FileOutputStream(path + "/vocab.bin")));
		
		List<String> t = index.getVocabulary();	// Get vocab from index
		long position = 0;

		for (String i : t) {	// Go through vocabulary, write to vocab.bin

			position =  vocabOut.size() - position;
			vocabPos.add(position);
			System.out.println(position);
			System.out.println("Vocab: "+ i);
			vocabOut.writeUTF(i);	// UTF-8 Encoded
			
		}
		vocabOut.close();

		return vocabPos;
	}
		

	
	// vocabTable.bin
	private void writeVocabTable(Path path, Index index) throws IOException{
		System.out.println("Writing vocabTable.bin ...");



		List<Long> docPos = writePostings(path, index);
		List<Long> vocabPos = writeVocab(path, index);


		DataOutputStream vtableOut = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(path + "/vocabTable.bin")));


		for (int i = 0; i <docPos.size(); i++){

			long vPos = vocabPos.get(i);
			long dPos = docPos.get(i);

			vtableOut.writeLong(vPos);
			vtableOut.writeLong(dPos);
			}

		vtableOut.close();
		
		
		
		
		vtableOut.close();
	}

}
