package org.glucosio.android.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.glucosio.android.R;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.adapter.AssistantAdapter;
import org.glucosio.android.presenter.AssistantPresenter;

public class AssistantFragment extends Fragment {

    private static final int REQUEST_INVITE = 0;

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
        // dB = ((MainActivity)getActivity()).getDatabase();
        // tipsManager = new TipsManager(getActivity(), dB.getUser(1).get_age());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        presenter = new AssistantPresenter(this);

        View mView = inflater.inflate(R.layout.fragment_assistant, container, false);
        tipsRecycler = (RecyclerView) mView.findViewById(R.id.fragment_tips_recyclerview);
        adapter = new AssistantAdapter(getActivity().getApplicationContext(), presenter);

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
                // TODO: Remove swiped item from list and notify the RecyclerView
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(tipsRecycler);

        return mView;
    }

    public void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        getActivity().startActivityForResult(intent, REQUEST_INVITE);
    }

    public void addReading(){
        ((MainActivity)getActivity()).showAddDialog();
    }

    public void openGitty(){
        ((MainActivity)getActivity()).startGittyReporter();
    }

    private boolean isPlayServicesConfigured() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if(status == ConnectionResult.SUCCESS)
            return true;
        else {
            Log.d("STATUS", "Error connecting with Google Play services. Code: " + String.valueOf(status));
            return false;
        }
    }
}