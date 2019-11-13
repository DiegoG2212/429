package cecs429.rankings;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class DefaultRank implements RankFormula {
    public DefaultRank(){

    }

    @Override
    public double getWqt() {
        //return Math.log(1 + (double)(corpusSize / i.getPostings(term).size()));
        return 0;
    }

    @Override
    public double getWdt() {
        return 0;
    }

    @Override
    public double getLd(){

        return 0;
    }
}
