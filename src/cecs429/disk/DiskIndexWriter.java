package cecs429.disk;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import cecs429.index.Index;
import cecs429.index.Posting;
import edu.csulb.PositionalInvertedIndexer;

public class DiskIndexWriter {

	public void WriteIndex(Index x, Path y) throws IOException {
		writeVocabTable(y,x);
		writeDocWeights(y,x);
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
	//List< HashMap<String, Integer> > holdTerms = new ArrayList< HashMap<String, Integer> >();

	// docWeights
	List<Double> holdLd = Collections.emptyList();
	// docLength
	List<Double> holdDocLengths = Collections.emptyList();
	// byteSize
	List<Double> holdByteSizes = Collections.emptyList();
	//aveTFtd
	List<Double> holdAvgTFtds = Collections.emptyList();
	// docWeights.bin
	//public void addDocWeight(HashMap<String,Integer> terms) throws IOException {
	public void addDocWeight(double add) throws IOException {
		holdLd.add(add);
	}

	public void addDocLength(double add) throws IOException {
		holdDocLengths.add(add);
	}

	public void addByteSize(double add) throws IOException {
		holdByteSizes.add(add);
	}

	public void addAvgTFs(double add) throws IOException {
		holdAvgTFtds.add(add);
	}

	public List<Double> getDocWeights() {
		return this.holdLd;
	}

	public List<Double> getDocLengths() {
		return this.holdDocLengths;
	}

	public List<Double> getByteSizes() {
		return this.holdByteSizes;
	}

	public List<Double> getAvgTFs() {
		return this.holdAvgTFtds;
	}

	private void writeDocWeights(Path path, Index index) throws IOException {
		System.out.println("Writing docWeights.bin ...");
		DataOutputStream docWeightsOut = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(path + "/docWeights.bin")));

		List<Double> Lds = getDocWeights();
		List<Double> docLengths = getDocLengths();
		List<Double> byteSizes = getByteSizes();
		List<Double> avgTFs = getAvgTFs();

		for (int i = 0; i < holdLd.size(); i++) {
			double Ld = Lds.get(i);

			docWeightsOut.writeDouble(Ld);
		}

		//docWeightsOut.writeDouble(Ld);
		docWeightsOut.close();
	}
	
}
