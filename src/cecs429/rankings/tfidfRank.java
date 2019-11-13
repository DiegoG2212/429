package cecs429.rankings;

import cecs429.index.Index;

import java.util.HashMap;

public class tfidfRank implements RankFormula {
    HashMap<String,Integer> terms = new HashMap<String,Integer>();
    int tftd = 0;

    public tfidfRank(HashMap<String,Integer> x){
       terms = x;
    }
    @Override
    public double getWqt() {

        return 0;
    }

    @Override
    public double getWdt() {
        return tftd;
    }

    @Override
    public double getWdt() {
        return 0;
    }

    @Override
    public double getLd() {
        double wSum = 0;
        for (HashMap.Entry<String, Integer> entry : terms.entrySet()) { // Go through HashMap
            //System.out.println("Key: "+entry.getKey() +", Value: "+entry.getValue());
            tftd = entry.getValue();
            wSum += Math.pow( getWdt() , 2 );
            //System.out.println(wSum);
        }
        //double Ld = Math.sqrt(wSum);
        //System.out.println("Document Weight: " +Ld);
        return Math.sqrt(wSum);
    }
}
