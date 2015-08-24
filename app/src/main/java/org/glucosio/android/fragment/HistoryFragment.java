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

        // Get database from MainActivity
        final DatabaseHandler db;
        db = ((MainActivity)getActivity()).getDatabase();

        ArrayList<Double> reading;
        ArrayList <Integer> type;
        ArrayList<String> datetime;

        reading = db.getGlucoseReadingAsArray();
        type = db.getGlucoseTypeAsArray();
        datetime = db.getGlucoseDateTimeAsArray();

        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.fragment_history_recycler_view);
        final FrameLayout parentLayout = (FrameLayout) mFragmentView.findViewById(R.id.fragment_history_parent);

        // Swipe to delete functionality
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();

                //Remove swiped item from list and notify the RecyclerView
                Snackbar
                        .make(parentLayout, R.string.fragment_history_snackbar_text, Snackbar.LENGTH_LONG)
                        .setAction(R.string.fragment_history_snackbar_action, readingUndoListener)
                        .show(); // Don’t forget to show!
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

        // specify an adapter (see also next example)
        mAdapter = new HistoryAdapter(super.getActivity().getApplicationContext(),reading, type, datetime);
        mRecyclerView.setAdapter(mAdapter);

        return mFragmentView;
    }

    // SnackBar undo listener
    View.OnClickListener readingUndoListener = new View.OnClickListener() {
        public void onClick(View v) {

        }
    };
}
