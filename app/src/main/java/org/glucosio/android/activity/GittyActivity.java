package org.glucosio.android.activity;

import android.os.Bundle;
import android.util.Base64;

import com.github.paolorotolo.gitty_reporter.GittyReporter;

import java.io.UnsupportedEncodingException;


public class GittyActivity extends GittyReporter {
    @Override
    public void init(Bundle savedInstanceState) {

        String token = "OGRlZDVkZjBjZGYzNzNjYTdiNzY2MmYwMGVmMTU5ZjcyMjYwMWQ1NA==";

        byte[] data1 = Base64.decode(token, Base64.DEFAULT);
        String decodedToken = token;
        try {
            decodedToken = new String(data1, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Set where Gitty will send issues.
        // (username, repository name);
        setTargetRepository("Glucosio", "andorid");

        // Set Auth token to open issues if user doesn't have a GitHub account
        // For example, you can register a bot account on GitHub that will open bugs for you.
        setGuestOAuth2Token(decodedToken);

        // OPTIONAL METHODS

        // Set if User can send bugs with his own GitHub account (default: true)
        // If false, Gitty will always use your Auth token
        enableUserGitHubLogin(true);

        // Set if Gitty can use your Auth token for users without a GitHub account (default: true)
        // If false, Gitty will redirect non registred users to github.com/join
        enableGuestGitHubLogin(true);
    }
}
