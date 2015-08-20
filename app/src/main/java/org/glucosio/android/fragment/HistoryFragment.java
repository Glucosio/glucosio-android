package org.glucosio.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.glucosio.android.R;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.adapter.HistoryAdapter;
import org.glucosio.android.db.DatabaseHandler;

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
        return inflater.inflate(R.layout.fragment_history, container, false);

        // Get database from MainActivity
        DatabaseHandler db;
        db = ((MainActivity)getActivity()).getDatabase();
        ArrayList<Double> reading;
        ArrayList <Integer> type;
        ArrayList<String> datetime;

        reading = db.getGlucoseReadingAsArray();
        type = db.getGlucoseTypeAsArray();
        datetime = db.getGlucoseDateTimeAsArray();

        mRecyclerView = (RecyclerView) container.findViewById(R.id.fragment_history_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(super.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new HistoryAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }
}
