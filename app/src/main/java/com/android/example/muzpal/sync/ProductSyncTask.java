package com.android.example.muzpal.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.android.example.muzpal.data.ProductContract;
import com.android.example.muzpal.utils.JsonUtils;
import com.android.example.muzpal.utils.NetworkUtils;

import java.net.URL;

public class ProductSyncTask {

    // Performs the network request for updated product, parses the JSON from that request.
    synchronized public static void syncProduct(Context context) {

        try {

            // The getUrl method will return the URL that we need to get the JSON for the products.
            URL productRequestUrl = NetworkUtils.buildUrl();

            // Use the URL to retrieve the JSON
            String jsonProductResponse = NetworkUtils.getResponseFromHttpUrl(productRequestUrl);

            // Parse the JSON into a list of product values
            ContentValues[] productValues = JsonUtils.getProductFromJsonStr(context, jsonProductResponse);

            // In cases where our JSON contained an error code, getProductFromJsonStr would have returned null.
            if (productValues != null && productValues.length != 0) {

                // Get a handle on the ContentResolver to delete and insert data
                ContentResolver productContentResolver = context.getContentResolver();

                // Delete old product data because we don't need to keep multiple days' data
                productContentResolver.delete(
                        ProductContract.ProductEntry.CONTENT_URI,
                        null,
                        null);

                /* Insert our new product data into product's ContentProvider */
                productContentResolver.bulkInsert(
                        ProductContract.ProductEntry.CONTENT_URI,
                        productValues);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
