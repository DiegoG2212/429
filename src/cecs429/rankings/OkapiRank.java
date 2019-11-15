package cecs429.rankings;

import cecs429.index.Index;

import java.util.HashMap;

public class OkapiRank implements RankFormula {
    public OkapiRank() {}

    int corpusSize = 0;
    int documentFreq;
    public OkapiRank(int cSize, int dft){
        corpusSize = cSize;
        documentFreq = dft;
    }

    int tftd = 0;
    double docLengthd = 0;
    double docLengthA = 0;
    //HashMap<Integer, Integer> tokenAll = new HashMap<Integer, Integer>();
    public OkapiRank(int t, double dld, double dla){
        tftd = t;
        docLengthd = dld;
        docLengthA = dla;
    }


    @Override
    public double getWqt() {
        double calc = (corpusSize - documentFreq + 0.5)/(documentFreq + 0.5);
        return Math.max(0.1, Math.log(calc));
    }

    @Override
    public double getWdt() {
        /*
        double docLenA = 0;
        for(double s: tokenAll.values()){ // Add all doc token counts
            docLenA += s;
        }
        docLenA = docLenA/ tokenAll.size();
         */

        return (2.2 * tftd) / ( 1.2 * ( 0.25 + 0.75 * (docLengthd/ docLengthA) + tftd) );
    }

    @Override
    public double getLd() {
        return 1;
    }
}
