package org.glucosio.android.presenter;

import org.glucosio.android.tools.network.GlucosioExternalLinks;
import org.glucosio.android.tools.network.NetworkConnectivity;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExternalViewPresenterTest {

  private ExternalViewPresenter.View view;
  private ExternalViewPresenter presenter;
  private NetworkConnectivity network;

  @Before public void setUp() throws Exception {
    view = mock(ExternalViewPresenter.View.class);
    network = mock(NetworkConnectivity.class);
    presenter = new ExternalViewPresenter(view, network);
  }

  @Test
  public void ShouldLoadTerms_WhenNoExtras() throws Exception {
    when(network.isConnected()).thenReturn(true);
    when(view.extractAction()).thenReturn(null);

    presenter.onViewCreated();

    verify(view).loadExternalUrl(GlucosioExternalLinks.TERMS);
  }

  @Test
  public void ShouldLoadOpenSourceLicenses_WhenLicenseAction() throws Exception {
    when(view.extractAction()).thenReturn("open_source");
    when(network.isConnected()).thenReturn(true);

    presenter.onViewCreated();

    verify(view).loadExternalUrl(GlucosioExternalLinks.LICENSES);
  }

  @Test
  public void ShouldLoadPrivacy_WhenPrivacyAction() throws Exception {
    when(view.extractAction()).thenReturn("privacy");
    when(network.isConnected()).thenReturn(true);

    presenter.onViewCreated();

    verify(view).loadExternalUrl(GlucosioExternalLinks.PRIVACY);
  }

  @Test public void ShouldLoadAlwaysTerms_WhenUnknownAction() throws Exception {
    when(view.extractAction()).thenReturn("unknown");
    when(network.isConnected()).thenReturn(true);

    presenter.onViewCreated();

    verify(view).loadExternalUrl(GlucosioExternalLinks.TERMS);
  }

  @Test public void ShouldDisplayTermsToolbarTitle_WhenDefaultAction() throws Exception {
    when(view.extractAction()).thenReturn("unknown");
    when(network.isConnected()).thenReturn(true);

    presenter.onViewCreated();

    verify(view).setupToolbarTitle(GlucosioExternalLinks.TERMS);
  }
}