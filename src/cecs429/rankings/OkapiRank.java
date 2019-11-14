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
    int tokenCount = 0;
    HashMap<Integer, Integer> tokenAll = new HashMap<Integer, Integer>();
    public OkapiRank(int t, int tC, HashMap<Integer,Integer> tAll, int c){
        corpusSize = c;
        tftd = t;
        tokenCount = tC;
        tokenAll = tAll;
    }

    @Override
    public double getWqt() {
        double calc = (corpusSize - documentFreq + 0.5)/(documentFreq + 0.5);
        return Math.max(0.1, Math.log(calc));
    }

<<<<<<< HEAD

    public double getWdt(int x) {
=======
    @Override
    public double getWdt() {
>>>>>>> f4198b343b068328156a4cf8344a6543400da62a
        return 0;
    }

    @Override
    public double getWdt() {
        double docLenA = 0;
        for(double s: tokenAll.values()){ // Add all doc token counts
            docLenA += s;
        }
        docLenA = docLenA/ tokenAll.size();

        double calc = (2.2 * tftd) / ( 1.2 * ( 0.25 + 0.75 * (tokenCount/ docLenA) + tftd) );
        return calc;
    }

    @Override
    public double getLd() {
        return 1;
    }
}
