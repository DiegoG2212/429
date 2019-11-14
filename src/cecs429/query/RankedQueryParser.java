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

        //List<Double> top10 = new RankQuery(rf, terms, corpusSize, path).getPostings(index);

        //System.out.println("Top 10 docs: ");
        /*
        for (double d : top10) {
            System.out.println(d);
        }

         */
        return new RankQuery(result, c, formulaSelect, tCount, ave);
    }

}
