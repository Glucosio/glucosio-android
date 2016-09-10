package org.glucosio.android.presenter;

import org.glucosio.android.RobolectricTest;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.GlucoseReading;
import org.glucosio.android.view.ExportView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ExportPresenterTest extends RobolectricTest {

    private static final String MOCK_NOTE_FOR_TEST = "Note for testing, this should be exported";
    private static final Integer TEST_READING_VALUE = 55;

    @Mock
    private ExportPresenter presenter;

    @Mock
    private ExportView exportView;

    @Mock
    private DatabaseHandler dbHandlerMock;

    @Mock
    private GlucoseReading glucoseReadingMock;

    @Captor
    private ArgumentCaptor<GlucoseReading> glucoseReadingCaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(dbHandlerMock.getGlucoseReading(anyLong())).thenReturn(glucoseReadingMock);
        when(glucoseReadingMock.getNotes()).thenReturn(MOCK_NOTE_FOR_TEST);
        when(glucoseReadingMock.getCreated()).thenReturn(new Date(System.currentTimeMillis()));
        when(glucoseReadingMock.getReading()).thenReturn(TEST_READING_VALUE);
    }


    @Test
    public void ShouldReturnNoteFromReading_WhenReadingHasNote() throws Exception {
        assertThat(glucoseReadingMock.getNotes()).matches(MOCK_NOTE_FOR_TEST);
    }

    @Test
    public void ShouldReturnNotReturnNoteFromReading_WhenReadingHasNoNote() throws Exception {
        when(glucoseReadingMock.getNotes()).thenReturn("");
        assertThat(glucoseReadingMock.getNotes()).doesNotMatch(MOCK_NOTE_FOR_TEST);
        assertThat(glucoseReadingMock.getNotes()).isEmpty();
    }
}