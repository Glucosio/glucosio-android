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

import java.util.Calendar;
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

    @Before
    public void setUp() {

    }

    @Test
    public void dialogOnButtonPressed_numberIsNull() {
        String testFakeTime = "11:09";
        String testFakeDate = "22.12";
        String testFakeType = "fakeType";

        presenter.updateReadingSplitDateTime(new Date());

        when(dB.getUser(anyLong())).thenReturn(userMock);
        when(userMock.getPreferred_unit()).thenReturn("mg/dl");

        presenter.dialogOnAddButtonPressed(testFakeTime, testFakeDate, null, testFakeType, "");
        verify(mockActivity).showErrorMessage();
    }
}
