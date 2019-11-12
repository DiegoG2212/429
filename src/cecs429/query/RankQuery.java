package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.rankings.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class RankQuery /*implements QueryComponent*/ {

    private int corpusSize;
    private RankFormula rf;
    private List<String> query;
    private Path path;

    public RankQuery(RankFormula rf, List<String> query, int corpusSize, Path path) {
        this.rf = rf;
        this.query = query;
        this.corpusSize = corpusSize;
        this.path = path;
    }

    /* @Override */
    public List<Double> getPostings(Index index) {

        List<Double> Ads = Collections.emptyList();

        for (String s : query) {
            double Wqt = rf.getWqt(index, s, corpusSize);

            List<Integer> doc = Collections.emptyList();
            for (Posting p : index.getPostings(s)) {
                doc.add(p.getDocumentId());
            }

            for (Posting p : index.getPostings(s)) {
                for (int i : doc) {
                    // Accumulator
                    double Ad = 0;
                    // Calculate Wdt
                    double Wdt = rf.getWdt(index, s, i);
                    // Increeasing Ad by Wqt * Wdt
                    Ad += Wqt * Wdt;

                    // If non-zero, divide by Ld
                    if (Ad != 0) {
                        try {
                            Ad /= rf.getLd(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Ads.add(Ad);
                }
            }
//            // Accumulator
//            double Ad = 0;
//            for (Posting p : index.getPostings(s)) {
//                // Calculating Wdt
//                double Wdt = rf.getWdt(index, s, p.getDocumentId());
//                // Increasing accumulator
//                Ad += Wqt * Wdt;
//            }
//            if (Ad != 0) {
//                try {
//                    Ad /= rf.getLd(path);
//                    Ads.add(Ad);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        return Ads;
    }
}
