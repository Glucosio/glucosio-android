package org.glucosio.android.fragment;

import org.glucosio.android.RobolectricTest;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class AssistantFragmentTest extends RobolectricTest {
    private AssistantFragment fragment;

    @Before
    public void setUp() throws Exception {
        fragment = AssistantFragment.newInstance();
        SupportFragmentTestUtil.startFragment(fragment);
    }

    @Test
    public void ShouldBindViews_WhenCreated() throws Exception {
        assertThat(fragment.archivedButton).isNotNull();
        assertThat(fragment.archivedDismissButton).isNotNull();
        assertThat(fragment.tipsRecycler).isNotNull();
    }

    @Test
    public void ShouldUnBindViews_WhenDestroyed() throws Exception {
        fragment.onDestroyView();

        assertThat(fragment.archivedButton).isNull();
        assertThat(fragment.archivedDismissButton).isNull();
        assertThat(fragment.tipsRecycler).isNull();
    }
}