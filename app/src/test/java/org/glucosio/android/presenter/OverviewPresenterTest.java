package org.glucosio.android.presenter;

import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.db.User;
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

    @Mock
    private User userMock;

    @Before
    public void setUp() throws Exception {
        //to remove joda error printing
        DateTimeZone.setProvider(new UTCProvider());

        presenter = new OverviewPresenter(viewMock, dbMock);
        when(dbMock.getUser(anyLong())).thenReturn(userMock);
    }

    @Test
    public void ShouldAddZerosBetweenReadings_WhenAsked() throws Exception {
        DateTime now = DateTime.now();
        DateTime fiveDaysAgo = now.minusDays(5);
        when(dbMock.getLastMonthGlucoseReadings()).thenReturn(
                Arrays.asList(
                        new GlucoseReading(12, "test", fiveDaysAgo.toDate(), ""),
                        new GlucoseReading(21, "test", now.toDate(), ""))
        );

        presenter.loadDatabase(true);

        final List<Integer> readings = presenter.getGlucoseReadings();
        DateTime minDateTime = DateTime.now().minusMonths(1).minusDays(15);
        assertThat(readings).hasSize(Days.daysBetween(minDateTime, now).getDays());
        assertThat(readings).containsSequence(12, 0, 0, 0, 0, 21);
    }

    @Test
    public void ShouldSortReadingsChronologically_WhenAsked() throws Exception {
        DateTime now = DateTime.now();
        DateTime twoDaysAgo = now.minusDays(2);
        when(dbMock.getLastMonthGlucoseReadings()).thenReturn(
                Arrays.asList(
                        new GlucoseReading(33, "test", now.toDate(), ""),
                        new GlucoseReading(11, "test", twoDaysAgo.toDate(), ""))
        );

        presenter.loadDatabase(true);

        final List<Integer> readings = presenter.getGlucoseReadings();
        assertThat(readings).containsSequence(11, 0, 33);
    }
}