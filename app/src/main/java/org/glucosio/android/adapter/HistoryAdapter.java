package org.glucosio.android.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Double> reading;
    private ArrayList <Integer> type;
    private ArrayList<String> datetime;

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
    public HistoryAdapter(Context context, ArrayList<Double> gReading, ArrayList<Integer> gType, ArrayList<String> gDatetime) {
        this.mContext = context;
        this.reading = gReading;
        this.type = gType;
        this.datetime = gDatetime;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
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

        readingTextView.setText(reading.get(position).toString());
        datetimeTextView.setText(datetime.get(position));
        typeTextView.setText(typeToString(type.get(position)));
    }

    private String typeToString(int typeInt){
        String typeString = "";
        if (typeInt == 1) {
            typeString = mContext.getString(R.string.dialog_add_type_1);

        } else if (typeInt == 2) {
            typeString = mContext.getString(R.string.dialog_add_type_2);

        } else if (typeInt == 3) {
            typeString = mContext.getString(R.string.dialog_add_type_3);

        } else if (typeInt == 4) {
            typeString = mContext.getString(R.string.dialog_add_type_4);

        } else if (typeInt == 5) {
            typeString = mContext.getString(R.string.dialog_add_type_5);

        } else if (typeInt == 6) {
            typeString = mContext.getString(R.string.dialog_add_type_6);
        }
        return  typeString;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reading.size();
    }
}