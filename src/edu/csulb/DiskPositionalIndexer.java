package edu.csulb;

// Class Imports

import cecs429.disk.DiskIndexWriter;
import cecs429.disk.DiskInvertedIndex;
import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.index.PositionalInvertedIndex;
//import cecs429.rankings.*;
import cecs429.query.RankedQueryParser;
import cecs429.rankings.DefaultRank;
import cecs429.rankings.OkapiRank;
import cecs429.rankings.RankCalculator;
import cecs429.rankings.tfidfRank;
import cecs429.text.BetterTokenProcessor;
import cecs429.text.EnglishTokenStream;
import org.tartarus.snowball.ext.englishStemmer;

// General Imports
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// GUI Imports
import javax.swing.JButton;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiskPositionalIndexer {
    String directory = ""; // Sets directory to blank
    // File defStore = new File("src/DefaultDirectory.txt"); // Text file storing
    // Default Directory
    DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(directory).toAbsolutePath(), ".json");
    // DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get(directory).toAbsolutePath(), ".txt");
    String lastQuery = ""; // Saves last user query
    int queryCheck = 0;
    Index index;
    //For selecting the type of ranking query
    int formulaSelect = 0;
    //For selecting between Ranked or Boolean query
    int modeSelect = 0;
    //For showing how long it took to index
    long indexTime = 0;


    // Allows for other functions to append to GUI text box
    JTextArea results = new JTextArea(19, 55);

    //Ranking formula selection
    RankCalculator rankSelect = new RankCalculator();


    public DiskPositionalIndexer() throws Exception {
        query();
    }

    public void query() throws IOException {
        // Load Default Directory (Last selected folder)
        // Disabled for now :(
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
                // Panels ======================================
                JPanel p = new JPanel();
                // Components ========================================
                JTextField textField = new JTextField(35);
                JButton search = new JButton("Enter");
                JButton browseFile = new JButton("Browse Files");
                JLabel l = new JLabel("Blank");

                // JTextPane results = new JTextPane("text/html", "");
                JScrollPane scrollPane = new JScrollPane(results);
                JFileChooser j = new JFileChooser();
                j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // Fonts =====================================================
                Font font = new Font(Font.SANS_SERIF, Font.BOLD, 15);
                Font bold = new Font(Font.SANS_SERIF, Font.BOLD, 15);
                Font inputFont = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
                Font resultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
                // Disable horizontal scrolling on Search Results
                results.setLineWrap(true);
                results.setWrapStyleWord(true);
                // END OF FORMATTING ====================================================================


                // Browse Files Action Listener; :index Special Query
                browseFile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        int returnVal = j.showOpenDialog(null); // Select File
                        File file = j.getSelectedFile();
                        //System.out.println(returnVal);
                        if (returnVal == 0) {
                            String fullPath = file.getAbsolutePath();
                            System.out.println(fullPath);
                            directory = fullPath;    // Update directory

                            /*
                             * // Stores last chosen directory BufferedWriter writer; writer = new
                             * BufferedWriter(new FileWriter(PositionalInvertedIndexer.this.defStore));
                             * writer.write(fullPath); writer.close();
                             */

                            // Update chosen directory
                            try {
                                updateDirectory(fullPath);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            results.setText(""); // Clear previous
                            results.append("" + indexTime + " milliseconds to index the corpus\n");

                            /*
                             * catch (IOException e1) { e1.printStackTrace(); }
                             */
                        } else {
                            System.out.println("Cancelled Directory");
                        }
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

                        } else if (special[0].equals(":doc")) {
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
                            } else {
                                results.append("You must search a query first \n");
                            }
                        }

                        // Special Query for MAP Comparison
                        else if (special[0].equals(":MAP")) {
                            try {
                                MAP(); // Calls MAP function
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        // Special Query for Precision Recall Curve
                        else if (special[0].equals(":PrecRec")) {
                            try {
                                PrecisionRecall(); // Calls MAP function
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        // Special Query for MRT Comparison
                        else if (special[0].equals(":MRT")) {
                            try {
                                MRT(); // Calls MAP function
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
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
                            //System.out.println("HELP");

                            int docCount = 0;
                            if (modeSelect == 0) {
                                System.out.println("Boolean Parse");
                            }
                            QueryComponent q = new BooleanQueryParser().parseQuery(query); // Default

                            if (modeSelect == 1) {
                                System.out.println("Ranked Retrieval Parse");
                                q = new RankedQueryParser().parseQuery(query, corpus, formulaSelect);
                            }


                            /*
                            for(Posting p: index.getPostings(query)){
                                results.append("Document: " +corpus.getDocument(p.getDocumentId())+ "\n");
                            }

                             */


                            for (Posting p : q.getPostings(index)) {

                                results.append("Document: " + corpus.getDocument(p.getDocumentId()).getTitle() + "\n");

                                if (modeSelect == 1) {
                                    results.append("Accumulator: " + p.getAccumulator());
                                }

                                if (modeSelect == 0) {
                                    results.append("Positions: " + p.getPos() + "\n");
                                }
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


                results.setEditable(false); // Doesn't let user edit results box

                // DIALOG BOXES ====================================================
                // Opening Dialog Box to select Mode
                Object[] options = {"Boolean query mode",
                        "Ranked query mode",
                        "Exit"};
                modeSelect = JOptionPane.showOptionDialog(frame,
                        "What mode would you like to use?",
                        "Mode Selection",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]);
                // System.out.println(n);
                if (modeSelect == 0 || modeSelect == 1) {
                } else {
                    // End Program
                    System.exit(-1);
                }

                if (modeSelect == 1) {
                    // Opening Dialog Box to select Mode
                    Object[] options2 = {"Default",    // 0
                            "tf-idf",                // 1
                            "OkapiBM25",            // 2
                            "Wacky"};                // 3
                    formulaSelect = JOptionPane.showOptionDialog(frame,
                            "What formula would you like to use?",
                            "Formula Selection",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options2,
                            options2[3]);
                    // System.out.println(n);
                    if (formulaSelect == 0 || formulaSelect == 1 || formulaSelect == 2 || formulaSelect == 3) {
                    } else {
                        // End Program
                        System.exit(-1);
                    }
                }

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

    //
    public void updateDirectory(String dir) throws IOException { // Updates changes to corpus and index
        corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(dir).toAbsolutePath(), ".json");
        //DiskPositionalIndexer.this.corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath(), ".txt");

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
        DiskIndexWriter writeDisk = new DiskIndexWriter(); // Writes index to disk
        Index tdi;
        if (Files.exists(Paths.get(directory + "/index/vocabTable.bin").toAbsolutePath()) == false) { // If index folder doesn't exist
            System.out.println("Writing Index to Disk...");
            tdi = new PositionalInvertedIndex();

            // Loops through documents
            for (Document d : corpus.getDocuments()) {
                System.out.println("At Document: " + d.getId());
                int x = 0; // Reset counter for positions
                // Creates tokens by splitting on whitespace
                EnglishTokenStream stream = new EnglishTokenStream(d.getContent());
                List<String> token2 = new ArrayList<String>();

                //Hashmap to keep track of the frequency of each term in the doc
                HashMap<String, Integer> terms = new HashMap<String, Integer>();

                // List of Wdts to calculate the Ld of each doc
                List<Double> Wdts = new ArrayList<Double>();

                // Adds term to index along with Document ID
                for (String token : stream.getTokens()) {    // Go through each token
                    // Increment token counter each time
                    // Add processed token
                    tdi.addTerm(processor.processToken(token), d.getId(), x);
                    //System.out.println(processor.processToken(token));
                    // Doc Weight Work ===================================================
                    for (String i : processor.processToken(token)) {
                        // If term exists in HashMap
                        if (terms.containsKey(i)) {
                            int valHold = terms.get(i); // Take value
                            valHold++; // Increment counter by 1
                            terms.put(i, valHold);
                        } else { // Term doesn't currently exist
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
                        /*for (String b : token3) {
                            if (terms.containsKey(b)) {
                                int valHold = terms.get(b); // Take value
                                valHold++; // Increment counter by 1
                                terms.put(b, valHold);
                            } else { // Term doesn't currently exist
                                terms.put(b, 1); // Give count of 1
                            }
                        }*/
                        // ====================================================================
                        token2.remove(0);
                    }
                    x++;
                }


                // Save Average Token Frequency
                double atSum = 0;
                for (double s : terms.values()) {
                    atSum += s;
                }
                double aveCalc = atSum / terms.size();


                writeDisk.addAvgTFs(aveCalc);
                writeDisk.addDocLength(atSum);

                // Calculate docWeightsd

                writeDisk.addDocWeight(rankSelect.calculateLd(new DefaultRank(terms)));

//                if (formulaSelect == 1) { // tf-idf
//					writeDisk.addDocWeight(rankSelect.calculateLd(new tfidfRank(terms)));
//				}
//                if (formulaSelect == 2) { // OkapiBM25
//					writeDisk.addDocWeight(rankSelect.calculateLd(new OkapiRank()));
//				}

                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }    // End of Documents
            //List to get the byte size of each doc
            List<Long> dBytes = getByteSizes();
            for (Long s : dBytes) {
                double byteGet = (double) s;
                writeDisk.addByteSize(byteGet);
            }

            // Write to Disk
            writeDisk.WriteIndex(tdi, Paths.get(directory + "/index").toAbsolutePath());

            // Return Index
            //return tdi;
        } else {
            corpus.getDocuments();
        }


        // Switches to DiskInvertedIndex after building/ already existed
        System.out.println("Index on Disk");
        tdi = new DiskInvertedIndex(Paths.get(directory + "/index").toAbsolutePath());


        // Return Index
        return tdi;
    }

    // Gets byte sizes for all documents
    public List<Long> getByteSizes() {
        List<Long> result = new ArrayList<Long>();

        try (Stream<Path> walk = Files.walk(Paths.get(directory).toAbsolutePath(), 1)) {
            result = walk.filter(f -> f.getFileName().toString().endsWith(".json"))
                    .map(p -> p.toFile().length())
                    .collect(Collectors.toList());

            for (Long l : result) {
                System.out.println();
                System.out.println(l);
            }
        } catch (IOException i) {
            i.printStackTrace();
        }

        return result;
    }

    private double getWdt(int tftd) {
        return 1 + Math.log(tftd);
    }

    private double Ld(List<Double> Wdts) {
        double docWeight = 0;
        for (double d : Wdts) {
            docWeight += Math.pow(d, 2);
        }
        return Math.sqrt(docWeight);
    }


    // Milestone 3 Methods ==========================

    public void MAP() throws IOException { // MAP ================================================
        // Scan for Queries + Query Rels
        BufferedReader br = new BufferedReader(new
                FileReader(Paths.get(directory).toAbsolutePath() + "/relevance/queries"));
        BufferedReader br1 = new BufferedReader(new
                FileReader(Paths.get(directory).toAbsolutePath() + "/relevance/qrel"));


        List<String> queries = new ArrayList<>(); // Store Queries
        List<String> qRel = new ArrayList<String>(); // Store  Queries Rels


        // Create List of Queries
        String query = "";
        while ((query = br.readLine()) != null) {
            queries.add(query);
        }

        // Create List of Query Rels
        String st = "";
        while ((st = br1.readLine()) != null) {
            qRel.add(st);
        }

        List<Double> formulaMAPs = new ArrayList<Double>(); // Store MAP results for each formula

        // Cycle through formulas
        for (int i = 0; i < 4; i++) {
            //System.out.println("Running Formula " +i);
            // List of all query AVG Precisions
            double avgTotal = 0;

            int queryCount = 0;
            for (String s : queries) { // Query Loop ===============================================


                // Uses selected formula on query
                QueryComponent q = new RankedQueryParser().parseQuery(s, corpus, i);

                // Get postings for query
                int precTrack = 0;
                int qRelCounter = 0;
                double precisionStore = 0;
                int docCount = 0;
                for (Posting p : q.getPostings(index)) {
                    docCount++;
                    // Get FileName of Document
                    String fName = corpus.getDocument(p.getDocumentId()).getFileName();

                    // Strip off extension and parse as int to get rid of padding
                    int fID = Integer.parseInt(fName.substring(0, fName.indexOf(".")));

                    // Print ID from Filename
                    System.out.println("Doc Name: " + fID);

                    // Print actual ID (Not used for checking relativity)
                    //System.out.println("ID: " + p.getDocumentId());

                    // Compare found ID with qRel list
                    // Split qRel line to get individual integers
                    String check[] = qRel.get(queryCount).split("\\s+");
                    for (String c : check) { // For every "int" in corresponding qRel line
                        qRelCounter++;
                        int iCheck = Integer.parseInt(c); // Parse Int
                        if (fID == iCheck) { // (+) If Postings ID matches with qRel ID
                            precTrack++;
                            double calc = ((double) (precTrack)) / (double) docCount;

                            // Store Precision
                            precisionStore += calc;

                        }
                    }

                    System.out.println("");
                } // End of Postings Loop

                // Calc Avg Precision for Query
                double avgPrecCalc = (precisionStore / qRelCounter);

                // Add to Total list of Avg Precisions
                avgTotal += avgPrecCalc;

                queryCount++;
            } // End of Query Loop ========================================


            // Calc MAP
            double mapCalc = (avgTotal / queryCount);
            System.out.println("MAP CALC: " + mapCalc);

            // Store Values
            formulaMAPs.add(mapCalc);

        } // End of Formula Loop


        // Print Results
        results.setText("");
        results.append("MAPs ==============================\n");
        results.append("Default MAP: " + formulaMAPs.get(0) + "\n");
        results.append("tf-idf MAP: " + formulaMAPs.get(1) + "\n");
        results.append("OkapiBM25 MAP: " + formulaMAPs.get(2) + "\n");
        results.append("Wacky MAP: " + formulaMAPs.get(3) + "\n");
    }


    public void MRT() throws IOException { // MRT ===================================================
        // Scan for Queries
        BufferedReader br = new BufferedReader(new
                FileReader(Paths.get(directory).toAbsolutePath() + "/relevance/queries"));

        List<String> queries = new ArrayList<>(); // Store Queries

        List<Float> allMRT = new ArrayList<Float>();
        float MRTSum = 0;

        // Create List of Queries
        String query = "";
        while ((query = br.readLine()) != null) {
            queries.add(query);
        }

        // Cycle through formulas
        for (int i = 0; i < 4; i++) {
            //System.out.println("Running Formula " +i);
            int queryCount = 0;
            for (String s : queries) { // Query Loop ===============================================
                long startTime = System.currentTimeMillis(); // Index Timer

                // Uses selected formula on query
                QueryComponent q = new RankedQueryParser().parseQuery(s, corpus, i);

                // Get postings for query using formula
                for (Posting p : q.getPostings(index)) {
                    //System.out.println("Posting: " +corpus.getDocument(p.getDocumentId()).getTitle() );
                } // End of Postings Loop

                // End Timer
                long endTime = System.currentTimeMillis();

                // Calc Total Time for Query
                float totalTime = (endTime - startTime) / 1000F;

                // Add Query Time to Total
                MRTSum += totalTime;

                // Increment Query Counter
                queryCount++;
            } // End of Query Loop ========================================

            float MRTCalc = MRTSum / queryCount; // Calculate Formula MRT
            allMRT.add(MRTCalc); // Add Formula's MRT
        } // End of Formula Loop


        // Print MRT
        results.setText("");
        results.append("Mean Response Times ===============\n");
        results.append("Default MRT:        " + allMRT.get(0) + " seconds\n");
        results.append("tf-idf MRT:            " + allMRT.get(1) + " seconds\n");
        results.append("OkapiBM25 MRT: " + allMRT.get(2) + " seconds\n");
        results.append("Wacky MRT:        " + allMRT.get(3) + " seconds\n");


        // Calc Throughputs
        float dThrough = 1 / allMRT.get(0);
        float tfidfThrough = 1 / allMRT.get(1);
        float OkapiThrough = 1 / allMRT.get(2);
        float WackyThrough = 1 / allMRT.get(3);


        // Print Throughput
        results.append("\n");
        results.append("Throughputs ======================\n");
        results.append("Default Throughput:       " + dThrough + " queries/second\n"); // Queries/ Second
        results.append("tf-idf Throughput:            " + tfidfThrough + " queries/second\n"); // Queries/ Second
        results.append("OkapiBM25 Throughput: " + OkapiThrough + " queries/second\n"); // Queries/ Second
        results.append("Wacky Throughput:         " + WackyThrough + " queries/second\n"); // Queries/ Second
    }


    public void PrecisionRecall() throws IOException { // Precision Recall Curve ================================================
        // Scan for Queries + Query Rels
        BufferedReader br = new BufferedReader(new
                FileReader(Paths.get(directory).toAbsolutePath() + "/relevance/queries"));
        BufferedReader br1 = new BufferedReader(new
                FileReader(Paths.get(directory).toAbsolutePath() + "/relevance/qrel"));


        // Query
        String s = br.readLine();
        System.out.println("Query: " + s);

        // Query Rel
        String st = br1.readLine();
        System.out.println("qRel: " + st);

        // Lists
        List<Double> precision = new ArrayList<Double>();
        List<Double> recall = new ArrayList<Double>();

        // Split qRel line to get individual integers
        String check[] = st.split("\\s+");
        int qRelSize = check.length;

        // Cycle through formulas
        for (int i = 0; i < 4; i++) {
            System.out.println("Running Formula: " + 0 + "=========================================");

            // Uses selected formula on query
            QueryComponent q = new RankedQueryParser().parseQuery(s, corpus, 0);

            // Get postings for query
            int precTrack = 0;
            int docCount = 0;
            for (Posting p : q.getPostings(index)) { // Doc Loop ======================

                docCount++;
                // Get FileName of Document
                String fName = corpus.getDocument(p.getDocumentId()).getFileName();

                // Strip off extension and parse as int to get rid of padding
                int fID = Integer.parseInt(fName.substring(0, fName.indexOf(".")));

                // Print ID from Filename
                System.out.println("Doc Name: " + fID);

                // Print actual ID (Not used for checking relativity)
                //System.out.println("ID: " + p.getDocumentId());


                int hit = 0;
                for (String c : check) { // For every "int" in corresponding qRel line
                    int iCheck = Integer.parseInt(c); // Parse Int
                    System.out.println("Check iCheck: " + iCheck);

                    if (fID == iCheck) { // (+) If Postings ID matches with qRel ID
                        hit = 1;
                        System.out.println("Hit");
                        precTrack++;
                        double pCalc = ((double) (precTrack)) / (double) docCount;

                        // Recall
                        double rCalc = ((double) (precTrack)) / qRelSize;

                        // Store Precision
                        precision.add(pCalc);
                        recall.add(rCalc);
                    }

                }

                if (hit == 0) {

                    System.out.println("Miss");
                    double pCalc = ((double) (precTrack)) / (double) docCount;

                    // Recall
                    double rCalc = ((double) (precTrack)) / qRelSize;

                    // Store Precision
                    precision.add(pCalc);
                    recall.add(rCalc);

                }

                System.out.println("");
            } // End of Postings Loop


//            DataOutputStream DefaultWriter = new DataOutputStream(new FileOutputStream(Paths.get(directory).toAbsolutePath() + "/relevance/DefaultPlot.txt"));
//            DataOutputStream tfidfWriter = new DataOutputStream(new FileOutputStream(Paths.get(directory).toAbsolutePath() + "/relevance/tfidfPlot.txt"));
//            DataOutputStream OkapiWriter = new DataOutputStream(new FileOutputStream(Paths.get(directory).toAbsolutePath() + "/relevance/OkapiPlot.txt"));
//            DataOutputStream WackyWriter = new DataOutputStream(new FileOutputStream(Paths.get(directory).toAbsolutePath() + "/relevance/WackyPlot.txt"));


            // Stores last chosen directory
            BufferedWriter DefaultWriter = new BufferedWriter(new FileWriter(Paths.get(directory).toAbsolutePath() + "/relevance/DefaultPlot.txt"));
            BufferedWriter tfidfWriter = new BufferedWriter(new FileWriter(Paths.get(directory).toAbsolutePath() + "/relevance/tfidfPlot.txt"));
            BufferedWriter OkapiWriter = new BufferedWriter(new FileWriter(Paths.get(directory).toAbsolutePath() + "/relevance/OkapiPlot.txt"));
            BufferedWriter WackyWriter = new BufferedWriter(new FileWriter(Paths.get(directory).toAbsolutePath() + "/relevance/WackyPlot.txt"));

//            writer.write(fullPath);
//            writer.close();
//


            for (int b = 0; b < 4; b++) {
                for (int x = 0; x < docCount; x++) {
                    System.out.print("Precision: " + precision.get(x));
                    System.out.println(", Recall: " + recall.get(x));

                    if (b == 0) {
                        DefaultWriter.write(precision.get(x).toString());
                        DefaultWriter.write(",");
                        DefaultWriter.write(recall.get(x).toString());
                        DefaultWriter.write("\n");

                    }
                    if (b == 1) {
                        tfidfWriter.write(precision.get(x).toString());
                        tfidfWriter.write(",");
                        tfidfWriter.write(recall.get(x).toString());
                        tfidfWriter.write("\n");


                    }
                    if (b == 2) {
                        OkapiWriter.write(precision.get(x).toString());
                        OkapiWriter.write(",");
                        OkapiWriter.write(recall.get(x).toString());
                        OkapiWriter.write("\n");


                    }
                    if (b == 3) {
                        WackyWriter.write(precision.get(x).toString());
                        WackyWriter.write(",");
                        WackyWriter.write(recall.get(x).toString());
                        WackyWriter.write("\n");


                    }
                }
            }

            DefaultWriter.close();
            tfidfWriter.close();
            OkapiWriter.close();
            WackyWriter.close();

            System.out.println("================================================");
        } // End of Formula Loop
    }


    public static void main(String[] args) throws Exception {
        new DiskPositionalIndexer(); // Calls program
    }

}
