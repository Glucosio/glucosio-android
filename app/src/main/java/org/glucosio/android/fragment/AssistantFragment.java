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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.activity.A1cCalculatorActivity;
import org.glucosio.android.activity.AddGlucoseActivity;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.adapter.AssistantAdapter;
import org.glucosio.android.object.ActionTip;
import org.glucosio.android.presenter.AssistantPresenter;

import java.util.ArrayList;

public class AssistantFragment extends Fragment {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private RecyclerView tipsRecycler;
    private AssistantAdapter adapter;
    private LinearLayout archivedButton;
    private LinearLayout archivedDismissButton;
    private ArrayList<ActionTip> actionTips;
    private String[] actionTipTitles;
    private String[] actionTipDescriptions;
    private String[] actionTipActions;

    public AssistantFragment() {
        // Required empty public constructor
    }

    public static AssistantFragment newInstance() {
        AssistantFragment fragment = new AssistantFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AssistantPresenter presenter = new AssistantPresenter(this);
        actionTips = new ArrayList<>();

        actionTipTitles = getResources().getStringArray(R.array.assistant_titles);
        actionTipDescriptions = getResources().getStringArray(R.array.assistant_descriptions);
        actionTipActions = getResources().getStringArray(R.array.assistant_actions);
        popolateWithNewTips();

        View mView = inflater.inflate(R.layout.fragment_assistant, container, false);
        tipsRecycler = (RecyclerView) mView.findViewById(R.id.fragment_tips_recyclerview);
        archivedButton = (LinearLayout) mView.findViewById(R.id.fragment_assistant_archived);
        archivedDismissButton = (LinearLayout) mView.findViewById(R.id.fragment_assistant_archived_dismiss);
        adapter = new AssistantAdapter(getActivity().getApplicationContext(), presenter, actionTips);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        tipsRecycler.setLayoutManager(llm);
        tipsRecycler.setAdapter(adapter);
        tipsRecycler.setHasFixedSize(false);

        // Swipe to remove functionality
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (archivedDismissButton.getVisibility() == View.VISIBLE) {
                    // If we're in archive, restore tips
                    TextView title = (TextView) viewHolder.itemView.findViewById(R.id.fragment_assistant_item_title);
                    removePreference(title.getText().toString());

                    int position = viewHolder.getAdapterPosition();
                    actionTips.remove(position);
                    adapter.notifyDataSetChanged();
                } else {
                    // Else archive them
                    TextView title = (TextView) viewHolder.itemView.findViewById(R.id.fragment_assistant_item_title);
                    addPreference(title.getText().toString());

                    int position = viewHolder.getAdapterPosition();
                    actionTips.remove(position);
                    adapter.notifyDataSetChanged();

                    ((MainActivity) getActivity()).reloadFragmentAdapter();
                }
            }
        };

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(tipsRecycler);

        // If there aren't dismissed tips, don't show archive button
        if (actionTipTitles.length == adapter.getItemCount()) {
            archivedButton.setVisibility(View.GONE);
        }

        archivedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popolateWithArchivedTips();
                adapter.notifyDataSetChanged();
                tipsRecycler.swapAdapter(adapter, false);
                archivedDismissButton.setVisibility(View.VISIBLE);
                final Animation slide = new TranslateAnimation(0, 0, 0, 200);
                slide.setDuration(500);

                archivedButton.startAnimation(slide);
                archivedButton.setVisibility(View.GONE);
            }
        });

        archivedDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popolateWithNewTips();
                adapter.notifyDataSetChanged();
                tipsRecycler.swapAdapter(adapter, false);
                archivedDismissButton.setVisibility(View.GONE);
                archivedButton.setVisibility(View.VISIBLE);

                ((MainActivity) getActivity()).reloadFragmentAdapter();
            }
        });

        return mView;
    }

    private void popolateWithNewTips() {
        actionTips.clear();
        for (int i = 0; i < actionTipTitles.length; i++) {
            String actionTipTitle = actionTipTitles[i];
            String actionTipDescription = actionTipDescriptions[i];
            String actionTipAction = actionTipActions[i];

            ActionTip actionTip = new ActionTip();
            actionTip.setTipTitle(actionTipTitle);
            actionTip.setTipDescription(actionTipDescription);
            actionTip.setTipAction(actionTipAction);

            Boolean value = sharedPref.getBoolean(actionTipTitle, false);
            if (!value) {
                actionTips.add(actionTip);
            }
        }
    }

    private void popolateWithArchivedTips() {
        actionTips.clear();
        for (int i = 0; i < actionTipTitles.length; i++) {
            String actionTipTitle = actionTipTitles[i];
            String actionTipDescription = actionTipDescriptions[i];
            String actionTipAction = actionTipActions[i];

            ActionTip actionTip = new ActionTip();
            actionTip.setTipTitle(actionTipTitle);
            actionTip.setTipDescription(actionTipDescription);
            actionTip.setTipAction(actionTipAction);

            Boolean value = sharedPref.getBoolean(actionTipTitle, false);
            if (value) {
                actionTips.add(actionTip);
            }
        }
    }

    private void addPreference(String key) {
        editor.putBoolean(key, true);
        editor.commit();
    }

    private void removePreference(String key) {
        editor.putBoolean(key, false);
        editor.commit();
    }

    public void addReading() {
        Intent intent = new Intent(getActivity(), AddGlucoseActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void startExportActivity() {
        ((MainActivity) getActivity()).showExportCsvDialog();
    }

    public void startA1CCalculatorActivity() {
        Intent intent = new Intent(getActivity(), A1cCalculatorActivity.class);
        startActivity(intent);
    }

    public void openLiveChat() {
        ((MainActivity) getActivity()).openSupportDialog();
    }
}