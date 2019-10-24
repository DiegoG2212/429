package cecs429.index;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.csulb.PositionalInvertedIndexer;

public class DiskIndexWriter {

	public void WriteIndex(Index x, Path y) throws IOException {
		writeVocabTable(y,x);
		writeDocWeight(y,x);
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

			position = postingsOut.size() - position; // Get gap
			docPos.add(position); // Keep track of positions
			int docNum = index.getPostings(i).size(); // Gets # of docs
			postingsOut.writeInt(docNum); // Writes # of docs
			for (Posting p : index.getPostings(i)) { // Get posting for term
				postingsOut.writeInt((int)position); // Write position
				int posNum = p.getPos().size();	// Get # of positions
				postingsOut.writeInt(posNum); // Writes # of positions
				for(int x: p.getPos()) {	// For every position
					postingsOut.writeInt(x);	// Write position
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
			position =  vocabOut.size();
			vocabPos.add(position);
			//System.out.println(position);
			//System.out.println("Vocab: "+ i);
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
	}
	
	// List of HashMap
	List< HashMap<String, Integer> > holdTerms = new ArrayList< HashMap<String, Integer> >();
	// docWeights.bin
	public void addDocWeight(HashMap<String,Integer> terms) throws IOException {
		holdTerms.add(terms);
	}
	
	private void writeDocWeight(Path path, Index index) throws IOException {
		System.out.println("Writing docWeights.bin ...");
		DataOutputStream docWeightsOut = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(path + "/docWeights.bin")));
		
		// Iterate List of HashMaps
		for(HashMap<String, Integer> scan: holdTerms) { // For every Document's HashMap
			double wSum = 0;
			for (HashMap.Entry<String, Integer> entry : scan.entrySet()) { // Go through HashMap
				System.out.println("Key: "+entry.getKey() +", Value: "+entry.getValue());
				wSum += Math.pow( (1 + Math.log( entry.getValue() )) ,2);
				//System.out.println(wSum);
			}
			double Ld = Math.sqrt(wSum);
			System.out.println("Document Weight: " +Ld);
			docWeightsOut.writeDouble(Ld);
		}
		
		docWeightsOut.close();
	}
	
}
