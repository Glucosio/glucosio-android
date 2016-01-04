package org.glucosio.android.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.adapter.HistoryAdapter;
import org.glucosio.android.listener.RecyclerItemClickListener;
import org.glucosio.android.presenter.HistoryPresenter;
import org.glucosio.android.tools.FormatDateTime;

public class HistoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private HistoryPresenter presenter;
    private Spinner historySpinner;
    private Boolean isToolbarScrolling = true;

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
        mFragmentView = inflater.inflate(R.layout.fragment_history, container, false);

        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.fragment_history_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mLayoutManager = new LinearLayoutManager(super.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        historySpinner = (Spinner) mFragmentView.findViewById(R.id.history_spinner);
        // use a linear layout manager
        // Set array and adapter for graphSpinner
        String[] selectorArray = getActivity().getResources().getStringArray(R.array.fragment_history_selector);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, selectorArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        historySpinner.setAdapter(dataAdapter);

        final Context context = getActivity().getApplicationContext();
        final int metricId = historySpinner.getSelectedItemPosition();
        historySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!presenter.isdbEmpty()) {
                    mAdapter = new HistoryAdapter(context, presenter, metricId);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Do nothing
            }

            @Override
            public void onItemLongClick(final View view, final int position) {
                CharSequence items[] = new CharSequence[]{getResources().getString(R.string.dialog_delete)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // DELETE
                        TextView idTextView = (TextView) view.findViewById(R.id.item_history_id);
                        final int idToDelete = Integer.parseInt(idTextView.getText().toString());
                        final CardView item = (CardView) view.findViewById(R.id.item_history);
                        item.animate().alpha(0.0f).setDuration(2000);
                        Snackbar.make(((MainActivity) getActivity()).getFabView(), R.string.fragment_history_snackbar_text, Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                switch (event) {
                                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                        // Do nothing, see Undo onClickListener
                                        break;
                                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                        presenter.onDeleteClicked(idToDelete);
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
                                item.clearAnimation();
                                item.setAlpha(1.0f);
                                mAdapter.notifyDataSetChanged();
                            }
                        }).setActionTextColor(getResources().getColor(R.color.glucosio_accent)).show();
                    }
                });
                builder.show();
            }
        }));

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mRecyclerView.removeOnLayoutChangeListener(this);
                updateToolbarBehaviour();
            }
        });

        return mFragmentView;
    }

    public void updateToolbarBehaviour(){
        if (mAdapter!=null) {
            if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.getItemCount() - 1) {
                isToolbarScrolling = false;
                ((MainActivity) getActivity()).turnOffToolbarScrolling();
            } else {
                if (!isToolbarScrolling) {
                    isToolbarScrolling = true;
                    ((MainActivity) getActivity()).turnOnToolbarScrolling();
                }
            }
        }
    }

    public String convertDate(String date){
        FormatDateTime dateTime = new FormatDateTime(getActivity().getApplicationContext());
        return dateTime.convertDate(date);
    }

    public void notifyAdapter(){
        mAdapter.notifyDataSetChanged();
    }

    public void reloadFragmentAdapter(){
        ((MainActivity)getActivity()).reloadFragmentAdapter();
        ((MainActivity)getActivity()).checkIfEmptyLayout();
    }
}
