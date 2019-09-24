package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.index.InvertedIndex;
import cecs429.index.PositionalInvertedIndex;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.BetterTokenProcessor;
import cecs429.text.EnglishTokenStream;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class PositionalInvertedIndexer {

	public static void main(String[] args) {
		DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get("C:\\Users\\diego\\Desktop\\boii").toAbsolutePath(), ".txt");
		Index index = indexCorpus(corpus);

		List<String> t = index.getVocabulary();
		
		for (String i : t) {
			System.out.println(i);
		}
		
		String query = "";
		Scanner scan = new Scanner(System.in);
		while (!query.equals("quit")) {
			System.out.println("Enter a term to search: ");
			query = scan.nextLine();
			query = query.toLowerCase();
			int docCount = 0;
			for (Posting p : index.getPostings(query)) {
				System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle());
				System.out.println("Positions: " + p.getPos());
				docCount++;
			}
			System.out.println("Number of Documents:" + docCount);
			System.out.println();
		}
		scan.close();
		System.out.println("Quitting...");
	}
	
	
	private static Index indexCorpus(DocumentCorpus corpus) {
		
		BetterTokenProcessor processor = new BetterTokenProcessor();
		PositionalInvertedIndex tdi = new PositionalInvertedIndex();
	
		
		// Loops through documents
		for (Document d : corpus.getDocuments()) {
			int x = 0; //Reset counter
			// Creates tokens by splitting on whitespace
			EnglishTokenStream stream = new EnglishTokenStream(d.getContent());
			
			// Adds term to index along with Document ID
			for (String token: stream.getTokens()) {
				//System.out.println(processor.processToken(token));
				tdi.addTerm(processor.processToken(token), d.getId(), x);
				x++;
			}
			
			try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}	
		return tdi;
	}
}

	
	
