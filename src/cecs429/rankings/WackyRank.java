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

    @Override
    public double getWqt() {
        double calc = (corpusSize - docFreq)/(docFreq);
        return Math.max(0, Math.log(calc));
    }

<<<<<<< HEAD

    public double getWdt(int x) {
=======
    @Override
    public double getWdt() {
>>>>>>> f4198b343b068328156a4cf8344a6543400da62a
        return 0;
    }

    public double getWdt(){
        return 0;
    }
    @Override
    public double getLd() {
        return 0;
    }
}
