package org.glucosio.android.presenter;

import org.glucosio.android.activity.HelloActivity;
import org.glucosio.android.activity.MainActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.fragment.AssistantFragment;

public class AssistantPresenter {
    DatabaseHandler dB;
    AssistantFragment fragment;


    public AssistantPresenter(AssistantFragment assistantFragment) {
        this.fragment= assistantFragment;
        dB = new DatabaseHandler();
    }

    public void addReading() {
        fragment.addReading();
    }

    public void openGitty() {
        fragment.openGitty();
    }
}
