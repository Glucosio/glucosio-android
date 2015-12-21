package org.glucosio.android.presenter;

import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.fragment.AssistantFragment;

public class AssistantPresenter {
    private DatabaseHandler dB;
    private AssistantFragment fragment;


    public AssistantPresenter(AssistantFragment assistantFragment) {
        this.fragment= assistantFragment;
        dB = new DatabaseHandler(assistantFragment.getContext());
    }

    public void addReading() {
        fragment.addReading();
    }

    public void openGitty() {
        fragment.openGitty();
    }

    public void startExportActivity() {
        fragment.startExportActivity();
    }
}
