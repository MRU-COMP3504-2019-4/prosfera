package com.example.prosfera;

import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static ItemList il;
    private Item featuredItem;

    private static BasketItemList basketItems = new BasketItemList();

    // NOTE: Not the best way to load item information
    // (can just point to item in item list and use getters)
    // However, it HAS to be done this way because that's how the RecyclerView is set up
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<Integer> mPrices = new ArrayList<>();
    private ArrayList<Integer> mProgress = new ArrayList<>();
    private ArrayList<Integer> mQuantities  = new ArrayList<>();

    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        il =  new ItemList(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Log that activity started
        Log.d(TAG, "onCreate: started.");

        initFeaturedItem(il.getItem(0));
        initRecyclerView();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: basket fab clicked");

                Intent in = new Intent(MainActivity.this, GiftBasket.class);

                // To pass in extra data:
                //i.putExtra("cartId", featuredItem.getItemID());

                startActivity(in);

                // Unneeded default code
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //         .setAction("Action", null).show();
            }
        });

    }
  
    @Override
    public void onResume(){
        super.onResume();
        ArrayList<Integer> positions = checkForProgressUpdates();
        if(!positions.isEmpty()) {
            for (int i = 0; i < positions.size(); i++) {
                adapter.notifyItemChanged(positions.get(i));
            }
        }
    }

    // Populates featured item card with information
    private void initFeaturedItem(Item item) {
        // Note: Should featured item be removed from regular wishlist?
        this.featuredItem = item;

        // initialize views
        final TextView featuredDescription = findViewById(R.id.featuredDescription);
        final TextView featuredTitle = findViewById(R.id.featuredTitle);
        final ImageView featuredImage = findViewById(R.id.featuredPic);
        final CardView featuredCard = findViewById(R.id.featuredCard);

        // set their contents
        featuredTitle.setText(item.getName());
        featuredDescription.setText(item.getDescription());
        Glide.with(MainActivity.this)
                .asBitmap()
                .load(item.getImageURL())
                .into(featuredImage);

        // give featured parent an onclick
        featuredCard.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: featured item clicked");

                Intent intent = new Intent(MainActivity.this, ItemDetails.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("itemObj", featuredItem);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: preparing wishlist data");

        for(int i=0; i < il.getSize(); i++) {
            Item item = il.getItem(i);
            mImageUrls.add(item.getImageURL());
            mNames.add(item.getName());
            mPrices.add(item.getTotalPrice());
            mProgress.add(item.getTempPercent());
            mQuantities.add(1);
        }

        RecyclerView rv = findViewById(R.id.recyclerView);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv);

        adapter = new RecyclerViewAdapter(MainActivity.this, mNames, mImageUrls, mPrices, mProgress, mQuantities);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
    }

    public static BasketItemList getBasketItems() {
        return basketItems;
    }

    public static ItemList getWishlistItems() {
        return il;
    }

    // This checks to see if the adapter data is out of date from the data in the list, and then
    // updates accordingly
    // (Changes to progress made in the giftbasket don't automatically update the progress bars
    // on wishlist. Only the ITEMS list is changed, and not the recyclerView data)
    //
    // Updates the data in mProgress, and returns the positions changed (or empty array if none)
    public ArrayList<Integer> checkForProgressUpdates() {
        ArrayList<Integer> positions = new ArrayList<Integer>();

        for(int i=0; i < il.getSize(); i++) {
            int curr_prog = il.getItem(i).getTempPercent();
            int old_prog = mProgress.get(i);

            if(curr_prog != old_prog){
                mProgress.set(i, curr_prog);
                positions.add(i);
            }
        }
        return positions;
    }

}
