package com.example.prosfera;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

// Recycler view class taken from "RecyclerView" tutorial by CodingWithMitch
// src: https://www.youtube.com/watch?v=Vyqz_-sJGFk

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ItemList mItemList;
    private ArrayList<String> mImageNames;
    private ArrayList<String> mImageUrls;
    private ArrayList<Integer> mPrices;
    private ArrayList<Integer> mProgress;
    private ArrayList<Integer> mQuantities;
    private Context mContext;
    private PopupWindow popup;
    BasketItemList bItems = MainActivity.getBasketItems();

    //This is used in the close function, in which this value is needed to reset the
    //progress bar and qty
    private int changed_by = 0;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mImageNames, ArrayList<String> mImageUrls, ArrayList<Integer> mPrices, ArrayList<Integer> mProgress,
                               ArrayList<Integer> mQuantities) {
        this.mImageNames = mImageNames;
        this.mImageUrls = mImageUrls;
        this.mPrices = mPrices;
        this.mContext = mContext;
        this.mProgress = mProgress;
        this.mQuantities = mQuantities;
         this.mItemList = new ItemList(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // TODO: Add condition: if loaded via this button or page -> inflate this item
        // If loaded by wishlist, inflate layout_listitem
        // If loaded by activity_basket, inflate layout_basketitem
//        View view = null;
//
//        if(mContext instanceof MainActivity ) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
//        }
//        else if(mContext instanceof GiftBasket) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_basketitem, parent, false);
//        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called with 2 parameters.");
        Glide.with(mContext)
                .asBitmap()
                .load(mImageUrls.get(position))
                .into(holder.image);

        bindFunc(holder, position);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> qtyArray) {
        Log.d(TAG, "onBindViewHolder: called with 3 parameters.");
        if(!qtyArray.isEmpty()) {
            if (qtyArray.get(0) instanceof String) {
                holder.quantity.setText(String.valueOf(qtyArray.get(0)));
            }
        } else {
            super.onBindViewHolder(holder,position, qtyArray);
        }
    }

    private void bindFunc(@NonNull ViewHolder holder, final int position){

        holder.imageName.setText(mImageNames.get(position));
        holder.price.setText(Integer.toString(mPrices.get(position)));

        //TODO: Deal with manual quantity edits in the EditText box. Disabling changes for now
        //TODO: You must also change a few line in the listitem XML: android:inputType="none"
        holder.price.setEnabled(false);


        holder.quantity.setText(Integer.toString(mQuantities.get(position)));
        holder.progress.setMax(100);
        holder.progress.setProgress(mProgress.get(position));

        Item itm = null;
        if(mContext instanceof MainActivity ) {
            itm = mItemList.getItem(position);
        }
        else if(mContext instanceof GiftBasket) {
            itm = bItems.getBasketItems().get(position);
        }

        final Item clickedItem = itm;

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));

                ViewGroup root = (ViewGroup) v.getRootView();
                final ScrollView sv = (ScrollView) root.findViewById(R.id.scroll);
                sv.setSmoothScrollingEnabled(true);

                // Inflates 'layout_listitem_popup.xml' as a centered popup window
                final View container = LayoutInflater.from(mContext).inflate(R.layout.layout_listitem_popup, root, false);
                popup = new PopupWindow(container, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);

                // Closes the popup window when touched outside.
                popup.setOutsideTouchable(true);
                popup.setFocusable(true);
                // Removes default background.
                popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final int[] screenPos = new int[2];
                v.getLocationOnScreen(screenPos);

                //Show popup overtop clicked view
                popup.showAsDropDown(v, 0, -v.getHeight() + container.getHeight()-150);

                // This gets the height of the popup (before it has been drawn)
                container.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

                final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
                final int popupHeight = container.getMeasuredHeight();

                Log.d(TAG, "Screen height is " + screenHeight);
                Log.d(TAG, "Clicked item is at " + screenPos[0] +" and "+screenPos[1] );
                Log.d(TAG, "Popup height is " + popupHeight );

                final int[] vertical_scroll = new int[]{0};

                //if the item it too far down to entirely show the popup, scroll down
                if(screenPos[1] > (screenHeight-popupHeight) && !(screenPos[1] < popupHeight)) {

                    //Only main activity has FAB. This class is also used in Giftbasket, so this would cause an error
                    if(mContext instanceof MainActivity ) {
                            FloatingActionButton fab = root.findViewById(R.id.fab);
                            final int fabHeight = fab.getHeight();
                            vertical_scroll[0] = (popupHeight - (screenHeight - screenPos[1])) + fabHeight;
                        }
                    else {
                        vertical_scroll[0] = (popupHeight - (screenHeight - screenPos[1]));
                    }
                }
                //if the item it high up to entirely show the popup, scroll up
                else if(!(screenPos[1] > (screenHeight-popupHeight)) && (screenPos[1] < popupHeight)) {
                    vertical_scroll[0] = screenPos[1]-popupHeight + v.getHeight();
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sv.smoothScrollBy(0, vertical_scroll[0]);
                    } }, 200);

                View parent;
                if (android.os.Build.VERSION.SDK_INT > 22) {
                    parent = (View) popup.getContentView().getParent().getParent();
                }else{
                    parent = (View) popup.getContentView().getParent();
                }
                //dim the window in the background
                WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) parent.getLayoutParams();
                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.6f;
                wm.updateViewLayout(parent, p);


                // Find elements in the popup container
                TextView name = container.findViewById(R.id.image_name);
                final EditText qty = container.findViewById(R.id.itemQty);
                final TextView price = container.findViewById(R.id.itemPrice);
                final ProgressBar progress = container.findViewById(R.id.progressView);
                CircleImageView img = container.findViewById(R.id.image);

                final int unitPrice = clickedItem.getPrice();

                // Set data of popup window to match clicked element
                name.setText(clickedItem.getName());
                if(mContext instanceof MainActivity ) {
                    qty.setText(Integer.toString(clickedItem.getCurrentQty()));
                }
                else if(mContext instanceof GiftBasket) {
                    int basket_pos = bItems.findItemInList(clickedItem.getItemID());
                    qty.setText(Integer.toString(bItems.getItemQtys().get(basket_pos)));
                }

                price.setText(Integer.toString(clickedItem.getTotalPrice()));
                progress.setProgress(clickedItem.getTempPercent());
                Glide.with(mContext)
                        .asBitmap()
                        .load(mImageUrls.get(position))
                        .into(img);

                // Find button elements of popup container
                TextView details = container.findViewById(R.id.button_details);
                ImageButton qty_increment = container.findViewById(R.id.button_qty_increment);
                ImageButton qty_decrement = container.findViewById(R.id.button_qty_decrement);
                ImageButton okay_button = container.findViewById(R.id.button_ok);
                ImageButton exit_button =container.findViewById(R.id.button_exit);

                details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "onClick: clicked on details button");
                        Intent intent = new Intent(mContext, ItemDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("itemObj", clickedItem);
                        intent.putExtras(bundle);

                        mContext.startActivity(intent);
                    }
                });

                qty_increment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "onClick: clicked on increment button");
                        int qty_text = Integer.parseInt(qty.getText().toString().trim());
                        qty_text++;
                        changed_by++;
                        qty.setText(Integer.toString(qty_text));
                        clickedItem.setCurrentQTY(qty_text);
                        clickedItem.addToTemporaryProgress(1);
                        price.setText(Integer.toString(clickedItem.getTotalPrice()));
                        progress.setProgress(clickedItem.getTempPercent());
                    }
                });

                qty_decrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "onClick: clicked on decrement button");
                        int qty_text = Integer.parseInt(qty.getText().toString().trim());
                        if(qty_text > 1){
                            qty_text--;
                            changed_by--;
                            clickedItem.addToTemporaryProgress(-1);
                            progress.setProgress(clickedItem.getTempPercent());
                        }
                        qty.setText(Integer.toString(qty_text));
                        clickedItem.setCurrentQTY(qty_text);
                        price.setText(Integer.toString(clickedItem.getTotalPrice()));
                    }
                });

                okay_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "onClick: clicked on okay button");

                        boolean found = false;

                        int qty_text = Integer.parseInt(qty.getText().toString().trim());
                        int clickedItemID = clickedItem.getItemID();
                        int bList_pos = bItems.findItemInList(clickedItemID);

                        //ADD TO CART
                        //check if item exists in basket
                        if(bList_pos > -1) {
                            found = true;
                        }

                        if(!found) {
                        //add new item
                        Item newItem = new Item(clickedItem.getItemID(), clickedItem.getCurrentQty(),
                                clickedItem.getName(), clickedItem.getDescription(),
                                clickedItem.getPrice(), clickedItem.getThreshold(), clickedItem.getImageURL(), clickedItem.getCharityQty());

                        newItem.addToTemporaryProgress(qty_text);
                        bItems.addToLists(newItem, qty_text);
                        } else {
                            //add quantity to existing item
                            int currQty = bItems.getItemQtys().get(bList_pos);
                            Item basketItem = bItems.getBasketItems().get(bList_pos);

                            if(mContext instanceof MainActivity) {
                                bItems.updateQty((currQty + qty_text), bList_pos);
                                basketItem.addToTemporaryProgress(qty_text);
                            }

                            else if(mContext instanceof GiftBasket) {
                                bItems.updateQty(qty_text, bList_pos);
                                basketItem.resetTemporaryProgress();
                                basketItem.addToTemporaryProgress(qty_text);

                                int wishlist_pos = mItemList.findItemInList(clickedItemID);
                                Item wishlistItem = mItemList.getWishlistItems().get(wishlist_pos);
                                wishlistItem.resetTemporaryProgress();
                                wishlistItem.addToTemporaryProgress(qty_text);
                            }

                        }
                        //Notify the recycler view
                        if(mContext instanceof GiftBasket ) {
                            clickedItem.setCurrentQTY(qty_text);
                            clickedItem.updateTotalPrice();

                            mQuantities.set(bList_pos, clickedItem.getCurrentQty());
                            mPrices.set(bList_pos, clickedItem.getTotalPrice());
                            mProgress.set(bList_pos, clickedItem.getTempPercent());
                            RecyclerViewAdapter.this.notifyItemChanged(bList_pos);
                        }
                        else if(mContext instanceof MainActivity) {
                            clickedItem.setCurrentQTY(1);
                            clickedItem.updateTotalPrice();
                            mQuantities.set(position, clickedItem.getCurrentQty());
                            mPrices.set(position, clickedItem.getTotalPrice());
                            mProgress.set(position, clickedItem.getTempPercent());
                            RecyclerViewAdapter.this.notifyItemChanged(position);

                            //Make 'added to gift basket' popup
                            Toast toast = Toast.makeText(mContext, "Added to Gift Basket.", Toast.LENGTH_LONG);
                            View view =toast.getView();
                            view.getBackground().setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                            TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                            toastMessage.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                            toast.show();
                        }
                        popup.dismiss();
                    }
                });

                exit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        Log.d(TAG, "onClick: clicked on exit button");
                        if(mContext instanceof MainActivity ) {
                            clickedItem.setCurrentQTY(Integer.parseInt(qty.getText().toString().trim()));
                            clickedItem.updateTotalPrice();
                            mQuantities.set(position, clickedItem.getCurrentQty());
                            mPrices.set(position, clickedItem.getTotalPrice());
                            mProgress.set(position, progress.getProgress());

                            RecyclerViewAdapter.this.notifyItemChanged(position);
                        }
                        //The close button does NOT save changes on the giftbasket page
                        //This reverts any changes made on the popup, and ensures that the data is not
                        //sent back to the main list
                        if(mContext instanceof GiftBasket ) {
                            clickedItem.setCurrentQTY(Integer.parseInt(qty.getText().toString().trim())-changed_by);
                            clickedItem.updateTotalPrice();
                            clickedItem.addToTemporaryProgress(-changed_by);

                            mQuantities.set(position, clickedItem.getCurrentQty()-changed_by);
                            mPrices.set(position, clickedItem.getTotalPrice());
                            mProgress.set(position, clickedItem.getTempPercent());
                        }
                        changed_by = 0;
                        popup.dismiss();
                    }
                });


            }
        });
    }

    // size of item list = number of items loaded into recyclerView
    @Override
    public int getItemCount() { return mImageNames.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView imageName;
        TextView price;
        ProgressBar progress;
        EditText quantity;
        RelativeLayout rl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            price = itemView.findViewById(R.id.itemPrice);
            progress = itemView.findViewById(R.id.progressView);
            quantity = itemView.findViewById(R.id.itemQty);
            rl = itemView.findViewById(R.id.parent_layout);
        }
    }
}

