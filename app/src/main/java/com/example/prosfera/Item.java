package com.example.prosfera;
import java.io.Serializable;

public class Item implements Serializable{

    private int itemID, threshold, calculatedPerc;
    //price is the unit price per unit, while total_price tracks price*qty
    private int price, totalPrice;
    //currentQty refers to session quantities, while charityQty refers to the total amount raised by the charity
    private int  currentQty, charityQty;
    private String name, description, imageURL;

    //tempPercent is used to track temporary progress changes (items in basket that have NOT been
    //checked out. Once user checks out their cart, calculatedPercent = tempPercent
    private int tempProgress;

    //visible (flag)
    //featured/promoted (flag)

    public Item(int itID, int currQty, String nm, String desc, int prce,
                int thresh, String image, int charityQ) {
        this.itemID      = itID;
        this.currentQty  = currQty;
        this.name        = nm;
        this.description = desc;
        this.price       = prce;
        this.threshold   = thresh;
        this.imageURL   = image;
        this.charityQty = charityQ;
        updatePercentage();
        updateTotalPrice();

        //like calculated percent, but includes the default qty of 1 in recyclerViews
        //(already included in progressbar)
        tempProgress = ((this.charityQty+1)*100)/this.threshold;
    }

    public int getItemID() { return itemID; }

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

    public int getPrice() { return price; }

    public int getThreshold() { return threshold; }

    public String getImageURL()
    {
        return imageURL;
    }

    public int getCalculatedPerc() {
        updatePercentage();
        return calculatedPerc;
    }

    public int getCharityQty() { return charityQty; }

    public int getTotalPrice() {
        updateTotalPrice();
        return totalPrice;
    }

    public int getTempPercent()
    {
        return tempProgress;
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

    public void setImageFile(String res_name)
    {
        imageURL = res_name;
    }

    public void setCharityQty(int charityQ) { charityQty = charityQ;}

    public void updateTotalPrice() {this.totalPrice = this.currentQty * this.price;}

    public void updatePercentage() { this.calculatedPerc = (this.charityQty*100)/this.threshold; }

    public void setTempProgress(int prog)
    {
        this.tempProgress = prog;
    }


    public void addToTemporaryProgress(int qty) {
        this.tempProgress += ((qty*100)/this.threshold);
    }

    public void resetTemporaryProgress() {
        this.tempProgress = this.calculatedPerc;
    }
}
