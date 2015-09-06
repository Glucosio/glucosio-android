package org.glucosio.android.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import org.glucosio.android.listener.RecyclerItemClickListener;
import org.glucosio.android.tools.DividerItemDecoration;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    DatabaseHandler db;

    ArrayList<Integer> id;
    ArrayList<Integer> reading;
    ArrayList <Integer> type;
    ArrayList<String> datetime;
    GlucoseReading readingToRestore;

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

        View mFragmentView;
        db = ((MainActivity)getActivity()).getDatabase();


        if (db.getGlucoseReadings().size() != 0) {
            mFragmentView = inflater.inflate(R.layout.fragment_history, container, false);

            loadDatabase();

            mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.fragment_history_recycler_view);
            mAdapter = new HistoryAdapter(super.getActivity().getApplicationContext(),id, reading, type, datetime);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(false);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(super.getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position)
                {
                    // Do nothing
                }

                @Override
                public void onItemLongClick(View view, final int position)
                {
                    CharSequence colors[] = new CharSequence[] {getResources().getString(R.string.dialog_edit), getResources().getString(R.string.dialog_delete)};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0){
                                // EDIT
                                TextView idTextView = (TextView) mRecyclerView.getChildAt(position).findViewById(R.id.item_history_id);
                                final double idToEdit = Double.parseDouble(idTextView.getText().toString());
                                ((MainActivity)getActivity()).showEditDialog(idToEdit);
                            } else {
                                // DELETE
                                TextView idTextView = (TextView) mRecyclerView.getChildAt(position).findViewById(R.id.item_history_id);
                                final double idToDelete = Double.parseDouble(idTextView.getText().toString());
                                readingToRestore = db.getGlucoseReadings("id = " + idToDelete).get(0);
                                removeReadingFromDb(db.getGlucoseReadings("id = " + idToDelete).get(0));

                                mAdapter.notifyDataSetChanged();

                                Snackbar.make(((MainActivity)getActivity()).getFabView(), R.string.fragment_history_snackbar_text, Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        switch (event) {
                                            case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                                // Do nothing, see Undo onClickListener
                                                break;
                                            case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                                // Do Nothing
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
                                        db.addGlucoseReading(readingToRestore);
                                        ((MainActivity)getActivity()).reloadFragmentAdapter();
                                    }
                                }).setActionTextColor(getResources().getColor(R.color.glucosio_accent)).show();
                            }
                        }
                    });
                    builder.show();
                }
            }));

        } else {
            mFragmentView = inflater.inflate(R.layout.fragment_empty, container, false);
        }

        return mFragmentView;
    }

    private void removeReadingFromDb(GlucoseReading gReading) {
        db.deleteGlucoseReadings(gReading);
        ((MainActivity)getActivity()).reloadFragmentAdapter();
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
