package com.android.example.muzpal.utils;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.android.example.muzpal.data.ProductContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    // This method parses JSON from a web response and returns an array of Strings
    public static ContentValues[] getProductFromJsonStr(Context context, String productJsonStr)
            throws JSONException {

        // headline, story, author and image are keys for the product item
        final String KEY_COMPANY = "company";
        final String KEY_DESCRIPTION = "description";
        final String KEY_IMAGE = "image";

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(productJsonStr)) {
            return null;
        }

        JSONArray productArray = new JSONArray(productJsonStr);

        ContentValues[] productContentValues = new ContentValues[productArray.length()];

        for (int i = 0; i < productArray.length(); i++) {

            /* These are the values that will be collected */
            String company;
            String description;
            String image;

            /* Get the JSON object representing the product item */
            JSONObject product = productArray.getJSONObject(i);

            // Extract the value for the key called "company", "story", "author" and "image"
            company = product.getString(KEY_COMPANY);
            description = product.getString(KEY_DESCRIPTION);
            image = product.getString(KEY_IMAGE);

            ContentValues productValues = new ContentValues();
            productValues.put(ProductContract.ProductEntry.COLUMN_COMPANY, company);
            productValues.put(ProductContract.ProductEntry.COLUMN_DESCRIPTION, description);
            productValues.put(ProductContract.ProductEntry.COLUMN_IMAGE, image);

            productContentValues[i] = productValues;

        }
        return productContentValues;
    }
}

