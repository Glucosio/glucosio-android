package org.glucosio.android.presenter;

import org.glucosio.android.Constants;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.db.User;
import org.glucosio.android.db.UserBuilder;
import org.glucosio.android.view.OverviewView;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.tz.UTCProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OverviewPresenterTest {
    private OverviewPresenter presenter;

    @Mock
    private OverviewView viewMock;

    @Mock
    private DatabaseHandler dbMock;

    private User user = new UserBuilder()
            .setId(1).setName("test")
            .setPreferredLanguage("en")
            .setCountry("en")
            .setAge(23)
            .setGender("M")
            .setDiabetesType(1)
            .setPreferredUnit(Constants.Units.MG_DL)
            .setPreferredA1CUnit("")
            .setPreferredWeightUnit("")
            .setPreferredRange("Test")
            .setMinRange(0)
            .setMaxRange(100)
            .createUser();

    @Before
    public void setUp() {
        //to remove joda error printing
        DateTimeZone.setProvider(new UTCProvider());

        presenter = new OverviewPresenter(viewMock, dbMock);
        when(dbMock.getUser(anyLong())).thenReturn(user);
    }

    @Test
    public void ShouldAddZerosBetweenReadings_WhenAsked() {
        DateTime now = DateTime.now();
        DateTime fiveDaysAgo = now.minusDays(5);
        when(dbMock.getLastMonthGlucoseReadings()).thenReturn(
                Arrays.asList(
                        new GlucoseReading(12, "test", fiveDaysAgo.toDate(), ""),
                        new GlucoseReading(21, "test", now.toDate(), ""))
        );

        presenter.loadDatabase(true);

        final List<Double> readings = presenter.getGlucoseReadings();
        DateTime minDateTime = DateTime.now().minusMonths(1).minusDays(15);
        assertThat(readings).hasSize(Days.daysBetween(minDateTime, now).getDays());
        assertThat(readings).containsSequence(12., 0., 0., 0., 0., 21.);
    }

    @Test
    public void ShouldSortReadingsChronologically_WhenAsked() {
        DateTime now = DateTime.now();
        DateTime twoDaysAgo = now.minusDays(2);
        when(dbMock.getLastMonthGlucoseReadings()).thenReturn(
                Arrays.asList(
                        new GlucoseReading(33, "test", now.toDate(), ""),
                        new GlucoseReading(11, "test", twoDaysAgo.toDate(), ""))
        );

        presenter.loadDatabase(true);

        final List<Double> readings = presenter.getGlucoseReadings();
        assertThat(readings).containsSequence(11., 0., 33.);
    }
}
