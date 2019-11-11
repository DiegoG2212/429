package cecs429.rankings;

import cecs429.index.Index;

public class DefaultRank implements RankFormula {

    @Override
    public double getWqt(Index i, String term) {
        return (double)Math.log(1+(i.getVocabulary().size() / i.getPostings(term)));
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
