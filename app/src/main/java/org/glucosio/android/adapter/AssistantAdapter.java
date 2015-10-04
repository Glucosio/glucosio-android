package org.glucosio.android.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.glucosio.android.R;
import java.util.ArrayList;

public class AssistantAdapter extends RecyclerView.Adapter<AssistantAdapter.ViewHolder> {
    private Context mContext;
    private String[] actionTipTitles;
    private String[] actionTipDescriptions;
    private String[] actionTipActions;


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
    public AssistantAdapter(Context context) {
        this.mContext = context;
        this.actionTipTitles = context.getResources().getStringArray(R.array.assistant_titles);
        this.actionTipDescriptions = context.getResources().getStringArray(R.array.assistant_descriptions);
        this.actionTipActions = context.getResources().getStringArray(R.array.assistant_actions);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AssistantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_assistant_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView actionTipTitle = (TextView) holder.mView.findViewById(R.id.fragment_assistant_item_title);
        TextView actionTipDescription = (TextView) holder.mView.findViewById(R.id.fragment_assistant_item_description);
        AppCompatButton actionTipAction = (AppCompatButton) holder.mView.findViewById(R.id.fragment_assistant_item_action);
        actionTipTitle.setText(actionTipTitles[position]);
        actionTipDescription.setText(actionTipDescriptions[position]);
        actionTipAction.setText(actionTipActions[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return actionTipTitles.length;
    }
}
