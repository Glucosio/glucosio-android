package org.glucosio.android.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.adapter.HistoryAdapter;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.listener.RecyclerItemClickListener;
import org.glucosio.android.presenter.HistoryPresenter;

public class HistoryFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    GlucoseReading readingToRestore;
    HistoryPresenter presenter;

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
        presenter = new HistoryPresenter(this);
        presenter.loadDatabase();

        if (!presenter.isdbEmpty()) {
            mFragmentView = inflater.inflate(R.layout.fragment_history, container, false);

            mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.fragment_history_recycler_view);
            mAdapter = new HistoryAdapter(super.getActivity().getApplicationContext(), presenter);

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
                                final int idToEdit = Integer.parseInt(idTextView.getText().toString());
                                ((MainActivity)getActivity()).showEditDialog(idToEdit);
                            } else {
                                // DELETE
                                TextView idTextView = (TextView) mRecyclerView.getChildAt(position).findViewById(R.id.item_history_id);
                                final int idToDelete = Integer.parseInt(idTextView.getText().toString());
                                presenter.onDeleteClicked(idToDelete);
                                Snackbar.make(((MainActivity)getActivity()).getFabView(), R.string.fragment_history_snackbar_text, Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        switch (event) {
                                            case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                                // Do nothing, see Undo onClickListener
                                                break;
                                            case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                                presenter.deleteReading(idToDelete);
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
                                        presenter.onUndoClicked();
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

    public void notifyAdapter(){
        mAdapter.notifyDataSetChanged();
    }

    public void reloadFragmentAdapter(){
        ((MainActivity)getActivity()).reloadFragmentAdapter();
    }
}
