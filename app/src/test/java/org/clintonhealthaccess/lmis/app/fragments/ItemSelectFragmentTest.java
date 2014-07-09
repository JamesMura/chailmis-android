package org.clintonhealthaccess.lmis.app.fragments;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.inject.Inject;

import org.clintonhealthaccess.lmis.app.R;
import org.clintonhealthaccess.lmis.app.models.Category;
import org.clintonhealthaccess.lmis.app.models.Commodity;
import org.clintonhealthaccess.lmis.app.persistence.CommoditiesRepository;
import org.clintonhealthaccess.lmis.utils.RobolectricGradleTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowDialog;

import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.clintonhealthaccess.lmis.utils.TestFixture.initialiseDefaultCommodities;
import static org.clintonhealthaccess.lmis.utils.TestInjectionUtil.setUpInjection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.robolectric.Robolectric.application;
import static org.robolectric.util.FragmentTestUtil.startFragment;

@RunWith(RobolectricGradleTestRunner.class)
public class ItemSelectFragmentTest {
    @Inject
    private CommoditiesRepository commoditiesRepository;
    private ItemSelectFragment itemSelectFragment;

    @Before
    public void setUp() throws Exception {
        setUpInjection(this);
        initialiseDefaultCommodities(application);

        Category antiMalarialCategory = commoditiesRepository.allCategories().get(0);
        itemSelectFragment = ItemSelectFragment.newInstance(antiMalarialCategory, new ArrayList<Commodity>());
        startFragment(itemSelectFragment);
    }

    @Test
    public void testShouldRenderAllCategoryButtons() throws Exception {
        Dialog dialog = ShadowDialog.getLatestDialog();
        assertTrue(dialog.isShowing());

        LinearLayout categoriesLayout = (LinearLayout) dialog.findViewById(R.id.itemSelectOverlayCategories);
        assertThat(categoriesLayout, not(nullValue()));

        assertThat(categoriesLayout.getChildCount(), is(6));
        for (int i = 0; i < categoriesLayout.getChildCount(); i++) {
            View button = categoriesLayout.getChildAt(i);
            assertThat(button, instanceOf(Button.class));
        }
    }

    @Test
    public void testCategoryButtonClickChangesCommoditiesShowing() throws Exception {
        Dialog dialog = ShadowDialog.getLatestDialog();
        LinearLayout categoriesLayout = (LinearLayout) dialog.findViewById(R.id.itemSelectOverlayCategories);

        Button secondCategoryButton = (Button) categoriesLayout.getChildAt(1);
        secondCategoryButton.performClick();

        ListView commoditiesLayout = (ListView) dialog.findViewById(R.id.listViewCommodities);
        assertThat(commoditiesLayout, not(nullValue()));
        assertThat(commoditiesLayout.getAdapter().getCount(), is(1));

        assertThat(secondCategoryButton.isSelected(), is(true));
    }

    @Test
    public void testCloseButtonExists() throws Exception {

        Dialog dialog = ShadowDialog.getLatestDialog();
        Button buttonClose = (Button) dialog.findViewById(R.id.buttonClose);
        assertThat(buttonClose, not(nullValue()));

    }

    @Test
    public void testCloseButtonClosesTheDialog() throws Exception {
        Dialog dialog = ShadowDialog.getLatestDialog();
        Button buttonClose = (Button) dialog.findViewById(R.id.buttonClose);
        assertTrue(itemSelectFragment.isVisible());
        buttonClose.callOnClick();
        assertFalse(itemSelectFragment.isVisible());

    }
}