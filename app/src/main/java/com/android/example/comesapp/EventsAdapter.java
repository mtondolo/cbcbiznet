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

    private Cursor mCursor;

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

        // Inflate the list item xml into a view
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.event_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        // Return a new PolicyAdapterViewHolder with the above view passed in as a parameter
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new EventsAdapterViewHolder(view);
    }

    // OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(EventsAdapterViewHolder eventsAdapterViewHolder, int position) {

        // Move the cursor to the appropriate position
        mCursor.moveToPosition(position);

        /*******************
         * Events Item *
         *******************/

        // Read title and venue from the cursor
        String title = mCursor.getString(EventsActivity.INDEX_TITLE);
        String venue = mCursor.getString(EventsActivity.INDEX_VENUE);

        // Stick the title and venue to their views
        eventsAdapterViewHolder.mEventsTitleView.setText(title);
        eventsAdapterViewHolder.mEventsVenueView.setText(venue);

    }

    // This method simply returns the number of items to display.
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
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

        public EventsAdapterViewHolder(View view) {
            super(view);
            mEventsTitleView = (TextView) view.findViewById(R.id.events_title);
            mEventsVenueView = (TextView) view.findViewById(R.id.events_venue);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String eventsItem = mEventsTitleView.getText().toString();
            mClickHandler.onClick(eventsItem);
        }
    }
}
