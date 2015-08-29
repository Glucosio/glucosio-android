package org.glucosio.android.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.tools.ReadingTools;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Double> id;
    private ArrayList<Double> reading;
    private ArrayList <Integer> type;
    private ArrayList<String> datetime;
    private ReadingTools rTools;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryAdapter(Context context, ArrayList<Double> gId, ArrayList<Double> gReading, ArrayList<Integer> gType, ArrayList<String> gDatetime) {
        this.mContext = context;
        this.id = gId;
        this.reading = gReading;
        this.type = gType;
        this.datetime = gDatetime;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        rTools = new ReadingTools(mContext);

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView readingTextView = (TextView) holder.mView.findViewById(R.id.item_history_reading);
        TextView datetimeTextView = (TextView) holder.mView.findViewById(R.id.item_history_time);
        TextView typeTextView = (TextView) holder.mView.findViewById(R.id.item_history_type);
        TextView idTextView = (TextView) holder.mView.findViewById(R.id.item_history_id);

        // Reverse ListView order to display latest items first
        Collections.addAll(reading);
        Collections.addAll(datetime);
        Collections.addAll(type);
        Collections.addAll(id);

        idTextView.setText(id.get(position).toString());
        readingTextView.setText(reading.get(position).toString());
        datetimeTextView.setText(datetime.get(position));
        typeTextView.setText(rTools.typeToString(type.get(position)));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reading.size();
    }
}