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

    // temp vars for testing

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

        // TODO: upon merge, change reference to XML file on this line
        setContentView(R.layout.activity_basket);

        //Bundle itemData = getIntent().getExtras();
        ItemList il = new ItemList(this);

        // Populates basket
        initImageBitmaps();

        // handles if data returned is null
        /**
        if (itemData == null) {
            return;
        }**/

        // get info from itemData bundle
        //int itemId = itemData.getInt("itemId");

        // use itemId to reference item and get its name
        //String itemName = il.getItem(itemId).getName();

        // instantiate views and set text
        //final TextView detailsTitle = findViewById(R.id.detailsTitle);
        //detailsTitle.setText(itemName);

    }
//database connection here

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

    // FOR TESTING
    // This is how the basket is being populated
    private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: preparing wishlist bitmaps.");


        //TODO: add reference to items
        mImageUrls.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        mNames.add("Havasu Falls");
        //temporary. These need to change
        mPrices.add(5);
        mProgress.add(30);
        mQuantities.add(1);

        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mNames.add("Trondheim");
        //temporary. These need to change
        mPrices.add(5);
        mProgress.add(30);
        mQuantities.add(1);

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mNames.add("Portugal");
        //temporary. These need to change
        mPrices.add(5);
        mProgress.add(30);
        mQuantities.add(1);

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        mNames.add("Rocky Mountain National Park");
        //temporary. These need to change
        mPrices.add(5);
        mProgress.add(30);
        mQuantities.add(1);

        /**
        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add("Mahahual");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mNames.add("Frozen Lake");

        mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg");
        mNames.add("White Sands Desert");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
        mNames.add("Austrailia");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mNames.add("Washington");
        **/
        initBasketRecyclerView();

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

                    adapter1.notifyItemRemoved(position);
                    Snackbar.make(rv1, "Removed from Basket", Snackbar.LENGTH_LONG)
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
