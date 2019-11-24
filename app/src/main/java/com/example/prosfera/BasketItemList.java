package com.example.prosfera;

import java.util.*;

public class BasketItemList {

    private ArrayList<Item> basketItems;
    private ArrayList<Integer> itemQtys;

    public BasketItemList() {
        this.basketItems = new ArrayList<Item>();
        this.itemQtys = new ArrayList<Integer>();
    }

    public ArrayList<Item> getBasketItems() { return this.basketItems; }
    public ArrayList<Integer> getItemQtys() { return this.itemQtys; }

    public void setBasketItems(ArrayList<Item> items) { this.basketItems = items; }
    public void setItemQtys(ArrayList<Integer> qtys) { this.itemQtys = qtys; }

    public void addToLists(Item newItem, int newQty) {
        basketItems.add(newItem);
        itemQtys.add(newQty);
    }

    public void removeFromLists(int position) {
        basketItems.remove(position);
        itemQtys.remove(itemQtys);
    }

    public void updateQty(int newQty, int position) {
        itemQtys.set(position, newQty);
    }

    @Override
    public String toString() {
        String basketItemContents = "";

        for( int i=0 ; i < basketItems.size() ; i++ ) {
            basketItemContents += "Item " + (i++) + ":" + basketItems.toString() + "\n" +
                                  "Quantity: " + itemQtys.toString() + "\n";
        }

        return basketItemContents;
    }

}
