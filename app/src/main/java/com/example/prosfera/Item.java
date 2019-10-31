package com.example.prosfera;

public class Item {

    private int itemID, currentQty, price, threshold, calculatedPerc;
    private String name, description;
    //private String picture; //not sure how an image will be stored
    //visible (flag)
    //featured/promte (flag)

    public Item(int itID, int currQty, String nm, String desc, int prce,
                int thresh) {
        this.itemID      = itID;
        this.currentQty  = currQty;
        this.name        = nm;
        this.description = desc;
        this.price       = prce;
        this.threshold   = thresh;
        updatePercentage(); //constructor doesn't really need to update the percentage
    }

    public int getItemID()
    {
        return itemID;
    }

    public int getCurrentQty()
    {
        return currentQty;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getPrice()
    {
        return Integer.toString(price);
    }

    public int getThreshold()
    {
        return threshold;
    }

    public int getCalculatedPerc()
    {
        return calculatedPerc;
    }

    public void setItemID(int iID)
    {
        itemID = iID;
    }

    public void setCurrentQTY(int cQty)
    {
        currentQty = cQty;
    }

    public void setName(String n)
    {
        name = n;
    }

    public void setDescription(String d)
    {
        description = d;
    }

    public void setPrice(int p)
    {
        price = p;
    }

    public void setThreshold(int t)
    {
        threshold = t;
    }

    public void updatePercentage() { this.calculatedPerc = this.currentQty/this.threshold; }
}
