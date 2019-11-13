//package cecs429.query;
//
//import cecs429.index.Index;
//import cecs429.index.Posting;
//import cecs429.rankings.RankFormula;
//
//import java.util.Collections;
//import java.util.List;
//
//public class RankedQueryParser {
//    /**
//     * Given a boolean query, parses and returns a tree of QueryComponents representing the query.
//     */
//    public QueryComponent parseQuery(String query) {
//        String[] result = query.split("\\s+");
//        List<String> terms = Collections.emptyList();
//        for (int i = 0; i < result.length; i++) {
//            terms.add(result[i]);
//        }
//
//        //List<Double> top10 = new RankQuery(rf, terms, corpusSize, path).getPostings(index);
//
//        System.out.println("Top 10 docs: ");
//        /*
//        for (double d : top10) {
//            System.out.println(d);
//        }
//
//         */
//    }
//
//}
