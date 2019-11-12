package cecs429.rankings;

import cecs429.index.Index;

public interface RankFormula {

    double getWqt(Index i, String term, int corpusSize);

    double getWdt(Index i, String term, int docID);

    double getLd(int docID);

}
