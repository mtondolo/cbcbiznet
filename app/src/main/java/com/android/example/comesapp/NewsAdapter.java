package com.android.example.comesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class NewsAdapter extends
        RecyclerView.Adapter<NewsAdapter.NewsAdapterViewHolder> {

    // The context we use to utility methods, app resources and layout inflaters
    private final Context mContext;

    // An on-click handler that we've defined to make it easy for an Activity to interface with our RecyclerView
    private final NewsAdapterOnClickHandler mClickHandler;

    // The interface that receives onClick messages.
    public interface NewsAdapterOnClickHandler {
        void onClick(String news);
    }

    private Cursor mCursor;

    /**
     * Creates a NewsAdapter.
     */
    public NewsAdapter(Context context, NewsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    // This gets called when each new ViewHolder is created.
    @Override
    public NewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Inflate the list item xml into a view
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        // Return a new LatestNewsAdapterViewHolder with the above view passed in as a parameter
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new NewsAdapterViewHolder(view);
    }

    // OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(NewsAdapterViewHolder newsAdapterViewHolder, int position) {

        // Move the cursor to the appropriate position
        mCursor.moveToPosition(position);

        /* Get image, headline, name, date and web page from the cursor and display the values*/
        String image = mCursor.getString(NewsActivity.INDEX_IMAGE);

        if (image.isEmpty()) {//url.isEmpty()
            Picasso.get()
                    .load(R.color.colorPrimary)
                    .placeholder(R.color.colorPrimary)
                    .resize(126, 78)
                    .centerCrop()
                    .into(newsAdapterViewHolder.imageView);
        } else {
            Picasso.get()
                    .load(image)
                    .error(R.color.colorPrimary)
                    .fit()
                    .into(newsAdapterViewHolder.imageView);//this is our ImageView
        }

        String headline = mCursor.getString(NewsActivity.INDEX_HEADLINE);
        newsAdapterViewHolder.headlineView.setText(headline);

        String date = mCursor.getString(NewsActivity.INDEX_DATE);
        newsAdapterViewHolder.dateView.setText(date);
    }

    // This method simply returns the number of items to display.
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    // Swaps the cursor used by the NewsAdapter for its news data.
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    // Cache of the children views for a news.
    public class NewsAdapterViewHolder extends
            RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView headlineView;
        final TextView dateView;
        final ImageView imageView;

        public NewsAdapterViewHolder(View view) {
            super(view);

            headlineView = (TextView) view.findViewById(R.id.headline);
            dateView = (TextView) view.findViewById(R.id.date);
            imageView = (ImageView) view.findViewById(R.id.image);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // To be implemented later.
        }
    }
}
