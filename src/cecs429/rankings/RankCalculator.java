package cecs429.rankings;

public class RankCalculator {

    public double calculateWqt(RankFormula strategy){
        return strategy.getWqt();
    }

    public double calculateWdt(RankFormula strategy){
        return strategy.getWdt();
    }

    public double calculateLd(RankFormula strategy){
        return strategy.getLd();
    }

}