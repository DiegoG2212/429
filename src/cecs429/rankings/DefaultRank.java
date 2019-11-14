package cecs429.rankings;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class DefaultRank implements RankFormula {
    HashMap<String,Integer> terms = new HashMap<String,Integer>();
    int tftd = 0;

    public DefaultRank(HashMap<String,Integer> x){
        terms = x;
    }

    public DefaultRank(int t){
        tftd = t;
    }

    int corpusSize = 0;
    int documentFreq = 0;
    public DefaultRank(int corpSize, int docFreq){
        corpusSize = corpSize;
        documentFreq = docFreq;
    }

    @Override
    public double getWqt() {
        //return Math.log(1 + (double)(corpusSize / i.getPostings(term).size()));
        double calc1 = corpusSize/ documentFreq;
        return Math.log(1 + calc1);
    }

    @Override
    public double getWdt() {
        return (1 + Math.log(tftd)) ;
    }

    @Override
    public double getLd(){
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
