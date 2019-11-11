package cecs429.rankings;

import cecs429.index.Index;

public class DefaultRank implements RankFormula {

    @Override
    public double getWqt(Index i, String term) {
        return Math.log(1+((double) i.getVocabulary().size() / ((double) i.getPostings(term).size())));
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
