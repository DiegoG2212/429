package edu.csulb;

// Class Imports
import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.index.InvertedIndex;
import cecs429.index.PositionalInvertedIndex;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.BetterTokenProcessor;
import cecs429.text.EnglishTokenStream;
import org.tartarus.snowball.ext.englishStemmer;

// General Imports
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

// GUI Imports
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PositionalInvertedIndexer {
	String directory = ""; // Sets directory to blank
	File defStore = new File("src/DefaultDirectory.txt"); // Text file storing Default Directory
	DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(directory).toAbsolutePath(), ".json");
	//DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get("C:\\Users\\potad\\eclipse-workspace\\Text Files").toAbsolutePath(), ".txt");
	Index index = indexCorpus(corpus);
	
	public PositionalInvertedIndexer() throws Exception {
		query();
	}
	
	public void query() throws Exception {
		// Load Default Directory (Last selected folder)
		  BufferedReader br = new BufferedReader(new FileReader(PositionalInvertedIndexer.this.defStore));	  
		  // Reads text file into String
		  String st;
		  while((st = br.readLine()) != null) {
			  System.out.println(st);
			  updateDirectory(st);
		  }
		  	/*
			List<String> t = PositionalInvertedIndexer.this.index.getVocabulary();
			
			for (String i : t) {
				System.out.println(i);
			}
			*/
		  
		// GUI===========================================================================
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {			
				// Frame
				JFrame frame = new JFrame("Search Engine");
				//frame.setTitle("woah");
				// Panels
				JPanel p = new JPanel();
				// Components
				JTextField textField = new JTextField(35);
				JButton search = new JButton ("Enter");
				JButton browseFile = new JButton("Browse Files");
				JLabel l = new JLabel("Blank");
				JTextArea results = new JTextArea(19,55);
				//JTextPane results = new JTextPane("text/html", "");
				JScrollPane scrollPane = new JScrollPane(results);		
				JFileChooser j = new JFileChooser();
				j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				
				// Fonts
				Font font = new Font(Font.SANS_SERIF, Font.BOLD, 15);
				Font bold = new Font(Font.SANS_SERIF, Font.BOLD, 15);
				Font inputFont = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
				Font resultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 17);
				
				// Browse Files Action Listener; :index Special Query
				browseFile.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

							try {
								int returnVal = j.showOpenDialog(null); // Select File
								File file = j.getSelectedFile();
								String fullPath = file.getAbsolutePath();
								System.out.println(fullPath);
							    BufferedWriter writer;
								writer = new BufferedWriter(new FileWriter(PositionalInvertedIndexer.this.defStore));
							    writer.write(fullPath);
							    writer.close();
							    updateDirectory(fullPath);
							
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}			
					}				
				});		
		
				// Search Button Action Listener
				search.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						englishStemmer stemmer = new englishStemmer();
						String query = textField.getText().toLowerCase();	
						
						// Stem Queries
						stemmer.setCurrent(query);
						stemmer.stem();
						query = stemmer.getCurrent();
						System.out.println(query);
						
						results.setText(""); // Clear results
						
						// Gets first word in query
						String special[] = textField.getText().split(" ", 2);
						
						// :vocab Special Query
						if(textField.getText().equals(":vocab")) {
							List<String> t = PositionalInvertedIndexer.this.index.getVocabulary();
				
							int counter = 0;
							for (String i : t) {
								results.append(i +"\n");
								counter++;
								if(counter >= 1000) //Stops after first 1000 terms
									break;
							}
							results.append("End of Vocabulary \n");	
						}
						
						// :stem token Special Query
						else if(special[0].equals(":stem")){
							try {
									stemmer.setCurrent(special[1]);
									if(stemmer.stem()) {
										results.append(stemmer.getCurrent() + "\n");
									}
							}
							catch(ArrayIndexOutOfBoundsException exception) {
								results.setText("Correct usage: \":stem <token>\" ");
							}
							
						}
						
						
						else {
							int docCount = 0;
							System.out.println("im here");
							QueryComponent q = new BooleanQueryParser().parseQuery(query);
							for (Posting p : q.getPostings(index)) {
								System.out.println("inside q postings");
								results.append("Document: " + PositionalInvertedIndexer.this.corpus.getDocument(p.getDocumentId()).getTitle() +"\n");
								results.append("Positions: " + p.getPos() +"\n");
								docCount++;
							}
							results.append("Number of Documents:" + docCount +"\n");
							results.append( "\n");
						}	
						
						
						
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
				frame.setSize(750, 520);
				j.setPreferredSize(new Dimension(800,600));
				
				
				// Font Set
				browseFile.setFont(font);
				search.setFont(font);
				textField.setFont(inputFont);
				results.setFont(resultFont);
				
				frame.setResizable(false);	// No window resizing
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closing window stops program; :q Special Query
				frame.setVisible(true); // Visible
			}
		});
		// GUI End===================================================================================	
	}
	
	public void updateDirectory(String dir) { //Updates changes to corpus and index
		PositionalInvertedIndexer.this.corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(dir).toAbsolutePath(), ".json");
		//PositionalInvertedIndexer.this.corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath(), ".txt");
		PositionalInvertedIndexer.this.index = indexCorpus(PositionalInvertedIndexer.this.corpus);
	}	
	
	private Index indexCorpus(DocumentCorpus corpus) {
		BetterTokenProcessor processor = new BetterTokenProcessor();
		PositionalInvertedIndex tdi = new PositionalInvertedIndex();
	
		// Loops through documents
		for (Document d : corpus.getDocuments()) {
			int x = 0; //Reset counter for positions
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
	
	
	public static void main(String[] args) throws Exception {
		new PositionalInvertedIndexer(); // Calls program
	}
	
}

	
	
