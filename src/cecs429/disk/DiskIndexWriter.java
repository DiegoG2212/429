package cecs429.disk;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import cecs429.index.Index;
import cecs429.index.Posting;
import edu.csulb.PositionalInvertedIndexer;

public class DiskIndexWriter {

	public void WriteIndex(Index x, Path y) throws IOException {
		writeVocabTable(y, x);
		writeDocWeights(y, x);

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
			int lastDocId = 0;
			position = postingsOut.size(); // postion of the number of docs
			docPos.add(position); // Keep track of positions
			int numDocs = index.getPostings(i).size(); // Gets # of docs
			postingsOut.writeInt(numDocs); // Writes # of docs
			for (Posting p : index.getPostings(i)) { // Get posting for term
				//postingsOut.writeInt((int)position); // Write position
				int docID = p.getDocumentId() - lastDocId; // getting gaps for doc id
				lastDocId = p.getDocumentId(); // saving previous doc id
				postingsOut.writeInt(docID); // writing the doc id
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
			byte[] sbytes = i.getBytes("UTF-8");
			vocabOut.write(sbytes);
		}
		vocabOut.close();

		return vocabPos;
	}

	// vocabTable.bin
	private void writeVocabTable(Path path, Index index) throws IOException{
		System.out.println("Writing vocabTable.bin ...");
		List<Long> vocabPos = writeVocab(path, index);
		List<Long> docPos = writePostings(path, index);


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
	List<Double> holdLd = new ArrayList<Double>();
	// docLength
	List<Double> holdDocLengths = new ArrayList<Double>();
	// byteSize
	List<Double> holdByteSizes = new ArrayList<Double>();
	//aveTFtd
	List<Double> holdAvgTFtds = new ArrayList<Double>();
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

		System.out.println("Size Check: ");
		System.out.println(Lds.size());
		System.out.println(docLengths.size());
		System.out.println(byteSizes.size());
		System.out.println(avgTFs.size());
		System.out.println("End Size Check =============");


		for (int i = 0; i < holdLd.size(); i++) {

			System.out.println("docWeightsd Check: " +Lds.get(i));
			docWeightsOut.writeDouble(Lds.get(i));
			System.out.println("docLengthd Check: " +docLengths.get(i));
			docWeightsOut.writeDouble(docLengths.get(i));
			System.out.println("byteSize Check: " +byteSizes.get(i));
			docWeightsOut.writeDouble(byteSizes.get(i));
			System.out.println("ave(tftd) Check: " +avgTFs.get(i));
			docWeightsOut.writeDouble(avgTFs.get(i));
		}

		//docWeightsOut.writeDouble(Ld);
		docWeightsOut.close();
		writeAverageDocLength(path, docLengths);
	}

	private void writeAverageDocLength(Path path, List<Double> docLengths) throws IOException {
		DataOutputStream aveLength = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(path + "/avgLength.bin")));
		double avg = 0;

		for (double d : docLengths) {
			avg += d;
		}

		avg /= docLengths.size();
		System.out.println("DocLengthAvg Check: "+avg);
		aveLength.writeDouble(avg);

		aveLength.close();
	}

}