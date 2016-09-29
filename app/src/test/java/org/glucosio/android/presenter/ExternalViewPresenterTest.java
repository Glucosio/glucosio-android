package org.glucosio.android.presenter;

import org.glucosio.android.tools.network.GlucosioExternalLinks;
import org.glucosio.android.tools.network.NetworkConnectivity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
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
  public void shouldThrowException_WhenNoParameters() throws Exception {
    when(network.isConnected()).thenReturn(true);
    when(view.extractTitle()).thenReturn(null);

    try {
      presenter.onViewCreated();
    } catch (Exception ex) {
      assertTrue(ex instanceof IllegalArgumentException);
    }
  }

  @Test public void shouldLoadOpenSourceLicenses_WhenLicenseParameters() throws Exception {
    String LICENSES = "licenses";
    when(view.extractUrl()).thenReturn(GlucosioExternalLinks.LICENSES);
    when(view.extractTitle()).thenReturn(LICENSES);
    when(network.isConnected()).thenReturn(true);

    presenter.onViewCreated();

    verify(view).loadExternalUrl(GlucosioExternalLinks.LICENSES);
    verify(view).setupToolbarTitle(LICENSES);
  }
}