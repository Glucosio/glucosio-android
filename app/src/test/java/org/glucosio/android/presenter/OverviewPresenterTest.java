package org.glucosio.android.presenter;

import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.db.User;
import org.glucosio.android.fragment.OverviewView;
import org.joda.time.DateTime;
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
        presenter = new OverviewPresenter(viewMock, dbMock);
        when(dbMock.getUser(anyLong())).thenReturn(userMock);
    }

    @Test
    public void ShouldAddZerosBetweenReadings_WhenAsked() throws Exception {
        DateTime now = DateTime.now();
        DateTime fiveDaysAgo = now.minusDays(5);
        when(dbMock.getGlucoseReadings()).thenReturn(
                Arrays.asList(
                        new GlucoseReading(12, "test", fiveDaysAgo.toDate(), ""),
                        new GlucoseReading(21, "test", now.toDate(), ""))
        );

        presenter.loadDatabase();

        final List<Integer> readings = presenter.getGlucoseReadings();
        assertThat(readings).hasSize(6);
        assertThat(readings).containsSequence(12, 0, 0, 0, 0, 21);
    }
}