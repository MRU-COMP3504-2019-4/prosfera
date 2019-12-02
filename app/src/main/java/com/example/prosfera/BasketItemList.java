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

    //Same as above, but inserts at a given position
    public void addToLists(Item newItem, int newQty, int position) {
        basketItems.add(position, newItem);
        itemQtys.add(position, newQty);
    }

    public void removeFromLists(int position) {
        basketItems.remove(position);
        itemQtys.remove(position);
    }

    //Same as removeFromLists, but returns the removed Item
    public Item deleteFromLists(int position) {
        Item removed_item = basketItems.remove(position);
        itemQtys.remove(position); //need to fix quantity issues
        return removed_item;
    }

    public void updateQty(int newQty, int position) {
        itemQtys.set(position, newQty);
    }

    //finds if an itemID is in the list. Returns -1 if not, and the position of the item if found
    public int findItemInList(int itemID) {
        int position = -1;

        if (!basketItems.isEmpty()) {
            for (int i = 0; i < basketItems.size(); i++) {
                int currID = basketItems.get(i).getItemID();
                if (currID == itemID) {
                    position = i;
                }
            }
        }
        return position;
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
