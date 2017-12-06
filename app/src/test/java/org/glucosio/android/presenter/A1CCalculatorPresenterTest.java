package org.glucosio.android.presenter;

import org.glucosio.android.activity.A1cCalculatorActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class A1CCalculatorPresenterTest {
    @Mock
    private DatabaseHandler dbHandlerMock;

    @Mock
    private A1cCalculatorActivity activity;

    @InjectMocks
    private A1CCalculatorPresenter presenter;

    private User user = new User(1, "test", "en", "en", 23, "M", 1, "mg/dL", "percentage", "", "Test", 0, 100);

    @Before
    public void setUp() throws Exception {
        when(dbHandlerMock.getUser(anyLong())).thenReturn(user);
    }

    @Test
    public void ShouldReturnZero_WhenNullStringPassed() throws Exception {
        assertThat(presenter.calculateA1C(null)).isZero();
    }

    @Test
    public void ShouldReturnZero_WhenEmptyStringPassed() throws Exception {
        assertThat(presenter.calculateA1C(null)).isZero();
    }

    @Test
    public void ShouldReturnZero_WhenDecimalSeparatorStringPassed() throws Exception {
        assertThat(presenter.calculateA1C(".")).isZero();
    }

    @Test
    public void ShouldReturnZero_WhenAnotherDecimalSeparatorStringPassed() throws Exception {
        assertThat(presenter.calculateA1C(",")).isZero();
    }

    @Test
    public void ShouldCalculatable_WhenUserPreferredMmol() throws Exception {
        user.setPreferred_unit("mmol/L");

        assertThat(presenter.calculateA1C("1")).isEqualTo(2.25);
    }

    @Test
    public void ShouldCalculatable_WhenUserPreferredPercentage() throws Exception {
        user.setPreferred_unit_a1c("mmol/mol");

        assertThat(presenter.calculateA1C("20")).isEqualTo(1.84);
    }

    @Test
    public void ShouldCallSetMmol_WhenUserPreferredUnitIsNotMgDl() throws Exception {
        user.setPreferred_unit("mmol/L");

        presenter.checkGlucoseUnit();

        verify(activity).setMmol();
    }

    @Test
    public void ShouldReturnCorrectA1CUnit_WhenGetterCalled() throws Exception {
        user.setPreferred_unit_a1c("mmol/mol");

        assertThat(presenter.getA1cUnit()).isEqualTo("mmol/mol");
    }

    @Test
    public void ShouldFinishActivityAfterSaving_WhensaveA1CCalled() throws Exception {
        user.setPreferred_unit_a1c("mmol/mol");

        presenter.saveA1C(presenter.calculateA1C("20"));

        verify(activity).finish();
    }
}
