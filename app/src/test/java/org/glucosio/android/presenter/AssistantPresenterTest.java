package org.glucosio.android.presenter;

import org.glucosio.android.fragment.AssistantFragment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AssistantPresenterTest {
    @InjectMocks
    private AssistantPresenter presenter;
    @Mock
    private AssistantFragment assistantFragmentMock;

    @Test
    public void CallFragment_WhenUserAskedSupport() throws Exception {
        presenter.userSupportAsked();

        verify(assistantFragmentMock).openSupportDialog();
    }

    @Test
    public void CallFragment_WhenUserAskedToShowCalculator() throws Exception {
        presenter.userAskedA1CCalculator();

        verify(assistantFragmentMock).startA1CCalculatorActivity();
    }

    @Test
    public void CallFragment_WhenUserAskedToAddReading() throws Exception {
        presenter.userAskedAddReading();

        verify(assistantFragmentMock).addReading();
    }

    @Test
    public void CallFragment_WhenUserAskedExport() throws Exception {
        presenter.userAskedExport();

        verify(assistantFragmentMock).startExportActivity();
    }
}