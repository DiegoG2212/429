package cecs429.query;i

import cecs429.index.Index;
import cecs429.rankings.*;

import java.io.File;

public class RankQuery {

    private int corpusSize;
    private RankFormula rf;

    public RankQuery(int corpusSize) {
        this.rf = new DefaultRank();
        this.corpusSize = corpusSize;
    }

    public RankQuery(DefaultRank d, int corpusSize) {
        rf = d;
        this.corpusSize = corpusSize;
    }

    public RankQuery(tfidfRank t, int corpusSize) {
        rf = t;
        this.corpusSize = corpusSize;
    }

    public RankQuery(OkapiRank o, int corpusSize) {
        rf = o;
        this.corpusSize = corpusSize;
    }

    public RankQuery(WackyRank w, int corpusSize) {
        rf = w;
        this.corpusSize = corpusSize;
    }

    public double getWqt(Index i, String term, int corpusSize) {
        return this.rf.getWqt(i, term, corpusSize);
    }

    public double getWdt() {
        return this.rf.getWdt();
    }

    public double getLd() {
        return this.rf.getLd();
    }

    public void getCorpusSize() {

    }

    public boolean accept(File pathname) {
        String suffix = ".rbc";
        if( pathname.getName().toLowerCase().endsWith(suffix) ) {
            return true;
        }
        return false;
    }
}
