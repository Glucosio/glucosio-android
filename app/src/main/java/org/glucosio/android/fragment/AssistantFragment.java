package org.glucosio.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.glucosio.android.ActionTip;
import org.glucosio.android.R;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.adapter.AssistantAdapter;
import org.glucosio.android.presenter.AssistantPresenter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AssistantFragment extends Fragment {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private RecyclerView tipsRecycler;
    private AssistantAdapter adapter;
    private AssistantPresenter presenter;

    public static AssistantFragment newInstance() {
        AssistantFragment fragment = new AssistantFragment();

        return fragment;
    }

    public AssistantFragment() {
        // Required empty public constructor
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
        presenter = new AssistantPresenter(this);
        final ArrayList<ActionTip> actionTips = new ArrayList<>();
        String[] actionTipTitles = getResources().getStringArray(R.array.assistant_titles);
        String[] actionTipDescriptions = getResources().getStringArray(R.array.assistant_descriptions);
        String[] actionTipActions = getResources().getStringArray(R.array.assistant_actions);

        for (int i=0; i<actionTipTitles.length; i++){
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

        View mView = inflater.inflate(R.layout.fragment_assistant, container, false);
        tipsRecycler = (RecyclerView) mView.findViewById(R.id.fragment_tips_recyclerview);
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
                TextView title = (TextView) viewHolder.itemView.findViewById(R.id.fragment_assistant_item_title);
                addPreference(title.getText().toString());

                int position = viewHolder.getAdapterPosition();
                actionTips.remove(position);
                adapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(tipsRecycler);

        return mView;
    }

    private void addPreference(String key){
        editor.putBoolean(key, true);
        editor.commit();
    }

    public void addReading(){
        ((MainActivity)getActivity()).showAddDialog();
    }

    public void openGitty(){
        ((MainActivity)getActivity()).startGittyReporter();
    }
}