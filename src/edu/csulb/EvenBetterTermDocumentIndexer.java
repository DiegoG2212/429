package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.index.InvertedIndex;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.EnglishTokenStream;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;


public class EvenBetterTermDocumentIndexer {

	public static void main(String[] args) {
		DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get("C:\\Users\\iamno\\Desktop\\temp").toAbsolutePath(), ".json");
		Index index = indexCorpus(corpus);
		// We aren't ready to use a full query parser; for now, we'll only support
		// single-term queries.
		String query = ""; // hard-coded search for "whale"
		Scanner scan = new Scanner(System.in);
		while (!query.equals("quit")) {
			System.out.println("Enter a term to search: ");
			query = scan.nextLine();
			query = query.toLowerCase();
			for (Posting p : index.getPostings(query)) {
				System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle());
			}
		}
		scan.close();
		System.out.println("Quitting...");
	}
	
	
	private static Index indexCorpus(DocumentCorpus corpus) {
		BasicTokenProcessor processor = new BasicTokenProcessor();
		
		// First, build the vocabulary hash set.
		
		
		// TODO:
		// Get all the documents in the corpus by calling GetDocuments().
		
		// Iterate through the documents, and:
		// Tokenize the document's content by constructing an EnglishTokenStream around the document's content.
		// Iterate through the tokens in the document, processing them using a BasicTokenProcessor,
		//		and adding them to the HashSet vocabulary.
		
		// TODO:
		// Constuct a TermDocumentMatrix once you know the size of the vocabulary.
		// THEN, do the loop again! But instead of inserting into the HashSet, add terms to the index with addPosting.
			
		
		InvertedIndex tdi = new InvertedIndex();
		
		for (Document d : corpus.getDocuments()) {
			EnglishTokenStream stream = new EnglishTokenStream(d.getContent());
			
			for (String token: stream.getTokens()) {
				tdi.addTerm(processor.processToken(token), d.getId());
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

	
	
