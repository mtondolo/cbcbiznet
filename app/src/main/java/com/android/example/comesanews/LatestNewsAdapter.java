package com.android.example.comesanews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LatestNewsAdapter extends RecyclerView.Adapter<LatestNewsAdapter.LatestNewsAdapterViewHolder> {

    private String[] mLatestNewsData;

    // This gets called when each new ViewHolder is created.
    @Override
    public LatestNewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Inflate the list item xml into a view
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.latest_news_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        // Return a new PolicyAdapterViewHolder with the above view passed in as a parameter
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new LatestNewsAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     */
    @Override
    public void onBindViewHolder(LatestNewsAdapterViewHolder latestNewsAdapterViewHolder, int position) {

        // Set the text of the TextView to the policy for this list item's position
        String latestNewsItem = mLatestNewsData[position];
        latestNewsAdapterViewHolder.mLatestNewsTextView.setText(latestNewsItem);

    }

    // This method simply returns the number of items to display.
    @Override
    public int getItemCount() {
        if (null == mLatestNewsData) return 0;
        return mLatestNewsData.length;
    }

    // Cache of the children views for a latest news list item.
    public class LatestNewsAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mLatestNewsTextView;

        public LatestNewsAdapterViewHolder(View view) {
            super(view);
            mLatestNewsTextView = (TextView) view.findViewById(R.id.tv_latest_news_data);
        }
    }

    /**
     * This method is used to set the news on a PolicyAdapter if we've already created one.
     */
    public void setLatestNewsData(String[] latestNewsData) {
        mLatestNewsData = latestNewsData;
        notifyDataSetChanged();
    }

}
