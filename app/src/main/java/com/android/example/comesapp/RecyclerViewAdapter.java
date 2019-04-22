package com.android.example.comesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class RecyclerViewAdapter extends
        RecyclerView.Adapter {

    public static Cursor mCursor;

    // Constant IDs for the ViewType for latest and for past news
    private static final int VIEW_TYPE_LATEST = 0;
    private static final int VIEW_TYPE_PAST = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    // The context we use to utility methods, app resources and layout inflaters
    private final Context mContext;

    // An on-click handler that we've defined to make it easy for an Activity to interface with our RecyclerView
    private final RecyclerViewAdapterOnClickHandler mClickHandler;

    // The interface that receives onClick messages.
    public interface RecyclerViewAdapterOnClickHandler {
        void onClick(long dateTimeInMillis);
    }

    private boolean mUseLatestLayout;

    // Creates a RecyclerViewAdapter.
    public RecyclerViewAdapter(Context context, RecyclerViewAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mUseLatestLayout = mContext.getResources().getBoolean(R.bool.use_latest_layout);
    }

    // Returns an integer code related to the type of View we want the ViewHolder to be at a given position.
    @Override
    public int getItemViewType(int position) {
        if (mUseLatestLayout && position == 0) {
            return VIEW_TYPE_LATEST;
        } else if (position == mCursor.getCount()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_PAST;
        }
    }

    // This method simply returns the number of items to display.
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount() + 1;
    }

    // This gets called when each new ViewHolder is created.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_LATEST:
                return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.latest_list_item, parent, false));
            case VIEW_TYPE_PAST:
                return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
            case VIEW_TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.footer_item, parent, false));
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    // OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // Check the type of view to display
        if (holder instanceof ItemViewHolder) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            // Move the cursor to the appropriate position
            if (mCursor != null && mCursor.moveToPosition(position)) {

                // Get image from the cursor and display it.
                String image = mCursor.getString(NewsActivity.INDEX_IMAGE_URL);

                if (image.isEmpty()) {//url.isEmpty()
                    Picasso.get()
                            .load(R.color.colorPrimary)
                            .placeholder(R.color.colorPrimary)
                            .resize(126, 78)
                            .centerCrop()
                            .into(itemViewHolder.imageView);
                } else {
                    Picasso.get()
                            .load(image)
                            .error(R.color.colorPrimary)
                            .fit()
                            .into(itemViewHolder.imageView);//this is our ImageView
                }

                String headline = mCursor.getString(NewsActivity.INDEX_HEADLINE);
                itemViewHolder.headlineView.setText(headline);

                // Get given time in milliseconds
                long dateTimeInMillis = mCursor.getLong(NewsActivity.INDEX_DATE);

                // Get current time in milliseconds
                long currentDateLong = System.currentTimeMillis();

                // Convert time to relative time and add it to text view
                CharSequence relativeDate = DateUtils.getRelativeTimeSpanString(dateTimeInMillis, currentDateLong,
                        0L, DateUtils.FORMAT_ABBREV_ALL);

                itemViewHolder.dateView.setText(relativeDate);

            }
        }// Display footer if instance view is footer.
        else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.footerText.setText(R.string.detail_copyright);
        }
    }

    // Swaps the cursor used by the RecyclerViewAdapter for its news data.
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    // Cache of the children views for news item.
    public class ItemViewHolder extends
            RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView dateView;
        final TextView headlineView;
        final ImageView imageView;

        public ItemViewHolder(View view) {
            super(view);

            dateView = view.findViewById(R.id.date);
            headlineView = view.findViewById(R.id.headline);
            imageView = view.findViewById(R.id.image);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateTimeInMillis = mCursor.getLong(NewsActivity.INDEX_DATE);
            mClickHandler.onClick(dateTimeInMillis);
        }
    }

    // Cache of the children views for footer item.
    public class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView footerText;

        public FooterViewHolder(View view) {
            super(view);
            footerText = view.findViewById(R.id.footer_text);
        }
    }
}
