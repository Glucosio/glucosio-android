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

package org.glucosio.android.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.HashMap;
import java.util.Map;
import org.glucosio.android.R;
import org.glucosio.android.presenter.ExternalViewPresenter;
import org.glucosio.android.tools.network.BasicNetworkConnectivity;
import org.glucosio.android.tools.network.GlucosioExternalLinks;

public class ExternalLinkActivity extends AppCompatActivity implements ExternalViewPresenter.View {

  private static final String TITLE_KEY = "TITLE_KEY";
  private static final String URL_KEY = "URL_KEY";
  private ExternalViewPresenter presenter;
  private WebView webView;
  private Map<String, Integer> toolbarTitle;

  public static void launch(@NonNull Context context, @NonNull String title, String url) {
    Intent intent = new Intent(context, ExternalLinkActivity.class);
    intent.putExtra(TITLE_KEY, title);
    intent.putExtra(URL_KEY, url);
    context.startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_licence);
    webView = (WebView) findViewById(R.id.webview_licence);
    init();
  }

  private void init() {
    initTitles();
    initPresenter();
    initView();
  }

  private void initView() {
    webView.setWebViewClient(new WebViewClient() {
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return true;
      }
    });
    setupToolbar();
  }

  private void initPresenter() {
    presenter = new ExternalViewPresenter(this, new BasicNetworkConnectivity(this));
    presenter.onViewCreated();
  }

  @Override public void setupToolbarTitle(String link) {
    if (toolbarTitle.containsKey(link)) {
      setToolbarTitle(getString(toolbarTitle.get(link)));
    }
  }

  @Override public String extractTitle() {
    return extractExtra(TITLE_KEY);
  }

  @Override public String extractUrl() {
    return extractExtra(URL_KEY);
  }

  private String extractExtra(String key) {
    String extra = "";
    if (getIntent().getExtras() != null) {
      extra = getIntent().getStringExtra(key);
    }
    return extra;
  }

  @Override public void loadExternalUrl(String url) {
    webView.loadUrl(url);
  }

  @Override public void showNoConnectionWarning() {
    AlertDialog dialog =
        new AlertDialog.Builder(this).setMessage(R.string.warning_internet_connection_required)
            .setCancelable(false)
            .setPositiveButton(R.string.mdtp_ok, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                ExternalLinkActivity.this.finish();
              }
            })
            .create();
    dialog.show();
  }

  private void setupToolbar() {
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
  }

  private void initTitles() {
    toolbarTitle = new HashMap<>();
    toolbarTitle.put(GlucosioExternalLinks.PRIVACY, R.string.preferences_privacy);
    toolbarTitle.put(GlucosioExternalLinks.LICENSES, R.string.preferences_licences_open);
    toolbarTitle.put(GlucosioExternalLinks.TERMS, R.string.preferences_terms);
  }

  private void setToolbarTitle(String string) {
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(string);
    }
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    finish();
    return true;
  }
}


