package cecs429.rankings;

import cecs429.index.Index;

public class WackyRank implements RankFormula {
    public WackyRank(){

    }
    int corpusSize = 0;
    int docFreq = 0;
    public WackyRank(int c, int d){
        corpusSize = c;
        docFreq = d;
    }

    int tftd = 0;
    double ave = 0;
    public WackyRank(int t, double a){
        tftd = t;
        ave = a;
    }


    @Override
    public double getWqt() {
        double calc = (corpusSize - docFreq)/(docFreq);
        return Math.max(0, Math.log(calc));
    }

    @Override
    public double getWdt() {
        double calc = (1 + Math.log(tftd))/(1 + Math.log(ave));
        return calc;
    }

    @Override
    public double getLd() {
        return 0;
    }
}
