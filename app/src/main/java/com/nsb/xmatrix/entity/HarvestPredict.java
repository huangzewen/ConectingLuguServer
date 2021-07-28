package com.nsb.xmatrix.entity;

public class HarvestPredict {
    private String mLevel;
    private String mRank;
    private double mPrice;

    public HarvestPredict(String level,String rank,double price){
        mLevel=level;
        mRank=rank;
        mPrice=price;
    }

    public String getLevel() {
        return mLevel;
    }

    public void setLevel(String level) {
        this.mLevel = level;
    }

    public String getRank() {
        return mRank;
    }

    public void setRank(String rank) {
        this.mRank = rank;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        this.mPrice = price;
    }
}
