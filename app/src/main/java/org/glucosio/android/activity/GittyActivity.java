package org.glucosio.android.activity;

import android.os.Bundle;

import com.github.paolorotolo.gitty_reporter.GittyReporter;


public class GittyActivity extends GittyReporter {
    @Override
    public void init(Bundle savedInstanceState) {

        // Set where Gitty will send issues.
        // (username, repository name);
        setTargetRepository("Glucosio", "andorid");

        // Set Auth token to open issues if user doesn't have a GitHub account
        // For example, you can register a bot account on GitHub that will open bugs for you.
        setGuestOAuth2Token("f070c9ac4abfa8450706e8dc28320da96ecba36e");


        // OPTIONAL METHODS

        // Set if User can send bugs with his own GitHub account (default: true)
        // If false, Gitty will always use your Auth token
        enableUserGitHubLogin(true);

        // Set if Gitty can use your Auth token for users without a GitHub account (default: true)
        // If false, Gitty will redirect non registred users to github.com/join
        enableGuestGitHubLogin(true);
    }
}
