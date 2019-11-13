package cecs429.rankings;

import cecs429.index.Index;

public class OkapiRank implements RankFormula {
    public OkapiRank() {}

    @Override
    public double getWqt() {
        return 0;
    }

    @Override
    public double getWdt() {
        return 0;
    }

    @Override
    public double getLd() {
        return 1;
    }
}
