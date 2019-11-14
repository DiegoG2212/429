package cecs429.query;

import cecs429.documents.DocumentCorpus;

import java.util.HashMap;

public class RankedQueryParser {
    /**
     * Given a boolean query, parses and returns a tree of QueryComponents representing the query.
     */
    public QueryComponent parseQuery(String query, DocumentCorpus c,
    int formulaSelect, HashMap<Integer,Integer> tFrequency,
    HashMap<Integer, Double> ave) {
        String[] result = query.split("\\s+");

        return new RankQuery(result, c, formulaSelect, tFrequency, ave);
    }

}
