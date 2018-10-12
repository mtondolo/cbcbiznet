package com.android.example.comesanews;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class LatestNewsAdapter extends
        RecyclerView.Adapter<LatestNewsAdapter.LatestNewsAdapterViewHolder> {

    // Constant IDs for the ViewType for latest and for past news
    private static final int VIEW_TYPE_LATEST = 0;
    private static final int VIEW_TYPE_PAST = 1;

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

    private boolean mUseLatestLayout;
    private Cursor mCursor;

    /**
     * Creates a NewsAdapter.
     */
    public LatestNewsAdapter(Context context, LatestNewsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mUseLatestLayout = mContext.getResources().getBoolean(R.bool.use_latest_layout);
    }

    // This gets called when each new ViewHolder is created.
    @Override
    public LatestNewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId;
        switch (viewType) {
            case VIEW_TYPE_LATEST: {
                layoutId = R.layout.news_list_item_latest;
                break;
            }
            case VIEW_TYPE_PAST: {
                layoutId = R.layout.news_list_item;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
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

        /* Get image, title, name, date and web page from the cursor and display the values*/
        String imageUrl = mCursor.getString(LatestNewsActivity.INDEX_IMAGE);

        if (imageUrl.isEmpty()) {//url.isEmpty()
            Picasso.get()
                    .load(R.mipmap.ic_launcher_placeholder)
                    .placeholder(R.mipmap.ic_launcher_placeholder)
                    .error(R.mipmap.ic_launcher_placeholder)
                    .resize(176, 128)
                    .centerCrop()
                    .into(latestNewsAdapterViewHolder.imageView);
        } else {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher_placeholder)
                    .resize(176, 128)
                    .centerCrop()
                    .into(latestNewsAdapterViewHolder.imageView);//this is our ImageView
        }

        String title = mCursor.getString(LatestNewsActivity.INDEX_TITLE);
        latestNewsAdapterViewHolder.titleView.setText(title);

        String author = mCursor.getString(LatestNewsActivity.INDEX_AUTHOR);
        latestNewsAdapterViewHolder.authorView.setText(author);

        String date = mCursor.getString(LatestNewsActivity.INDEX_DATE);
        latestNewsAdapterViewHolder.dateView.setText(date);

    }

    // This method simply returns the number of items to display.
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * Returns an integer code related to the type of View we want the ViewHolder to be at a given
     * position.
     */
    @Override
    public int getItemViewType(int position) {
        if (mUseLatestLayout && position == 0) {
            return VIEW_TYPE_LATEST;
        } else {
            return VIEW_TYPE_PAST;
        }
    }

    // Swaps the cursor used by the LatestNewsAdapter for its news data.
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    // Cache of the children views for a latest news list item.
    public class LatestNewsAdapterViewHolder extends
            RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imageView;
        final TextView titleView;
        final TextView dateView;
        final TextView authorView;

        public LatestNewsAdapterViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.image);
            titleView = (TextView) view.findViewById(R.id.title);
            dateView = (TextView) view.findViewById(R.id.date);
            authorView = (TextView) view.findViewById(R.id.author);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String url = mCursor.getString(LatestNewsActivity.INDEX_WEB);
            mClickHandler.onClick(url);

        }
    }
}
