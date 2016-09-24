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

package org.glucosio.android.presenter;

import java.util.HashMap;
import java.util.Map;
import org.glucosio.android.tools.network.GlucosioExternalLinks;
import org.glucosio.android.tools.network.NetworkConnectivity;

public class ExternalViewPresenter {
  private final String TERMS = "terms";
  private final String OPEN_SOURCE = "open_source";
  private final String PRIVACY = "privacy";
  private ExternalViewPresenter.View view;
  private NetworkConnectivity network;
  private Map<String, String> urlMatchings;

  public ExternalViewPresenter(View view, NetworkConnectivity network) {
    this.view = view;
    this.network = network;
    initUrls();
  }

  private void initUrls() {
    urlMatchings = new HashMap<>(3);
    urlMatchings.put(PRIVACY, GlucosioExternalLinks.PRIVACY);
    urlMatchings.put(OPEN_SOURCE, GlucosioExternalLinks.LICENSES);
    urlMatchings.put(TERMS, GlucosioExternalLinks.TERMS);
  }

  public void onViewCreated() {
    if (network.isConnected()) {
      String action = view.extractAction();
      String url = matchUrlToAction(action);
      view.setupToolbarTitle(url);
      view.loadExternalUrl(url);
    } else {
      view.showNoConnectionWarning();
    }
  }

  private String matchUrlToAction(String action) {
    String url = GlucosioExternalLinks.TERMS;
    if (action != null && urlMatchings.containsKey(action)) {
      url = urlMatchings.get(action);
    }
    return url;
  }

  public interface View {
    void setupToolbarTitle(String link);

    String extractAction();

    void loadExternalUrl(String url);

    void showNoConnectionWarning();
  }
}
