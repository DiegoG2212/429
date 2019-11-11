package cecs429.rankings;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.HashSet;
import java.util.Set;

public class DefaultRank implements RankFormula {

    @Override
    public double getWqt(Index i, String term, int corpusSize) {
        Set<Integer> docs = new HashSet<Integer>();
        for (Posting p : i.getPostings(term)) {
            docs.add(p.getDocumentId());
        }
        return (double)Math.log(1+(corpusSize / docs.size()));
    }

    @Override
    public double getWdt(Index i, String term, int docID) {
        return 0;
    }

    @Override
    public double getLd(int docID) {
        return 0;
    }
}
