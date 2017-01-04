/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.glucosio.android.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.activity.AddA1CActivity;
import org.glucosio.android.activity.AddCholesterolActivity;
import org.glucosio.android.activity.AddGlucoseActivity;
import org.glucosio.android.activity.AddKetoneActivity;
import org.glucosio.android.activity.AddPressureActivity;
import org.glucosio.android.activity.AddWeightActivity;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.adapter.HistoryAdapter;
import org.glucosio.android.listener.ItemClickSupport;
import org.glucosio.android.presenter.HistoryPresenter;
import org.glucosio.android.tools.FormatDateTime;

public class HistoryFragment extends Fragment {

    private static final String INTENT_EXTRA_PAGER = "pager";
    private static final String INTENT_EXTRA_EDITING_ID = "edit_id";
    private static final String INTENT_EXTRA_EDITING = "editing";
    private static final String INTENT_EXTRA_DROPDOWN = "history_dropdown";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private HistoryPresenter presenter;
    private LinearLayout glucoseLegend;
    private Spinner historySpinner;
    private BottomSheetDialog mBottomSheetDialog;
    private Boolean isToolbarScrolling = true;
    private int historyDropdownPosition = 0;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
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
        glucoseLegend = (LinearLayout) mFragmentView.findViewById(R.id.fragment_history_legend);

        // use a linear layout manager
        // Set array and adapter for graphSpinner
        String[] selectorArray = getActivity().getResources().getStringArray(R.array.fragment_history_selector);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, selectorArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        historySpinner.setAdapter(dataAdapter);

        final Context context = getActivity().getApplicationContext();
        historySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!presenter.isdbEmpty()) {
                    if (position != 0) {
                        glucoseLegend.setVisibility(View.GONE);
                    } else {
                        glucoseLegend.setVisibility(View.VISIBLE);
                    }
                    mAdapter = new HistoryAdapter(context, presenter, position);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    historyDropdownPosition = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                showBottomSheetDialog(v);
                return true;
            }
        });

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mRecyclerView.removeOnLayoutChangeListener(this);
                updateToolbarBehaviour();
            }
        });

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(INTENT_EXTRA_DROPDOWN)) {
                historySpinner.setSelection(extras.getInt(INTENT_EXTRA_DROPDOWN));
            }
        }
        return mFragmentView;
    }

    private void showBottomSheetDialog(final View itemView) {
        mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.fragment_history_bottom_sheet, null);
        LinearLayout edit = (LinearLayout) sheetView.findViewById(R.id.fragment_history_bottom_sheet_edit);
        LinearLayout delete = (LinearLayout) sheetView.findViewById(R.id.fragment_history_bottom_sheet_delete);
        final TextView idTextView = (TextView) itemView.findViewById(R.id.item_history_id);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int historyTypePosition = (int) historySpinner.getSelectedItemId();
                Intent intent;
                switch (historyTypePosition) {
                    // HAB1C
                    case 1:
                        intent = new Intent(getActivity(), AddA1CActivity.class);
                        break;
                    // Cholesterol
                    case 2:
                        intent = new Intent(getActivity(), AddCholesterolActivity.class);
                        break;
                    // Pressure
                    case 3:
                        intent = new Intent(getActivity(), AddPressureActivity.class);
                        break;
                    // Ketone
                    case 4:
                        intent = new Intent(getActivity(), AddKetoneActivity.class);
                        break;
                    // Weight
                    case 5:
                        intent = new Intent(getActivity(), AddWeightActivity.class);
                        break;
                    // Glucose
                    default:
                        intent = new Intent(getActivity(), AddGlucoseActivity.class);
                        break;
                }

                intent.putExtra(INTENT_EXTRA_EDITING_ID, Long.parseLong(idTextView.getText().toString()));
                intent.putExtra(INTENT_EXTRA_EDITING, true);
                intent.putExtra(INTENT_EXTRA_DROPDOWN, historyDropdownPosition);
                // History page is 1
                intent.putExtra(INTENT_EXTRA_PAGER, 1);
                startActivity(intent);
                mBottomSheetDialog.dismiss();
                getActivity().finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                TextView idTextView = (TextView) itemView.findViewById(R.id.item_history_id);
                final long idToDelete = Long.parseLong(idTextView.getText().toString());
                final CardView item = (CardView) itemView.findViewById(R.id.item_history);
                item.animate().alpha(0.0f).setDuration(2000);
                Snackbar.make(((MainActivity) getActivity()).getFabView(), R.string.fragment_history_snackbar_text, Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        switch (event) {
                            case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                // Do nothing, see Undo onClickListener
                                break;
                            case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                presenter.onDeleteClicked(idToDelete, historySpinner.getSelectedItemPosition());
                                break;
                            default:
                                break;
                        }
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

        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    public void updateToolbarBehaviour() {
        if (mAdapter != null) {
            if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.getItemCount() - 1) {
                isToolbarScrolling = false;
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).turnOffToolbarScrolling();
                }
            } else {
                if (!isToolbarScrolling) {
                    isToolbarScrolling = true;
                    ((MainActivity) getActivity()).turnOnToolbarScrolling();
                }
            }
        }
    }

    public String convertDate(String date) {
        FormatDateTime dateTime = new FormatDateTime(getActivity().getApplicationContext());
        return dateTime.convertDateTime(date);
    }

    public void notifyAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    public void reloadFragmentAdapter() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).reloadFragmentAdapter();
            ((MainActivity) getActivity()).checkIfEmptyLayout();
        }
    }

    public int getHistoryDropdownPosition() {
        return historyDropdownPosition;
    }
}
