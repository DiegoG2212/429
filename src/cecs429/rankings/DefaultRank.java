package cecs429.rankings;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class DefaultRank implements RankFormula {
    public DefaultRank(){}

    @Override
    public double getWqt(Index i, String term, int corpusSize) {
        return Math.log(1 + (double)(corpusSize / i.getPostings(term).size()));
    }

    @Override
    public double getWdt(Index i, String term, int docID) {
        int freq = 0;
        for (Posting p : i.getPostings(term)) {
            if (p.getDocumentId() == docID) {
                freq++;
            }
        }
        return 1 + Math.log(freq);
    }

    @Override
    public double getLd(Path path) throws IOException {
        DataInputStream docWeight = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(path + "/docWeights.bin")));



        docWeight.close();

        return
    }
}
