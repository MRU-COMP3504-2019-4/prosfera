package com.example.prosfera;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class GiftBasket extends AppCompatActivity {

    private static final String TAG = "GiftBasket";

    private int    basketID,
                   donorID,
                   basketItemID;
    private double total;
    BasketItemList bItems = MainActivity.getBasketItems();

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<Integer> mPrices = new ArrayList<>();
    private ArrayList<Integer> mProgress = new ArrayList<>();
    private ArrayList<Integer> mQuantities  = new ArrayList<>();

    //Created as globals to be used in onSwipe and onChildDraw (Swipe to delete)
    RecyclerViewAdapter adapter1;
    RecyclerView rv1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        //Bundle itemData = getIntent().getExtras();

        if(!bItems.getBasketItems().isEmpty()) {
            // Populates basket
            populateBasket();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    /*
        Constructor Class to initialize variables.
     */
    public GiftBasket() {
        // Blank constructor required by activity
        // args int dID, int bIID
        //this.basketID = 001; //this would actually be pulled from the database and incremented as needed
        //this.donorID = dID;
        //this.basketItemID = bIID;
        //this.total = 0;
    }

    /*
        Getters for basketID, donorID, basketItemID, and total.
     */
    public int getBasketID() { return this.basketID; }
    public int getDonorID() { return this.donorID; }
    public int getBasketItemID() { return this.basketItemID; }
    public double getTotal() { return this.total; }

    /*
        Setters for basketID, donorID, basketItemID, and total.
     */
    public void setDonorID(int dID) { this.donorID = dID; } //might not need
    public void setBasketItemID(int bIID) { this.basketItemID = bIID; } //might not need


    public void populateBasket() {

        for ( int i=0 ; i < bItems.getBasketItems().size() ; i++ ) {
            mImageUrls.add(bItems.getBasketItems().get(i).getImageURL());
            mNames.add(bItems.getBasketItems().get(i).getName());
            mPrices.add(bItems.getBasketItems().get(i).getTotalPrice());
            mProgress.add(bItems.getBasketItems().get(i).getTempPercent());
            mQuantities.add(bItems.getItemQtys().get(i));
        }

        initBasketRecyclerView();
    }

    private void initBasketRecyclerView(){
        Log.d(TAG, "initBasketRecyclerView: called.");

        rv1 = findViewById(R.id.basketRecycler);

        // TODO: Conditionally load data structures (wishlistNames or basketNames)

        adapter1 = new RecyclerViewAdapter(this, mNames, mImageUrls, mPrices, mProgress, mQuantities);
        rv1.setAdapter(adapter1);
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv1.setHasFixedSize(true);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallBack);
        itemTouchHelper.attachToRecyclerView(rv1);
    }

    /*
     * This section of code is to attach the "Swipe-to-delete" functionality on the Gift Basket.
     * Custom libraries are used to create the "undo" Snackbar and decoration when an item is slid to the left.
     * Taken from https://www.youtube.com/watch?v=rcSNkSJ624U
     */
    ItemTouchHelper.SimpleCallback simpleCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();
            if(direction == ItemTouchHelper.LEFT) {
                    //Stores and removes the elements of the deleted item
                    final String delName = mNames.remove(position);
                    final String delURL = mImageUrls.remove(position);
                    final int delPrice = mPrices.remove(position);
                    final int delProgress = mProgress.remove(position);
                    final int delQty = mQuantities.remove(position);
                    final Item delItem = bItems.deleteFromLists(position);

                    //reset the progress bar in wishlist if an item has been deleted from basket
                    ItemList il = MainActivity.getWishlistItems();
                    final Item del_item_wishlist = il.getItem(il.findItemInList(delItem.getItemID()));
                    final int del_progress = del_item_wishlist.getTempPercent();
                    del_item_wishlist.resetTemporaryProgress();

                    adapter1.notifyItemRemoved(position);
                    Snackbar.make(rv1, "Removed from Gift Basket", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener(){

                            //If undo is clicked, add the elements back into their arrays
                            @Override
                            public void onClick(View view) {
                                mNames.add(position, delName);
                                mImageUrls.add(position, delURL);
                                mPrices.add(position, delPrice);
                                mProgress.add(position, delProgress);
                                mQuantities.add(position, delQty);
                                adapter1.notifyItemInserted(position);
                                bItems.addToLists(delItem, delQty, position);
                                del_item_wishlist.setTempProgress(del_progress);
                            }
                        }).show();
            }
        }
        //This creates the background color and delete icon underneath a swiped item
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(recyclerView.getContext(), R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}
