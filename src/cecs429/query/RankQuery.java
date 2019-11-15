package cecs429.query;

import cecs429.disk.DiskInvertedIndex;
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
    //String[] query = {};
    RankCalculator rankC = new RankCalculator();
    int formulaSelect = 0;
    DocumentCorpus corpus;

    List<String> query = new ArrayList<>();

    public RankQuery(List<String> phr, DocumentCorpus c, int formSel) {
        query = phr;
        corpusSize = c.getCorpusSize();
        formulaSelect = formSel;
        corpus = c;
    }

    /* @Override */
    public List<Posting> getPostings(Index index) {
        // <DocID, Ad>
        HashMap<Integer, Double> acc = new HashMap<Integer, Double>();

        for(String s: query){
            System.out.println("Query part: " +s);
            // Calculate wqt
            int dft = 0;
            for( Posting p: index.getPostings(s) ){ // Calculate dft
                dft ++;
            }
            //System.out.println("DFT Test:" +dft);
            double wqt = 0;
            if(dft == 0){
                //System.out.println("uhoh");
            }
            else{
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
                        wdt = rankC.calculateWdt(new OkapiRank(tftd, index.getDocLengths().get(p.getDocumentId()), index.getDocLengthAvg() ));
                    }
                    if(formulaSelect == 3) { // Wacky
                        wdt = rankC.calculateWdt(new WackyRank(tftd, index.getAvgTFtds().get(p.getDocumentId())  ));
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
            }

        } // End of Query loop

        // For each non-zero Ad, divide Ad by Ld ===============
        List<Double> LdList = index.getLds();
        int i = 0;
        for (HashMap.Entry<Integer, Double> scan : acc.entrySet()) {
            if (scan.getValue() != 0){
                double calc = scan.getValue() / LdList.get(i);
                i++;
                acc.put(scan.getKey(),calc);
            }
        }



        // Sort and return Top 10 =========================================================



//
//        // Create Binary Heap Priority Queue
//        PriorityQueue<Map.Entry<Integer, Double>> queue
//                = new PriorityQueue<>(Comparator.comparing(e -> e.getValue()));
//
//        // Get the top 10
//        int k = 10;
//        for (Map.Entry<Integer, Double> entry : acc.entrySet()) {
//            queue.offer(entry);
//            if (queue.size() > k) {
//                queue.poll();
//            }
//        }
//
//        // Scan
//        HashMap<Integer, Double> r = new HashMap<Integer,Double>();
//        while (queue.size() > 0) {
//            Integer h = queue.poll().getKey();
//            r.put(h, acc.get(h));
//        }
//        //System.out.println(r);
//
//
//        // For the Top 10 results
//        // int count = 1;
//
//        for (HashMap.Entry<Integer, Double> entry : r.entrySet()) {
//            Posting newP = new Posting(entry.getKey(),entry.getValue()); // Create new posting with DocID
//            result.add(newP);
//            //System.out.println("At: " +count);
//            //count++;
//        }




        PriorityQueue<Map.Entry<Integer, Double>> queue
                = new PriorityQueue<>(Comparator.comparing(e -> e.getValue(), Collections.reverseOrder()));

        // Add to Priority Queue
        for (Map.Entry<Integer, Double> entry : acc.entrySet()) {
            System.out.println("Offer check: "+queue.offer(entry));
        }

        List<Posting> result = new ArrayList<Posting>();
        int count = 0;
        int qSize = queue.size();
        while (count < 10 && count < qSize) {
            Posting newP = new Posting(queue.peek().getKey(), queue.peek().getValue());
            result.add(newP);
            queue.remove();
            count++;
        }

        return result;
    } // End of getPostings
}
