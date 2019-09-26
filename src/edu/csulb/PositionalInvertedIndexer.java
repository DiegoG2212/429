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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class PositionalInvertedIndexer {
	public static void main(String[] args) {
		DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get("C:\\Users\\diego\\Desktop\\boii").toAbsolutePath(), ".txt");
		Index index = indexCorpus(corpus);
		*/
		
		// GUI
		DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get("C:\\Users\\potad\\eclipse-workspace\\JSON Files").toAbsolutePath(), ".json");
		Index index = indexCorpus(corpus);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {			
				// Frame
				JFrame frame = new JFrame("Search Engine");			
				// Panels
				JPanel p = new JPanel();
				// Components
				JTextField textField = new JTextField(16);
				JButton search = new JButton ("Search");
				JButton browseFile = new JButton("Browse Files");
				JLabel l = new JLabel("Blank");
				JTextArea results = new JTextArea(15,30);
				JScrollPane scrollPane = new JScrollPane(results);		
				JFileChooser j = new JFileChooser();
				j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				


				
				// Browse Files Action Listener
				browseFile.addActionListener(new ActionListener() {
					 
					
					public void actionPerformed(ActionEvent e) {
							int returnVal = j.showOpenDialog(null); // Select File	
							
							//corpus = DirectoryCorpus.loadJsonDirectory(Paths.get("C:\\Users\\potad\\eclipse-workspace\\JSON Files").toAbsolutePath(), ".json");
					}				
				});
				
				//File file = j.getSelectedFile();
				//String fullPath = file.getAbsolutePath();
				//corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(fullPath).toAbsolutePath(), ".json");
				
				
				
				// Search Button Action Listener
				search.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String query = textField.getText();
						query = query.toLowerCase();
						
						int docCount = 0;
						for (Posting p : index.getPostings(query)) {
							results.append("Document " + corpus.getDocument(p.getDocumentId()).getTitle() +"\n");
							results.append("Positions: " + p.getPos() +"\n");
							docCount++;
						}
						results.append("Number of Documents:" + docCount +"\n");
						results.append("\n");
						}					
					
				});
				
				results.setEditable(false);
	
				// Panel Add
				p.add(browseFile);
				p.add(textField);
				p.add(search);
				//p.add(l);
				p.add(scrollPane);
								
				// Frame Add
				frame.add(p);
						
				// Searches when Enter is pressed
				frame.getRootPane().setDefaultButton(search); 
				
				// Size Set
				frame.setSize(400, 450); 
				frame.setResizable(false);
				
				// Closing window stops program
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});

		// GUI End

		List<String> t = index.getVocabulary();
		
		for (String i : t) {
			System.out.println(i);
		}
		

		/*
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
		*/
		
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

	
	
