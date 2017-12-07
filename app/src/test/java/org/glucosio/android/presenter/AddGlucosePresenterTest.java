package org.glucosio.android.presenter;

import org.glucosio.android.activity.AddGlucoseActivity;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.db.User;
import org.glucosio.android.tools.ReadingTools;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddGlucosePresenterTest {

    @Mock
    private AddGlucoseActivity mockActivity;

    @Mock
    private DatabaseHandler dB;

    @Mock
    private ReadingTools readingTools;

    @InjectMocks
    private AddGlucosePresenter presenter;

    @Mock
    private User userMock;

    private final static String FAKE_TIME = "11:09";
    private final static String FAKE_DATE = "22.12";
    private final static String FAKE_TYPE = "fakeType";

    @Before
    public void setUp() {
        presenter.updateReadingSplitDateTime(new Date());

        when(dB.getUser(anyLong())).thenReturn(userMock);
        when(userMock.getPreferred_unit()).thenReturn("mg/dl");
    }

    @Test
    public void dialogOnButtonPressed_numberIsNull() {
        presenter.dialogOnAddButtonPressed(FAKE_TIME, FAKE_DATE, "", FAKE_TYPE, "");
        verify(mockActivity).showErrorMessage();
    }

    @Test
    public void dialogOnButtonPressed_isReadingAdded_false() {
        presenter.dialogOnAddButtonPressed(FAKE_TIME, FAKE_DATE, "2562", FAKE_TYPE, "");
        verify(mockActivity).showDuplicateErrorMessage();
    }
}
