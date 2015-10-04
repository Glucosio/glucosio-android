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
import org.glucosio.android.adapter.AssistantAdapter;
import org.glucosio.android.presenter.AssistantPresenter;

public class AssistantFragment extends Fragment {

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

        return mView;
    }

    public void addReading(){
        ((MainActivity)getActivity()).showAddDialog();
    }

    public void openGitty(){
        ((MainActivity)getActivity()).startGittyReporter();
    }
}