package cecs429.query;

import cecs429.documents.DocumentCorpus;
import cecs429.text.BetterTokenProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RankedQueryParser {
    /**
     * Given a boolean query, parses and returns a tree of QueryComponents representing the query.
     */
    public QueryComponent parseQuery(String query, DocumentCorpus c, int formulaSelect, HashMap<Integer,Integer> tCount, HashMap<Integer, Double> ave) {
        System.out.println("Inside Ranked Retrieval");

        BetterTokenProcessor proc = new BetterTokenProcessor();
        System.out.println("Splitting string...");
        String[] q = query.split("\\s+");
        List<String> tem = new ArrayList<>();
        List<String> phr = new ArrayList<>();
        int count = 0;
        for(String s: q) {
            System.out.println("String: " +s);
            tem = new BetterTokenProcessor().processToken(s);
            for(String t: tem){
                phr.add(t);
                System.out.println("After stem: "+t);
            }
        }

        return new RankQuery(phr, c, formulaSelect, tCount, ave);
    }

}
