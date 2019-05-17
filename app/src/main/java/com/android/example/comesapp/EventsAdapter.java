package com.android.example.comesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsAdapterViewHolder> {

    private String[] mEventsData;

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

        // Display the summary that we created above
        String event = title + " - " + venue;

        // Set the text of the TextView to the event for this list item's position
        String eventsItem = mEventsData[position];
        eventsAdapterViewHolder.mEventsTextView.setText(eventsItem);

        eventsAdapterViewHolder.mEventsTextView.setText(event);

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

        public final TextView mEventsTextView;

        public EventsAdapterViewHolder(View view) {
            super(view);
            mEventsTextView = (TextView) view.findViewById(R.id.tv_events_data);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String eventsItem = mEventsData[adapterPosition];
            mClickHandler.onClick(eventsItem);
        }
    }

    // This method is used to set the event on an EventsAdapter
    // if we've already created one.
    public void setEventsData(String[] eventsData) {
        mEventsData = eventsData;
        notifyDataSetChanged();
    }
}
