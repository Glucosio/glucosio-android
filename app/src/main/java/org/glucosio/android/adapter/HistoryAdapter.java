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
import org.glucosio.android.presenter.HistoryPresenter;
import org.glucosio.android.tools.ReadingTools;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context mContext;
    private HistoryPresenter presenter;

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
    public HistoryAdapter(Context context, HistoryPresenter presenter) {
        this.mContext = context;
        this.presenter = presenter;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history_item, parent, false);

        loadDatabase();

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
        Collections.addAll(presenter.getReading());
        Collections.addAll(presenter.getDatetime());
        Collections.addAll(presenter.getType());
        Collections.addAll(presenter.getId());

        // if (db.getUser(1).getUnitMeasurement == mmolL){
        //    readingTextView.setText(convert.toMmolL(reading.get(position)) + "mmol/l");
        //}

        idTextView.setText(presenter.getId().get(position).toString());
        readingTextView.setText(presenter.getReading().get(position).toString());
        datetimeTextView.setText(presenter.convertDate(presenter.getDatetime().get(position)));
        typeTextView.setText(typeToString(presenter.getType().get(position)));
    }

    public String typeToString(int typeInt){
       //TODO refactor this ugly mess
        String typeString = "";
        if (typeInt == 0) {
            typeString = mContext.getString(R.string.dialog_add_type_1);
        } else if (typeInt == 1) {
            typeString = mContext.getString(R.string.dialog_add_type_2);
        } else if (typeInt == 2) {
            typeString = mContext.getString(R.string.dialog_add_type_3);
        } else if (typeInt == 3) {
            typeString = mContext.getString(R.string.dialog_add_type_4);
        } else if (typeInt == 4) {
            typeString = mContext.getString(R.string.dialog_add_type_5);
        } else if (typeInt == 5) {
            typeString = mContext.getString(R.string.dialog_add_type_6);
        } else if (typeInt == 6) {
            typeString = mContext.getString(R.string.dialog_add_type_7);
        } else if (typeInt == 7) {
            typeString = mContext.getString(R.string.dialog_add_type_8);
        } else if (typeInt == 8) {
            typeString = mContext.getString(R.string.dialog_add_type_9);
        }
        return typeString;
    }

    private void loadDatabase(){
        // Get database from MainActivity
        presenter.loadDatabase();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return presenter.getReading().size();
    }
}