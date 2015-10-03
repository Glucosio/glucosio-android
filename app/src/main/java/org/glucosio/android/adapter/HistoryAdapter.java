package org.glucosio.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.glucosio.android.R;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.presenter.HistoryPresenter;
import org.glucosio.android.tools.FormatDateTime;
import org.glucosio.android.tools.GlucoseConverter;
import org.glucosio.android.tools.GlucoseRanges;
import org.glucosio.android.tools.ReadingTools;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context mContext;
    private HistoryPresenter presenter;
    private GlucoseConverter converter;

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
        converter = new GlucoseConverter();

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

        idTextView.setText(presenter.getId().get(position).toString());

        GlucoseRanges ranges = new GlucoseRanges();
        String color = ranges.colorFromRange(presenter.getReading().get(position));

        if (presenter.getUnitMeasuerement().equals("mg/dL")) {
            readingTextView.setText(presenter.getReading().get(position).toString() + " mg/dL");
        } else {
            readingTextView.setText(converter.toMmolL(Double.parseDouble(presenter.getReading().get(position).toString())) + " mmol/L");
        }

        switch (color) {
            case "green":
                readingTextView.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "red":
                readingTextView.setTextColor(Color.parseColor("#F44336"));
                break;
            default:
                readingTextView.setTextColor(Color.parseColor("#9C27B0"));
                break;
        }

        datetimeTextView.setText(presenter.convertDate(presenter.getDatetime().get(position)));
        typeTextView.setText(presenter.getType().get(position));
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