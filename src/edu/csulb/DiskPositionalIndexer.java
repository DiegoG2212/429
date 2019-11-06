package edu.csulb;

// Class Imports
import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.disk.DiskIndexWriter;
import cecs429.index.DiskPositionalIndex;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.index.PositionalInvertedIndex;
import cecs429.text.BetterTokenProcessor;
import cecs429.text.EnglishTokenStream;
import org.tartarus.snowball.ext.englishStemmer;

// General Imports
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// GUI Imports
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class DiskPositionalIndexer {
	String directory = ""; // Sets directory to blank
	// File defStore = new File("src/DefaultDirectory.txt"); // Text file storing
	// Default Directory
	// DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(directory).toAbsolutePath(), ".json");
	DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get(directory).toAbsolutePath(),".txt");
	String lastQuery = ""; // Saves last user query
	int queryCheck = 0;
	Index index;
	int formulaSelect = 0; // Used to select variant tf-idf formula

	long indexTime = 0;

	public DiskPositionalIndexer() throws Exception {
		query();
	}

	public void query() throws Exception {
		// Load Default Directory (Last selected folder)
		// BufferedReader br = new BufferedReader(new
		// FileReader(PositionalInvertedIndexer.this.defStore));
		// Reads text file into String
		// String st;
		// while ((st = br.readLine()) != null) {
		// System.out.println(st);
		// updateDirectory(st);
		// }

		// GUI===========================================================================
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Frame ==================
				JFrame frame = new JFrame("Search Engine");
				// Panels =================
				JPanel p = new JPanel();
				// Components =============
				JTextField textField = new JTextField(35);
				JButton search = new JButton("Enter");
				JButton browseFile = new JButton("Browse Files");
				JLabel l = new JLabel("Blank");
				JTextArea results = new JTextArea(19, 55);
				// JTextPane results = new JTextPane("text/html", "");
				JScrollPane scrollPane = new JScrollPane(results);
				JFileChooser j = new JFileChooser();
				j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				// Fonts ==================
				Font font = new Font(Font.SANS_SERIF, Font.BOLD, 15);
				Font bold = new Font(Font.SANS_SERIF, Font.BOLD, 15);
				Font inputFont = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
				Font resultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 16);

				// Disable horizontal scrolling on Search Results
				results.setLineWrap(true);
				results.setWrapStyleWord(true);

				
				
				
				// Browse Files Action Listener; :index Special Query
				browseFile.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						int returnVal = j.showOpenDialog(null); // Select File
						File file = j.getSelectedFile();
						String fullPath = file.getAbsolutePath();
						System.out.println(fullPath);
						directory = fullPath;	// Update directory

						/*
						 * // Stores last chosen directory BufferedWriter writer; writer = new
						 * BufferedWriter(new FileWriter(PositionalInvertedIndexer.this.defStore));
						 * writer.write(fullPath); writer.close();
						 */

						// Update chosen directory
						try {
							updateDirectory(fullPath);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						results.setText(""); // Clear previous
						results.append("" + indexTime + " milliseconds to index the corpus\n");

						/*
						 * catch (IOException e1) { e1.printStackTrace(); }
						 */
					}
				});
				// Search Button Action Listener
				search.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						englishStemmer stemmer = new englishStemmer();
						String query = textField.getText().toLowerCase(); // Get query, make lower case

						// Gets first word in query; splits string after first whitespace
						String special[] = textField.getText().split(" ", 2);

						String whole[] = textField.getText().split(" "); // Separate on every whitespace

						// :vocab Special Query
						if (textField.getText().equals(":vocab")) {
							results.setText(""); // Clear results
							List<String> t = DiskPositionalIndexer.this.index.getVocabulary();

							int counter = 0;
							for (String i : t) {
								results.append(i + "\n");
								counter++;
								if (counter >= 1000) // Stops after first 1000 terms
									break;
							}
							results.append("End of Vocabulary \n");
						}

						// :stem token Special Query
						else if (special[0].equals(":stem")) {
							try {
								results.setText(""); // Clear results

								results.append(stemThis(special[1]));
							} catch (ArrayIndexOutOfBoundsException exception) {
								results.setText("Correct usage: \":stem <token>\" ");
							}

						}

						else if (special[0].equals(":doc")) {
							if (queryCheck != 0) {
								results.setText(""); // Clear results
								String docSearch = special[1].toLowerCase();
								docSearch = stemThis(docSearch);

								QueryComponent q = new BooleanQueryParser().parseQuery(lastQuery);
								for (Posting p : q.getPostings(index)) {
									String compare = DiskPositionalIndexer.this.corpus
											.getDocument(p.getDocumentId()).getTitle().toLowerCase();

									compare = stemThis(compare);
									if (docSearch.equals(compare)) {
										results.append("\n");
										Reader b = DiskPositionalIndexer.this.corpus.getDocument(p.getDocumentId())
												.getContent();
										String read = "";

										int c = 0;
										try {
											c = b.read();
										} catch (IOException e1) {
											e1.printStackTrace();
										}
										while (c != -1) {
											// Converting to character
											// System.out.print((char)c);
											read += Character.toString((char) c);
											try {
												c = b.read();
											} catch (IOException e1) {
												e1.printStackTrace();
											}
										}
										results.append(read + "\n");
									}
								}

								/*
								 * for (Posting p : PositionalInvertedIndexer.this.index.getPostings(lastQuery))
								 * { String compare =
								 * PositionalInvertedIndexer.this.corpus.getDocument(p.getDocumentId()).getTitle
								 * ().toLowerCase();
								 * 
								 * compare = stemThis(compare); if(docSearch.equals(compare)) {
								 * results.append("\n"); Reader b =
								 * PositionalInvertedIndexer.this.corpus.getDocument(p.getDocumentId()).
								 * getContent(); String read = "";
								 * 
								 * int c = 0; try { c = b.read(); } catch (IOException e1) {
								 * e1.printStackTrace(); } while (c != -1){ //Converting to character
								 * //System.out.print((char)c); read += Character.toString((char)c); try { c =
								 * b.read(); } catch (IOException e1) { e1.printStackTrace(); } }
								 * results.append(read +"\n"); } }
								 */
							} else {
								results.append("You must search a query first \n");
							}
						}

						else {
							results.setText("");
							String combine = "";
							int counter = 0;
							// Stemming
							// System.out.println(whole[1]);
							for (String s : whole) {
								if (counter == 0) { // First word
									combine += stemThis(s);
									counter++;
								} else {
									combine += " ";
									combine += stemThis(s);
								}
							}
							System.out.println(combine);

							int docCount = 0;
							// System.out.println("im here");
							QueryComponent q = new BooleanQueryParser().parseQuery(query);

							for (Posting p : q.getPostings(index)) {
								// for (Posting p : PositionalInvertedIndexer.this.index.getPostings(query)) {
								// System.out.println("inside q postings");
								results.append("Document: " + DiskPositionalIndexer.this.corpus
										.getDocument(p.getDocumentId()).getTitle() + "\n");
								results.append("Positions: " + p.getPos() + "\n");
								results.append("\n");
								docCount++;
							}
							results.append("Number of Documents:" + docCount + "\n");
							results.append("\n");
							results.append("If you would like to view a document, type :doc <name> \n");
							results.append("Otherwise, type another search query \n");
							lastQuery = combine; // Stores last query for :doc usage
							queryCheck = 1; // Checks that query was searched allowing for :doc usage
						}
					}

				});

				
				
				

				
				// Opening Dialog Box to select Mode ===============================================================
				Object[] options = {"Boolean query mode",
				                    "Ranked query mode",
				                    "None"};
				int n = JOptionPane.showOptionDialog(frame,
				    "What mode would you like to use?",
				    "Mode Selection",
				    JOptionPane.YES_NO_CANCEL_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[2]);
				// System.out.println(n);
				if(n == 0) {	// 0 Boolean query mode
					
				}
				else if(n == 1) {	// 1 Ranked query mode
					
				}
				else {	// -1 or 2 Exit
					// End Program
					System.exit(-1);
				}
				// =========================================
				
				// Dialog Box to select variant tf-idf formula ======================================================
				Object[] formula = {"Default",
	                    "tf-idf",
	                    "Okapi BM25",
	                    "Wacky"};
				formulaSelect = JOptionPane.showOptionDialog(frame,
				    "What variant tf-idf formula would you like to use?",
				    "Mode Selection",
				    JOptionPane.YES_NO_CANCEL_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    formula,
				    formula[2]);
				//System.out.println(w);
				if(formulaSelect == 0) {	// 0 Default
					System.out.println("Default Formula selected");
				}
				else if(formulaSelect == 1) {	// 1 tf-idf
					System.out.println("tf-idf Formula selected");
				}
				else if(formulaSelect == 2) {	// 2 Okapi BM25
					System.out.println("Okapi BM25 Formula selected");
				}
				else if(formulaSelect == 3) {	// 3 Wacky
					System.out.println("Wacky Formula selected");
				}
				else {	// -1 Exit
					// End Program
					System.exit(-1);
				}

				results.setEditable(false); // Doesn't let user edit results box	
				// Panel Add
				p.add(browseFile);
				p.add(textField);
				p.add(search);
				// p.add(l);
				p.add(scrollPane);
				// Frame Add
				frame.add(p);
				// Searches when Enter is pressed
				frame.getRootPane().setDefaultButton(search);
				// Size Set
				frame.setSize(750, 520);
				j.setPreferredSize(new Dimension(800, 600));
				// Font Set
				browseFile.setFont(font);
				search.setFont(font);
				textField.setFont(inputFont);
				results.setFont(resultFont);
				frame.setResizable(false); // No window resizing
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Closing window stops program; :q Special Query
				frame.setVisible(true); // Visible
			}
		});
		// GUI
		// End===================================================================================
	}

	public void updateDirectory(String dir) throws IOException { // Updates changes to corpus and index
		// PositionalInvertedIndexer.this.corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(dir).toAbsolutePath(),".json");
		DiskPositionalIndexer.this.corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath(), ".txt");

		long startTime = System.nanoTime(); // Index Timer
		DiskPositionalIndexer.this.index = indexCorpus(DiskPositionalIndexer.this.corpus);
		long endTime = System.nanoTime();

		DiskPositionalIndexer.this.indexTime = (endTime - startTime) / 1000000;
		System.out.println(indexTime + " milliseconds");
	}

	public String stemThis(String x) { // Updates changes to corpus and index
		englishStemmer stemmer = new englishStemmer();

		// Stem Query
		stemmer.setCurrent(x);
		stemmer.stem();
		return stemmer.getCurrent();
	}

	private Index indexCorpus(DocumentCorpus corpus) throws IOException {
		BetterTokenProcessor processor = new BetterTokenProcessor();
		Index tdi = new PositionalInvertedIndex();
		DiskIndexWriter writeDisk = new DiskIndexWriter(); // Writes index to disk

		// Loops through documents
		for (Document d : corpus.getDocuments()) {
			int x = 0; // Reset counter for positions
			// Creates tokens by splitting on whitespace
			EnglishTokenStream stream = new EnglishTokenStream(d.getContent());
			List<String> token2 = new ArrayList<String>();
			HashMap<String, Integer> terms = new HashMap<String, Integer>();
			
			
			int termExist = 0;
			// Adds term to index along with Document ID
			for (String token : stream.getTokens()) {	// Go through each token
				// Add processed token
				tdi.addTerm(processor.processToken(token), d.getId(), x);
				//System.out.println(processor.processToken(token));
				
				
				// Doc Weight Work ===================================================
				
				for(String i: processor.processToken(token)) {
					// If term exists in HashMap
				    if(terms.containsKey(i)) {
				    	int valHold = terms.get(i); // Take value
				    	valHold++; // Increment counter by 1
				    	terms.put(i, valHold);
				    }
					
				    else{ // Term doesn't currently exist
				    	terms.put(i, 1); // Give count of 1		
				    }
				}
			    // ====================================================================
				
				
			    
				for (String i : processor.processToken(token)) {
					token2.add(i);
				}
				while (token2.size() >= 2) {
					String temp = token2.get(0) + " " + token2.get(1);
					List<String> token3 = new ArrayList<String>();
					token3.add(temp);
					tdi.addTerm(token3, d.getId(), x);
					// Doc Weight Work ===================================================
					for(String b: token3) {	
					    if(terms.containsKey(b)) {
					    	int valHold = terms.get(b); // Take value
					    	valHold++; // Increment counter by 1
					    	terms.put(b, valHold);
					    }
						
					    else{ // Term doesn't currently exist
					    	terms.put(b, 1); // Give count of 1		
					    }
					}
				   
				    // ====================================================================
				    token2.remove(0);
				}
				
				x++;
			}


			//writeDisk.addDocWeight(terms); // Add HashMap to list
			if(formulaSelect == 0) { // Using Default Formula
				double wSum = 0;
				for (HashMap.Entry<String, Integer> entry : terms.entrySet()) { // Go through HashMap
					System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
					wSum += Math.pow((1 + Math.log(entry.getValue())), 2); // w d,t
					//System.out.println(wSum);
				}
				double Ld = Math.sqrt(wSum); // Square root of sum of squared w d,t
				System.out.println("Document Weight: " + Ld);
				writeDisk.addDocWeight(Ld); // Store doc weight
				// ==========================================================================
			}
			if(formulaSelect == 1){	// Using tf-idf Formula
				System.out.println("Uh oh, stinky");
				

			}
			if(formulaSelect == 2){ // Okapi BM25
				System.out.println("Uh oh, stinky");
			}
			if(formulaSelect == 3){ // Wacky
				System.out.println("Uh oh, wacky");
			}



			try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	// End of Document
		
		
		// Write to Disk
		writeDisk.WriteIndex(tdi, Paths.get(directory +"/index").toAbsolutePath());
		
		// Return Index
		return tdi;
	}

	public static void main(String[] args) throws Exception {
		new DiskPositionalIndexer(); // Calls program
	}

}
