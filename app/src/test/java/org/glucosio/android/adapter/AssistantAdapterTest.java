package org.glucosio.android.adapter;

import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import org.glucosio.android.R;
import org.glucosio.android.RobolectricTest;
import org.glucosio.android.object.ActionTip;
import org.glucosio.android.presenter.AssistantPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AssistantAdapterTest extends RobolectricTest {
    private AppCompatActivity activity;
    private ViewGroup viewGroup;

    private AssistantAdapter adapter;
    private String[] actionTipTitles;

    @Mock
    private AssistantPresenter presenterMock;
    private String[] actionTipDescriptions;
    private String[] actionTipActions;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        activity = Robolectric.buildActivity(AppCompatActivity.class).create().get();
        viewGroup = (ViewGroup) activity.findViewById(android.R.id.content);

        List<ActionTip> tips = prepareTips();
        adapter = new AssistantAdapter(presenterMock, activity.getResources(), tips);
    }

    private List<ActionTip> prepareTips() {
        List<ActionTip> actionTips = new ArrayList<>();

        Resources r = activity.getResources();

        actionTipTitles = r.getStringArray(R.array.assistant_titles);
        actionTipDescriptions = r.getStringArray(R.array.assistant_descriptions);
        actionTipActions = r.getStringArray(R.array.assistant_actions);

        for (int i = 0; i < actionTipTitles.length; i++) {
            String actionTipTitle = actionTipTitles[i];
            String actionTipDescription = actionTipDescriptions[i];
            String actionTipAction = actionTipActions[i];

            ActionTip actionTip = new ActionTip();
            actionTip.setTipTitle(actionTipTitle);
            actionTip.setTipDescription(actionTipDescription);
            actionTip.setTipAction(actionTipAction);

            actionTips.add(actionTip);
        }

        return actionTips;
    }

    @Test
    public void CorrectlyBindRows() throws Exception {
        for (int i = 0; i < actionTipTitles.length; i++) {
            AssistantAdapter.ViewHolder holder = createAndBindViewHolder(i);

            assertThat(getTextView(holder, R.id.fragment_assistant_item_title))
                    .hasText(actionTipTitles[i]);
            assertThat(getTextView(holder, R.id.fragment_assistant_item_description))
                    .hasText(actionTipDescriptions[i]);
            assertThat(getTextView(holder, R.id.fragment_assistant_item_action))
                    .hasText(actionTipActions[i]);
        }
    }

    private TextView getTextView(AssistantAdapter.ViewHolder holder, @IdRes int viewId) {
        return (TextView) holder.mView.findViewById(viewId);
    }

    @Test
    public void CallPresenter_WhenFeedbackAsked() throws Exception {
        int position = findIndex(activity.getString(R.string.assistant_feedback_title));
        AssistantAdapter.ViewHolder holder = createAndBindViewHolder(position);

        clickActionButton(holder);

        verify(presenterMock).userSupportAsked();
    }

    private boolean clickActionButton(AssistantAdapter.ViewHolder holder) {
        return holder.mView.findViewById(R.id.fragment_assistant_item_action).performClick();
    }

    private AssistantAdapter.ViewHolder createAndBindViewHolder(int position) {
        final AssistantAdapter.ViewHolder holder = adapter.onCreateViewHolder(viewGroup, -1);
        adapter.onBindViewHolder(holder, position);
        return holder;
    }

    private int findIndex(String titleString) {
        for (int i = 0; i < actionTipTitles.length; i++) {
            if (actionTipTitles[i].equals(titleString)) {
                return i;
            }
        }

        return -1;
    }

    @Test
    public void CallPresenter_WhenExportAsked() throws Exception {
        int position = findIndex(activity.getString(R.string.assistant_export_title));
        AssistantAdapter.ViewHolder holder = createAndBindViewHolder(position);

        clickActionButton(holder);

        verify(presenterMock).userAskedExport();
    }

    @Test
    public void CallPresenter_WhenA1CCalculatorAsked() throws Exception {
        int position = findIndex(activity.getString(R.string.assistant_calculator_a1c_title));
        AssistantAdapter.ViewHolder holder = createAndBindViewHolder(position);

        clickActionButton(holder);

        verify(presenterMock).userAskedA1CCalculator();
    }

    @Test
    public void CallPresenter_WhenAddReadingAsked() throws Exception {
        int position = findIndex(activity.getString(R.string.assistant_reading_title));
        AssistantAdapter.ViewHolder holder = createAndBindViewHolder(position);

        clickActionButton(holder);

        verify(presenterMock).userAskedAddReading();
    }
}