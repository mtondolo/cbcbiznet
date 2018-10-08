package com.android.example.comesanews;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LatestNewsAdapter extends
        RecyclerView.Adapter<LatestNewsAdapter.LatestNewsAdapterViewHolder> {

    private String[] mLatestNewsData;

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final LatestNewsAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface LatestNewsAdapterOnClickHandler {
        void onClick(String latestNewsItem);
    }

    private Cursor mCursor;

    /**
     * Creates a NewsAdapter.
     */
    public LatestNewsAdapter(Context context, LatestNewsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    // This gets called when each new ViewHolder is created.
    @Override
    public LatestNewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Inflate the list item xml into a view
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.latest_news_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        // Return a new LatestNewsAdapterViewHolder with the above view passed in as a parameter
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new LatestNewsAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     */
    @Override
    public void onBindViewHolder(LatestNewsAdapterViewHolder latestNewsAdapterViewHolder, int position) {

        // Move the cursor to the appropriate position
        mCursor.moveToPosition(position);

        /*******************
         * News Item *
         *******************/

        // Read numdays, yellowCardNumber, vehicleRegistrationNumber and name from the cursor
        String title = mCursor.getString(LatestNewsActivity.INDEX_TITLE);
        String date = mCursor.getString(LatestNewsActivity.INDEX_DATE);
        String author = mCursor.getString(LatestNewsActivity.INDEX_AUTHOR);

        // Display the summary that we created above
        String newsItem = title + " - " + date + " - " + author;

        latestNewsAdapterViewHolder.mLatestNewsTextView.setText(newsItem);

    }

    // This method simply returns the number of items to display.
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    // Swaps the cursor used by the LatestNewsAdapter for its news data.
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    // Cache of the children views for a latest news list item.
    public class LatestNewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mLatestNewsTextView;

        public LatestNewsAdapterViewHolder(View view) {
            super(view);
            mLatestNewsTextView = (TextView) view.findViewById(R.id.tv_latest_news_data);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String latestNewsItem = mLatestNewsData[adapterPosition];
            mClickHandler.onClick(latestNewsItem);
        }
    }
}
