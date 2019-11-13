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
    private RankFormula rf;
    //private List<String> query;
    private Path path;
    String[] query = {};
    RankCalculator rankC;
    int formulaSelect = 0;
    DocumentCorpus corpus;

    public RankQuery(String[] r, DocumentCorpus c, int formSel) {
        corpusSize = c.getCorpusSize();
        formulaSelect = formSel;
        corpus = c;
    }

    /* @Override */
    public List<Posting> getPostings(Index index) {
        List<Posting> result = Collections.emptyList();
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
                wqt = rankC.calculateWqt(new tfidfRank());
            }
            if(formulaSelect == 2) { // OkapiBM25
                wqt = rankC.calculateWqt(new OkapiRank());
            }
            if(formulaSelect == 3) { // Wacky
                wqt = rankC.calculateWqt(new WackyRank());
            }

            // For each document d in t's posting list
            for (Posting p : index.getPostings(s)) {
                        // Calculate wdt (Default)
                        int tftd = p.getPos().size();
                        double wdt = 1 + Math.log(tftd);

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
        // Divide Ad by Ld
        /*
        for (HashMap.Entry<Integer, Double> entry : acc.entrySet()) { // Go through HashMap
            double newDiv = entry.getValue() / Ld;
            acc.put(entry.getKey(), newDiv);
        }
         */

        // Return Top 10
        /*
        List<Map.Entry<Integer, Double> > list =
                new LinkedList<Map.Entry<Integer, Double> >(acc.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Integer, Double> >() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        HashMap<Integer, Double> temp = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
         */



        return null;
    } // End of getPostings
}
