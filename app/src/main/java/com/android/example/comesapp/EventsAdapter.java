package com.android.example.comesapp;

import android.content.Context;
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
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_FOOTER = 1;


    // An on-click handler that we've defined to make it easy for an Activity
    // to interface with our RecyclerView
    private final EventsAdapterOnClickHandler mClickHandler;

    // The interface that receives onClick messages.
    public interface EventsAdapterOnClickHandler {
        void onClick(String eventsItem);
    }

    // Creates a NewsAdapter.
    public EventsAdapter(Context context, EventsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
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
        } else
            throw new IllegalArgumentException("Invalid view type, value of " + viewType);
    }

    // OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(EventsAdapterViewHolder eventsAdapterViewHolder, int position) {

        // Move the cursor to the appropriate position
        if (mCursor != null && mCursor.moveToPosition(position)) {

            String title = mCursor.getString(EventsActivity.INDEX_TITLE);
            eventsAdapterViewHolder.mEventsTitleView.setText(title);

            String venue = mCursor.getString(EventsActivity.INDEX_VENUE);
            eventsAdapterViewHolder.mEventsVenueView.setText(venue);

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
        if (position == mCursor.getCount()) {
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
    public class EventsAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        public final TextView mEventsTitleView;
        public final TextView mEventsVenueView;
        public final TextView mFooterTextView;

        public EventsAdapterViewHolder(View view) {
            super(view);
            mEventsTitleView = view.findViewById(R.id.events_title);
            mEventsVenueView = view.findViewById(R.id.events_venue);
            mFooterTextView = view.findViewById(R.id.footer_text);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String eventsItem = mEventsTitleView.getText().toString();
            mClickHandler.onClick(eventsItem);
        }
    }
}
