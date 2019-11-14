package cecs429.query;

import cecs429.documents.DocumentCorpus;

import java.util.HashMap;

public class RankedQueryParser {
    /**
     * Given a boolean query, parses and returns a tree of QueryComponents representing the query.
     */
    public QueryComponent parseQuery(String query, DocumentCorpus c, int formulaSelect, HashMap<Integer,Integer> tCount, HashMap<Integer, Double> ave) {
        System.out.println("Inside Ranked Retrieval");

        System.out.println("Splitting string...");
        String[] q = query.split("\\s+");
        for(String s: q) {
            System.out.println("String: " +s);
        }

        return new RankQuery(q, c, formulaSelect, tCount, ave);
    }

}
