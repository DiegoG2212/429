package cecs429.query;

import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.rankings.*;
import cecs429.text.BetterTokenProcessor;
import cecs429.text.EnglishTokenStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class RankQuery implements QueryComponent{

    private int corpusSize;

    //private List<String> query;
    private Path path;
    String[] query = {};
    RankCalculator rankC = new RankCalculator();
    int formulaSelect = 0;
    DocumentCorpus corpus;
    HashMap<Integer, Integer> tCount = new HashMap<Integer, Integer>(); // <DocID, # of tokens>
    HashMap<Integer, Double> ave = new HashMap<Integer, Double>(); // <DocID, Ave>

    public RankQuery(String[] r, DocumentCorpus c, int formSel, HashMap<Integer,Integer> tokCount, HashMap<Integer,Double> a) {
        corpusSize = c.getCorpusSize();
        formulaSelect = formSel;
        corpus = c;
        tCount = tokCount;
        ave = a;
    }

    /* @Override */
    public List<Posting> getPostings(Index index) {
        List<Posting> result = new ArrayList<Posting>();
        // <DocID, Ad>
        HashMap<Integer, Double> acc = new HashMap<Integer, Double>();

        for(String s: query){
            // Calculate wqt
            int dft = 0;
            for( Posting p: index.getPostings(s) ){ // Calculate dft
                dft ++;
            }
            double wqt = 0;
            if(formulaSelect == 0) { // Default
                wqt = rankC.calculateWqt(new DefaultRank(corpusSize, dft));
            }
            if(formulaSelect == 1) { // tf-idf
                wqt = rankC.calculateWqt(new tfidfRank(corpusSize, dft));
            }
            if(formulaSelect == 2) { // OkapiBM25
                wqt = rankC.calculateWqt(new OkapiRank(corpusSize, dft));
            }
            if(formulaSelect == 3) { // Wacky
                wqt = rankC.calculateWqt(new WackyRank(corpusSize, dft));
            }

            // For each document d in t's posting list
            for (Posting p : index.getPostings(s)) {
                        // Calculate wdt
                        int tftd = p.getPos().size();
                        double wdt = 0;

                        if(formulaSelect == 0) { // Default
                            wdt = rankC.calculateWdt(new DefaultRank(tftd));
                        }
                        if(formulaSelect == 1) { // tf-idf
                            wdt = rankC.calculateWdt(new tfidfRank(tftd));
                        }
                        if(formulaSelect == 2) { // OkapiBM25
                           wdt = rankC.calculateWdt(new OkapiRank(tftd, tCount.get(p.getDocumentId()), tCount, corpusSize));
                        }
                        if(formulaSelect == 3) { // Wacky
                            wdt = rankC.calculateWdt(new WackyRank(tftd, ave.get(p.getDocumentId()) ) );
                        }

                        // If accumulator exists for document
                        if(acc.containsKey(p.getDocumentId()) ){
                            double holdVal = acc.get(p.getDocumentId());
                            holdVal += (wdt * wqt);
                            acc.put(p.getDocumentId(), holdVal);
                        }
                        else{ // No accumulator yet
                            double hold = (wdt * wqt);
                            acc.put(p.getDocumentId(), hold);
                        }
            }
        } // End of Query loop

        // For each non-zero Ad, divide Ad by Ld ===============

        // Sort and return Top 10 ==============================
        // Create Binary Heap Priority Queue
        PriorityQueue<Map.Entry<Integer, Double>> queue
                = new PriorityQueue<>(Comparator.comparing(e -> e.getValue()));

        // Get the top 10
        int k = 10;
        for (Map.Entry<Integer, Double> entry : acc.entrySet()) {
            queue.offer(entry);
            if (queue.size() > k) {
                queue.poll();
            }
        }

        // Scan
        HashMap<Integer, Double> r = new HashMap<Integer,Double>();
        while (queue.size() > 0) {
            Integer h = queue.poll().getKey();
            r.put(h, acc.get(h));
        }
        //System.out.println(r);


        // For the Top 10 results
        for (HashMap.Entry<Integer, Double> entry : r.entrySet()) {
            Posting newP = new Posting(entry.getKey()); // Create new posting with DocID
            result.add(newP);
        }


        return result;
    } // End of getPostings
}
