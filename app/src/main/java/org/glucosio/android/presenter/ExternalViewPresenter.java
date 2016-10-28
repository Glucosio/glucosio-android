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

import android.text.TextUtils;
import org.glucosio.android.tools.network.NetworkConnectivity;

public class ExternalViewPresenter {

  private ExternalViewPresenter.View view;
  private NetworkConnectivity network;

  public ExternalViewPresenter(View view, NetworkConnectivity network) {
    this.view = view;
    this.network = network;
  }

  public void onViewCreated() {
    if (network.isConnected()) {
      String title = view.extractTitle();
      String url = view.extractUrl();
      parametersPrecondition(title, url);
      view.setupToolbarTitle(title);
      view.loadExternalUrl(url);
    } else {
      view.showNoConnectionWarning();
    }
  }

  private void parametersPrecondition(String title, String url) {
    if ((invalidParam(title)) || invalidParam(url)) {
      throw new IllegalArgumentException("Invalid arguments: need URL and TITLE");
    }
  }

  private boolean invalidParam(String url) {
    return TextUtils.isEmpty(url);
  }

  public interface View {
    void setupToolbarTitle(String link);

    String extractTitle();

    String extractUrl();

    void loadExternalUrl(String url);

    void showNoConnectionWarning();
  }
}
