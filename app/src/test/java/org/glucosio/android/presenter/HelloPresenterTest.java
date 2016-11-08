package org.glucosio.android.presenter;

import org.glucosio.android.RobolectricTest;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.view.HelloView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

//just because we use Tools from android it should be Robolectric test
public class HelloPresenterTest extends RobolectricTest {
    @InjectMocks
    private HelloPresenter presenter;

    @Mock
    private HelloView helloViewMock;

    @Mock
    private DatabaseHandler dbHandlerMock;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void ShouldSetUserId_WhenLoadDBHappens() throws Exception {
        presenter.loadDatabase();
        //33 just to pass age check
        presenter.onNextClicked("33", null, null, null, -1, null);

        verify(dbHandlerMock).addUser(userCaptor.capture());
        assertThat(userCaptor.getValue().getId()).isEqualTo(1);
    }

    @Test
    public void ShouldAskForWarning_WhenAgeIsNegative() throws Exception {

        presenter.onNextClicked("-1", null, null, null, -1, null);

        verify(helloViewMock).displayErrorWrongAge();
        verify(dbHandlerMock, never()).addUser(any(User.class));
    }

    @Test
    public void ShouldAskForWarning_WhenAgeIsZero() throws Exception {

        presenter.onNextClicked("0", null, null, null, -1, null);

        verify(helloViewMock).displayErrorWrongAge();
        verify(dbHandlerMock, never()).addUser(any(User.class));
    }

    @Test
    public void ShouldAskForWarning_WhenAgeIsAbove120() throws Exception {

        presenter.onNextClicked("121", null, null, null, -1, null);

        verify(helloViewMock).displayErrorWrongAge();
        verify(dbHandlerMock, never()).addUser(any(User.class));
    }

    @Test
    public void ShouldAskForWarning_WhenAgeIsNotNumber() throws Exception {

        presenter.onNextClicked("12haha", null, null, null, -1, null);

        verify(helloViewMock).displayErrorWrongAge();
        verify(dbHandlerMock, never()).addUser(any(User.class));
    }

    @Test
    public void ShouldAskForWarning_WhenAgeIsEmpty() throws Exception {

        presenter.onNextClicked("", null, null, null, -1, null);

        verify(helloViewMock).displayErrorWrongAge();
        verify(dbHandlerMock, never()).addUser(any(User.class));
    }

    @Test
    public void ShouldAskForWarning_WhenAgeIsNull() throws Exception {

        presenter.onNextClicked(null, null, null, null, -1, null);

        verify(helloViewMock).displayErrorWrongAge();
        verify(dbHandlerMock, never()).addUser(any(User.class));
    }

    @Test
    public void ShouldPropagateAllValues_WhenSavingUser() throws Exception {

        final String gender = "male";
        final String language = "ukrainian";
        final String country = "ukraine";
        final int type = 1;
        final String unit = "mmol/l";

        presenter.onNextClicked("35", gender, language, country, type, unit);

        verify(dbHandlerMock).addUser(userCaptor.capture());
        final User user = userCaptor.getValue();

        assertThat(user.getAge()).isEqualTo(35);
        assertThat(user.getGender()).isEqualTo(gender);
        assertThat(user.getPreferred_language()).isEqualTo(language);
        assertThat(user.getCountry()).isEqualTo(country);
        assertThat(user.getD_type()).isEqualTo(type);
        assertThat(user.getPreferred_unit()).isEqualTo(unit);
    }

    @Test
    public void ShouldPutADAValuesAsDefault_WhenSavingUser() throws Exception {

        presenter.onNextClicked("35", null, null, null, -1, null);

        verify(dbHandlerMock).addUser(userCaptor.capture());
        final User user = userCaptor.getValue();

        assertThat(user.getPreferred_unit_a1c()).isEqualTo("percentage");
        assertThat(user.getPreferred_unit_weight()).isEqualTo("kilograms");
        assertThat(user.getPreferred_range()).isEqualTo("ADA");
        assertThat(user.getCustom_range_max()).isEqualTo(180);
        assertThat(user.getCustom_range_min()).isEqualTo(70);
    }
}