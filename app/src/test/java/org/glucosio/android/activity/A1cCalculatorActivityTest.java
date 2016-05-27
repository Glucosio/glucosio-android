package org.glucosio.android.activity;

import android.text.Editable;
import android.view.inputmethod.EditorInfo;

import org.glucosio.android.R;
import org.glucosio.android.RobolectricTest;
import org.glucosio.android.TestGlucosioApplication;
import org.glucosio.android.presenter.A1CCalculatorPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class A1cCalculatorActivityTest extends RobolectricTest {
    private A1cCalculatorActivity activity;

    @Mock
    private Editable editableMock;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        activity = Robolectric.buildActivity(A1cCalculatorActivity.class).create().get();
    }

    @Test
    public void ShouldAskPresenterToCheckUnit_WhenCreated() throws Exception {
        verify(getA1cCalculatorPresenter()).checkGlucoseUnit();
    }

    private A1CCalculatorPresenter getA1cCalculatorPresenter() {
        //noinspection ConstantConditions
        return ((TestGlucosioApplication) RuntimeEnvironment.application).createA1cCalculatorPresenter(null);
    }

    @Test
    public void ShouldBindViews_WhenCreated() throws Exception {
        assertThat(activity.A1cUnitTextView).isNotNull();
        assertThat(activity.A1CTextView).isNotNull();
        assertThat(activity.glucoseUnit).isNotNull();
    }

    @Test
    public void ShouldSetMmolToMolAsUnit_WhenUserSettingsIsPercentage() throws Exception {
        when(getA1cCalculatorPresenter().getA1cUnit()).thenReturn("not percentage");

        activity = Robolectric.buildActivity(A1cCalculatorActivity.class).create().get();

        assertThat(activity.A1cUnitTextView).hasText(R.string.mmol_mol);
    }

    @Test
    public void NotifyPresenter_WhenGlucoseValueChanged() throws Exception {
        String value = "2";
        when(editableMock.toString()).thenReturn(value);

        activity.glucoseValueChanged(editableMock);

        verify(getA1cCalculatorPresenter()).calculateA1C(value);
    }

    @Test
    public void ReturnTrue_WhenKeyboardActionDone() throws Exception {
        assertThat(activity.editorAction(null, EditorInfo.IME_ACTION_DONE, null)).isTrue();
    }

    @Test
    public void ReturnFalse_WhenKeyboardActionOther() throws Exception {
        assertThat(activity.editorAction(null, EditorInfo.IME_ACTION_GO, null)).isFalse();
    }
}