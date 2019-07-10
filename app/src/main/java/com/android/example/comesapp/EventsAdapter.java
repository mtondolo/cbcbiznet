package com.android.example.comesapp;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsAdapterViewHolder> {

    // The context we use to utility methods, app resources and layout inflaters
    private final Context mContext;

    public Cursor mCursor;

    // Constant ID for the ViewType for footer
    private static final int VIEW_TYPE_TOP = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    private final OnEnquireTextViewClickListener mEnquireTextViewListener;

    private boolean mUseTopLayout;

    // The interface that receives onClick messages.
    public interface OnEnquireTextViewClickListener {
        void onEnquireIsClick(View button, String enquireTitle, String enquireEmail);
    }

    // Creates a NewsAdapter.
    public EventsAdapter(Context context, OnEnquireTextViewClickListener enquireTextViewListener) {
        mContext = context;
        mEnquireTextViewListener = enquireTextViewListener;
        mUseTopLayout = mContext.getResources().getBoolean(R.bool.use_top_layout);
    }

    // This gets called when each new ViewHolder is created.
    @Override
    public EventsAdapter.EventsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_NORMAL) {
            return new EventsAdapterViewHolder(LayoutInflater.from(mContext).
                    inflate(R.layout.event_list_item, viewGroup, false));
        } else if (viewType == VIEW_TYPE_FOOTER) {
            return new EventsAdapterViewHolder(LayoutInflater.
                    from(mContext).inflate(R.layout.footer_item, viewGroup, false));
        } else if (viewType == VIEW_TYPE_TOP) {
            return new EventsAdapterViewHolder(LayoutInflater.
                    from(mContext).inflate(R.layout.top_event_list_item, viewGroup, false));
        } else
            throw new IllegalArgumentException("Invalid view type, value of " + viewType);
    }

    // OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(EventsAdapterViewHolder eventsAdapterViewHolder, final int position) {

        // Move the cursor to the appropriate position
        if (mCursor != null && mCursor.moveToPosition(position)) {

            String title = mCursor.getString(EventsActivity.INDEX_TITLE);
            eventsAdapterViewHolder.mEventsTitleView.setText(title);

            long startDateInMills = mCursor.getLong(EventsActivity.INDEX_START_DATE);

            // Format date in millis to a relative string
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            eventsAdapterViewHolder.mEventsStartDateTextView
                    .setText(df.format("EEE, MMM d, ''yy", startDateInMills));

            String venue = mCursor.getString(EventsActivity.INDEX_VENUE);
            eventsAdapterViewHolder.mEventsVenueView.setText(venue);

            // Check if child text view is clicked and implement accordingly
            if (mEnquireTextViewListener != null) {
                eventsAdapterViewHolder.getEnquireTextView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCursor != null && mCursor.moveToPosition(position)) {
                            String enquireTitle = mCursor.getString(EventsActivity.INDEX_TITLE);
                            String enquireEmail = mCursor.getString(EventsActivity.INDEX_EMAIL);
                            mEnquireTextViewListener.onEnquireIsClick(v, enquireTitle, enquireEmail);
                        }
                    }
                });
            }

        } else if (mCursor != null && position == mCursor.getCount()) {
            EventsAdapterViewHolder footerHolder = eventsAdapterViewHolder;
            footerHolder.mFooterTextView.setText(R.string.detail_copyright);
        }
    }

    // This method simply returns the number of items to display.
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        // Get the orientation of the screen
        final int orientation = mContext.getResources().getConfiguration().orientation;
        if (mUseTopLayout && position == 0 || mUseTopLayout
                && position == 1
                && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return VIEW_TYPE_TOP;
        } else if (position == mCursor.getCount()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    // Swaps the cursor used by the EventsAdapter for its events data.
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    // Cache of the children views for a events list item.
    public class EventsAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mEventsTitleView;
        public final TextView mEventsStartDateTextView;
        public final TextView mEventsVenueView;
        public final TextView mFooterTextView;
        public final TextView mEnquireTextView;

        public EventsAdapterViewHolder(View view) {
            super(view);
            mEventsTitleView = view.findViewById(R.id.event_title);
            mEventsStartDateTextView = view.findViewById(R.id.events_date);
            mEventsVenueView = view.findViewById(R.id.events_venue);
            mFooterTextView = view.findViewById(R.id.footer_text);
            mEnquireTextView = view.findViewById(R.id.events_enquire);
        }

        public TextView getEnquireTextView() {
            return mEnquireTextView;
        }
    }
}
