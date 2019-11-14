package cecs429.query;

import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.rankings.RankFormula;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RankedQueryParser {
    /**
     * Given a boolean query, parses and returns a tree of QueryComponents representing the query.
     */
    public QueryComponent parseQuery(String query, DocumentCorpus c, int formulaSelect, HashMap<Integer,Integer> tCount, HashMap<Integer, Double> ave) {
        String[] result = query.split("\\s+");

        return new RankQuery(result, c, formulaSelect, tCount, ave);
    }

}
