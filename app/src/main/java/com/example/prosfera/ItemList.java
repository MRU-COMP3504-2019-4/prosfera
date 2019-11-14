package com.example.prosfera;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ItemList {

    // An array of Items
    // Should be common among all itemlist instantiations
    private static final List<Item> ITEMS = new ArrayList<>();
    Context context;

    // Constructor
    public ItemList(Context context) {
        if(ITEMS.isEmpty()) {
            initItemList(context);
        }
    }

    public void addItem(Item item) {
        ITEMS.add(item);
    }

    public Item getItem(int pos) { return ITEMS.get(pos); }

    public int getSize(){ return ITEMS.size(); }

  
    // This method is used to read in the items.json file and create a list of Item objects.
    private static void initItemList(Context context) {
        try {
            //Assuming that the default current quantity on startup is 1
            int default_qty = 1;

            JSONObject obj = new JSONObject(loadJSONFromAsset("items.json", context));
            JSONArray itemsArray = obj.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {

                JSONObject item = itemsArray.getJSONObject(i);
                String name = item.getString("name");
                String desc = item.getString("description");
                int price = item.getInt("price");
                String image = item.getString("image");
                int threshold = item.getInt("threshold");

                Item itm = new Item(i + 1, default_qty, name, desc, price, threshold, image);
                //For testing:
                //System.out.println(itm);
                ITEMS.add(itm);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //This method can be used to read a JSON string from a file in the assets folder.
    //See https://stackoverflow.com/questions/19945411/android-java-how-can-i-parse-a-local-json-file-from-assets-folder-into-a-listvi/19945484#19945484
    private static String loadJSONFromAsset(String assetName, Context context){
        String json;
        try {
            InputStream in = context.getAssets().open(assetName);
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();

            json = new String(buffer,"UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
  
}

