package com.android.example.muzpal;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProductAdapter extends
        RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder> {

    // The context we use to utility methods, app resources and layout inflaters
    private final Context mContext;

    // An on-click handler that we've defined to make it easy for an Activity to interface with our RecyclerView
    private final ProductAdapterOnClickHandler mClickHandler;

    // The interface that receives onClick messages.
    public interface ProductAdapterOnClickHandler {
        void onClick(String product);
    }

    private Cursor mCursor;

    /**
     * Creates a ProductAdapter.
     */
    public ProductAdapter(Context context, ProductAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    // This gets called when each new ViewHolder is created.
    @Override
    public ProductAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Inflate the list item xml into a view
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        // Return a new LatestNewsAdapterViewHolder with the above view passed in as a parameter
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ProductAdapterViewHolder(view);
    }

    // OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(ProductAdapterViewHolder productAdapterViewHolder, int position) {

        // Move the cursor to the appropriate position
        mCursor.moveToPosition(position);

        /* Get image, company, name, description and web page from the cursor and display the values*/
        String image = mCursor.getString(ProductActivity.INDEX_IMAGE);

        if (image.isEmpty()) {//url.isEmpty()
            Picasso.get()
                    .load(R.color.colorPrimary)
                    .placeholder(R.color.colorPrimary)
                    .resize(126, 78)
                    .centerCrop()
                    .into(productAdapterViewHolder.imageView);
        } else {
            Picasso.get()
                    .load(image)
                    .error(R.color.colorPrimary)
                    .fit()
                    .into(productAdapterViewHolder.imageView);//this is our ImageView
        }

        String company = mCursor.getString(ProductActivity.INDEX_COMPANY);
        productAdapterViewHolder.companyView.setText(company);

        String description = mCursor.getString(ProductActivity.INDEX_DESCRIPTION);
        productAdapterViewHolder.descriptionView.setText(description);
    }

    // This method simply returns the number of items to display.
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    // Swaps the cursor used by the ProductAdapter for its product data.
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    // Cache of the children views for a product.
    public class ProductAdapterViewHolder extends
            RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imageView;
        final TextView companyView;
        final TextView descriptionView;

        public ProductAdapterViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.image);
            companyView = (TextView) view.findViewById(R.id.company);
            descriptionView = (TextView) view.findViewById(R.id.description);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // To be implemented later.
        }
    }
}
