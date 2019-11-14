package cecs429.rankings;

import cecs429.index.Index;

import java.io.IOException;
import java.nio.file.Path;

public interface RankFormula {

    double getWqt();

    double getWdt();

    double getLd();

}