package cecs429.rankings;

import cecs429.index.Index;

public class WackyRank implements RankFormula {
    @Override
    public double getWqt(Index i, String term) {
        return 0;
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
