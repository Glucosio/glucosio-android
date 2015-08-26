package org.glucosio.android.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.glucosio.android.R;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.adapter.HistoryAdapter;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.tools.DividerItemDecoration;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    DatabaseHandler db;

    ArrayList<Double> id;
    ArrayList<Double> reading;
    ArrayList <Integer> type;
    ArrayList<String> datetime;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();


        return fragment;
    }

    public HistoryFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mFragmentView = inflater.inflate(R.layout.fragment_history, container, false);

        loadDatabase();

        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.fragment_history_recycler_view);
        final FrameLayout parentLayout = (FrameLayout) mFragmentView.findViewById(R.id.fragment_history_parent);
        mAdapter = new HistoryAdapter(super.getActivity().getApplicationContext(),id, reading, type, datetime);

        // Swipe to delete functionality
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                TextView idTextView = (TextView) mRecyclerView.getChildAt(position).findViewById(R.id.item_history_id);
                final double idToDelete = Double.parseDouble(idTextView.getText().toString());

                Snackbar.make(((MainActivity)getActivity()).getFabView(), R.string.fragment_history_snackbar_text, Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        switch (event) {
                            case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                // Do nothing, see Undo onClickListener
                                break;
                            case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                removeReadingFromDb(db.getGlucoseReadings("id = " + idToDelete).get(0));
                                break;
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        // Do nothing
                    }
                }).setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // On Undo pressed, reload the adapter. The reading is still present in db;
                        mAdapter.notifyDataSetChanged();
                    }
                }).show();
                // Remove item just from UI
                mAdapter.notifyItemRemoved(position);
            }


        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(super.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mAdapter);

        return mFragmentView;
    }

    private void removeReadingFromDb(GlucoseReading gReading) {
        db.deleteGlucoseReadings(gReading);
        loadDatabase();
    }

    private void loadDatabase(){
        // Get database from MainActivity
        db = ((MainActivity)getActivity()).getDatabase();

        this.id = db.getGlucoseIdAsArray();
        this.reading = db.getGlucoseReadingAsArray();
        this.type = db.getGlucoseTypeAsArray();
        this.datetime = db.getGlucoseDateTimeAsArray();
    }
}
