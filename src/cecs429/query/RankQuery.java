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
            System.out.println("DFT Count:" +dft);
            double wqt = 0;
            if(dft == 0){
                System.out.println("No Postings for: "+s);
            }
            else{
                System.out.println("Calculating wqt for: " +s);
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

                System.out.println("Wqt Check: "+wqt);

                // For each document d in t's posting list
                for (Posting p : index.getPositionalPostings(s)) {
                    System.out.println("For postings: " +s);
                    // Calculate wdt
                    int tftd = p.getPos().size();
                    System.out.println("tftd for Wdt Calc Check: "+tftd);
                    double wdt = 0;

                    System.out.println("Calculating wdt for: " +s);
                    if(formulaSelect == 0) { // Default
                        wdt = rankC.calculateWdt(new DefaultRank(tftd));
                    }
                    if(formulaSelect == 1) { // tf-idf
                        wdt = rankC.calculateWdt(new tfidfRank(tftd));
                    }
                    if(formulaSelect == 2) { // OkapiBM25
                        wdt = rankC.calculateWdt(new OkapiRank(tftd, index.getDocLengths(p.getDocumentId()), index.getDocLengthAvg() ));
                    }
                    if(formulaSelect == 3) { // Wacky
                        wdt = rankC.calculateWdt(new WackyRank(tftd, index.getAvgTFtds(p.getDocumentId())));
                    }
                    System.out.println("Wdt Check: "+wdt);

                    // If accumulator exists for document
                    if(acc.containsKey(p.getDocumentId()) ){
                        System.out.println("Accumulator exists for: " +p.getDocumentId());
                        double holdVal = acc.get(p.getDocumentId());
                        holdVal += (wdt * wqt);
                        acc.put(p.getDocumentId(), holdVal);
                    }
                    else{ // No accumulator yet
                        System.out.println("No Accumulator yet for: " +p.getDocumentId());
                        double hold = (wdt * wqt);
                        acc.put(p.getDocumentId(), hold);
                    }
                }
            }

        } // End of Query loop

        // For each non-zero Ad, divide Ad by Ld ===============
        System.out.println("For each non-zero Ad, divided by Ld");
        List<Double> LdList = index.getLds();
        int i = 0;
        for (HashMap.Entry<Integer, Double> scan : acc.entrySet()) {
            if (scan.getValue() != 0){
                double calc = 0;
                if(formulaSelect == 0 || formulaSelect == 1) { // Default / tf-idf
                    calc = scan.getValue() / LdList.get(scan.getKey());
                }
                if(formulaSelect == 2){ // OkapiBM25
                    calc = scan.getValue();
                }
                if(formulaSelect == 3){ // Wacky
                    double bCalc = scan.getValue()/ rankC.calculateLd(new WackyRank( index.getByteSizes(scan.getKey()) ));
                    calc = bCalc;
                }

                i++;
                acc.put(scan.getKey(),calc);
            }
        }

        System.out.println("Finished dividing Ld ...");

        // Sort and return Top 10 =========================================================

        System.out.println("Sorting and returning Top 10 ...");
        PriorityQueue<Map.Entry<Integer, Double>> queue
                = new PriorityQueue<>(Comparator.comparing(e -> e.getValue(), Collections.reverseOrder()));

        // Add to Priority Queue
        for (Map.Entry<Integer, Double> entry : acc.entrySet()) {
            //System.out.println("Offer check: "+queue.offer(entry));
            queue.offer(entry);
        }

        List<Posting> result = new ArrayList<Posting>();
        int count = 0;
        int qSize = queue.size();
        while (count < 50 && count < qSize) {
            Posting newP = new Posting(queue.peek().getKey(), queue.peek().getValue());
            result.add(newP);
            queue.remove();
            count++;
        }
        System.out.println("Finished sorting and returning Top 50...");
        return result;
    } // End of getPostings
}
