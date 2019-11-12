package cecs429.rankings;

import cecs429.index.Index;

import java.io.IOException;
import java.nio.file.Path;

public interface RankFormula {

    double getWqt(Index i, String term, int corpusSize);

    double getWdt(Index i, String term, int docID);

    double getLd(Path path) throws IOException;

}
