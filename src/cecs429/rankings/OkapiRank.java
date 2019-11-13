package cecs429.rankings;

import cecs429.index.Index;

public class OkapiRank implements RankFormula {
    public OkapiRank() {}

    @Override
    public double getWqt() {
        return 0;
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
        return 0;
    }

    @Override
    public double getLd() {
        return 1;
    }
}
