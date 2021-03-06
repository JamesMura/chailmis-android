/*
 * Copyright (c) 2014, Thoughtworks Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package org.clintonhealthaccess.lmis.app.activities;

import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import org.clintonhealthaccess.lmis.LmisTestClass;
import org.clintonhealthaccess.lmis.app.R;
import org.clintonhealthaccess.lmis.app.activities.viewmodels.BaseCommodityViewModel;
import org.clintonhealthaccess.lmis.app.adapters.SelectedCommoditiesAdapter;
import org.clintonhealthaccess.lmis.app.events.CommodityToggledEvent;
import org.clintonhealthaccess.lmis.app.models.Commodity;
import org.clintonhealthaccess.lmis.app.models.Dispensing;
import org.clintonhealthaccess.lmis.app.models.User;
import org.clintonhealthaccess.lmis.app.persistence.LmisSqliteOpenHelper;
import org.clintonhealthaccess.lmis.app.services.CommodityService;
import org.clintonhealthaccess.lmis.app.services.DispensingService;
import org.clintonhealthaccess.lmis.app.services.StockService;
import org.clintonhealthaccess.lmis.app.services.UserService;
import org.clintonhealthaccess.lmis.utils.RobolectricGradleTestRunner;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowToast;

import de.greenrobot.event.EventBus;

import static android.view.View.VISIBLE;
import static junit.framework.Assert.assertFalse;
import static org.clintonhealthaccess.lmis.utils.ListTestUtils.getViewFromListRow;
import static org.clintonhealthaccess.lmis.utils.TestInjectionUtil.setUpInjectionWithMockLmisServer;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Robolectric.application;
import static org.robolectric.Robolectric.setupActivity;

@RunWith(RobolectricGradleTestRunner.class)
public class DispenseActivityTest extends LmisTestClass {

    @Inject
    private CommodityService commodityService;

    private StockService stockService;
    private DispensingService dispenseService;
    private UserService userService;

    public static DispenseActivity getDispenseActivity() {
        return setupActivity(DispenseActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        LmisSqliteOpenHelper.closeHelper();
        stockService = mock(StockService.class);
        dispenseService = mock(DispensingService.class);
        userService = mock(UserService.class);
        when(userService.getRegisteredUser()).thenReturn(new User("", "", "place"));
        setUpInjectionWithMockLmisServer(application, this, new AbstractModule() {
            @Override
            protected void configure() {
                bind(StockService.class).toInstance(stockService);
                bind(DispensingService.class).toInstance(dispenseService);
                bind(UserService.class).toInstance(userService);
            }
        });
        commodityService.initialise(new User("test", "pass"));
    }

    @Test
    public void testBuildActivity() throws Exception {
        DispenseActivity activity = getDispenseActivity();
        assertThat(activity, not(nullValue()));
    }

    @Test
    public void shouldHaveAKeyBoardView() throws Exception {
        DispenseActivity activity = getDispenseActivity();
        assertThat(activity, not(nullValue()));
        assertThat(activity.keyBoardView, not(nullValue()));
    }

    @Test
    public void testShouldDisplayAllCategoriesAsButtons() throws Exception {
        DispenseActivity dispenseActivity = getDispenseActivity();

        LinearLayout categoryLayout = (LinearLayout) dispenseActivity.findViewById(R.id.layoutCategories);
        int buttonAmount = categoryLayout.getChildCount();
        assertThat(buttonAmount, is(7));

        for (int i = 2; i < buttonAmount; i++) {
            View childView = categoryLayout.getChildAt(i);
            assertThat(childView, instanceOf(Button.class));
        }
    }

    @Test
    public void shouldToggleSelectedItemsWhenToggleEventIsTriggered() throws Exception {
        CommodityToggledEventDetails eventDetails = fireCommodityToggledEvent(getDispenseActivity());

        assertThat(eventDetails.dispenseActivity.selectedCommodities, contains(eventDetails.commodityViewModel()));

        refire(eventDetails.commodityToggledEvent);

        assertThat(eventDetails.dispenseActivity.selectedCommodities, not(contains(eventDetails.commodityViewModel())));
    }

    @Test
    public void listViewShouldToggleCommodityWhenToggleEventIsTriggered() throws Exception {
        CommodityToggledEventDetails eventDetails = fireCommodityToggledEvent(getDispenseActivity());

        BaseCommodityViewModel commodityInList = (BaseCommodityViewModel) eventDetails.dispenseActivity.gridViewSelectedCommodities.getAdapter().getItem(0);

        assertThat(commodityInList, is(eventDetails.commodityViewModel()));

        refire(eventDetails.commodityToggledEvent);

        assertThat(eventDetails.dispenseActivity.gridViewSelectedCommodities.getAdapter().getCount(), is(0));
    }

    @Test
    public void shouldRemoveSelectedCommodityFromListWhenCancelButtonIsClicked() {
        CommodityToggledEventDetails eventDetails = fireCommodityToggledEvent(getDispenseActivity());

        SelectedCommoditiesAdapter adapter = (SelectedCommoditiesAdapter) eventDetails.dispenseActivity.arrayAdapter;

        ImageButton cancelButton = (ImageButton) getViewFromListRow(adapter, R.layout.selected_dispense_commodity_list_item, R.id.imageButtonCancel);

        cancelButton.performClick();

        assertFalse(eventDetails.dispenseActivity.selectedCommodities.contains(eventDetails.commodityViewModel()));
        assertThat(eventDetails.dispenseActivity.gridViewSelectedCommodities.getAdapter().getCount(), is(0));
    }

    @Test
    public void testThatSubmitButtonExists() throws Exception {
        DispenseActivity activity = getDispenseActivity();
        assertThat(activity.buttonSubmitDispense, is(notNullValue()));
    }

    private void refire(CommodityToggledEvent commodityToggledEvent) {
        EventBus.getDefault().post(commodityToggledEvent);
    }

    private CommodityToggledEventDetails fireCommodityToggledEvent(DispenseActivity dispenseActivity) {
        Commodity commodity = mock(Commodity.class);
        when(commodity.getName()).thenReturn("food");
        when(commodity.getStockOnHand()).thenReturn(10);
        BaseCommodityViewModel commodityViewModel = new BaseCommodityViewModel(commodity);
        CommodityToggledEvent commodityToggledEvent = new CommodityToggledEvent(commodityViewModel);

        refire(commodityToggledEvent);

        return new CommodityToggledEventDetails(dispenseActivity, commodityToggledEvent);
    }

    @Test
    public void shouldToggleSubmitButtonVisibility() throws Exception {
        DispenseActivity dispenseActivity = getDispenseActivity();

        assertThat(dispenseActivity.buttonSubmitDispense.getVisibility(), not(is(VISIBLE)));

        BaseCommodityViewModel commodityViewModel = new BaseCommodityViewModel(new Commodity("name"));
        CommodityToggledEvent commodityToggledEvent = new CommodityToggledEvent(commodityViewModel);
        EventBus.getDefault().post(commodityToggledEvent);

        assertThat(dispenseActivity.buttonSubmitDispense.getVisibility(), is(VISIBLE));
    }

    @Test
    public void getDispensingShouldGetItemsInTheListView() throws Exception {
        String commodityName = "food";

        DispenseActivity dispenseActivity = getDispenseActivity();

        BaseCommodityViewModel commodityViewModel = new BaseCommodityViewModel(new Commodity(commodityName));
        commodityViewModel.setQuantityEntered(12);
        CommodityToggledEvent commodityToggledEvent = new CommodityToggledEvent(commodityViewModel);
        EventBus.getDefault().post(commodityToggledEvent);

        Dispensing dispensing = dispenseActivity.getDispensing();
        assertThat(dispensing.getDispensingItems().size(), is(1));

        assertThat(dispensing.getDispensingItems().get(0).getQuantity(), is(12));
        assertThat(dispensing.getDispensingItems().get(0).getCommodity().getName(), is(commodityName));
    }

    @Test
    public void testThatIfAllDispensingItemsHaveQuantitiesNoToastIsMade() throws Exception {

        DispenseActivity dispenseActivity = getDispenseActivity();

        GridView mockGridView = mock(GridView.class);
        SelectedCommoditiesAdapter mockCommoditiesAdapter = mock(SelectedCommoditiesAdapter.class);
        View mockListItemView = mock(View.class);
        EditText mockEditText = new EditText(application);
        mockEditText.setText("12");

        when(mockListItemView.findViewById(R.id.editTextQuantity)).thenReturn(mockEditText);
        when(mockGridView.getChildAt(anyInt())).thenReturn(mockListItemView);
        when(mockGridView.getChildCount()).thenReturn(1);
        when(mockCommoditiesAdapter.getItem(anyInt())).thenReturn(new BaseCommodityViewModel(new Commodity("food")));
        when(mockGridView.getAdapter()).thenReturn(mockCommoditiesAdapter);

        dispenseActivity.gridViewSelectedCommodities = mockGridView;
        dispenseActivity.findViewById(R.id.buttonSubmitDispense).performClick();
        ShadowHandler.idleMainLooper();

        assertThat(ShadowToast.getTextOfLatestToast(), is(Matchers.nullValue()));


    }

    @Test
    public void testThatIfAnyOfTheDispensingItemsHaveErrorsAToastIsMade() throws Exception {
        DispenseActivity dispenseActivity = getDispenseActivity();

        GridView mockGridView = mock(GridView.class);
        View mockListItemView = mock(View.class);
        EditText mockEditText = new EditText(application);

        mockEditText.setText("12");
        mockEditText.setError("error");
        SelectedCommoditiesAdapter mockSelectedCommoditiesAdapter = mock(SelectedCommoditiesAdapter.class);

        when(mockListItemView.findViewById(R.id.editTextQuantity)).thenReturn(mockEditText);
        when(mockSelectedCommoditiesAdapter.getItem(anyInt())).thenReturn(new BaseCommodityViewModel(new Commodity("food")));
        when(mockSelectedCommoditiesAdapter.getCount()).thenReturn(1);
        when(mockSelectedCommoditiesAdapter.getView(anyInt(), org.mockito.Matchers.any(View.class), org.mockito.Matchers.any(ViewGroup.class))).thenReturn(mockListItemView);
        when(mockGridView.getAdapter()).thenReturn(mockSelectedCommoditiesAdapter);
        when(mockGridView.getChildAt(anyInt())).thenReturn(mockListItemView);
        when(mockGridView.getChildCount()).thenReturn(1);

        dispenseActivity.gridViewSelectedCommodities = mockGridView;
        dispenseActivity.findViewById(R.id.buttonSubmitDispense).performClick();
        ShadowHandler.idleMainLooper();

        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(application.getString(R.string.dispense_submit_validation_message_errors)));
    }

    @Test
    public void testThatIfAllDispensingItemsHaveNoQuantitiesAToastIsMade() throws Exception {
        DispenseActivity dispenseActivity = getDispenseActivity();

        GridView mockGridView = mock(GridView.class);
        View mockListItemView = mock(View.class);
        EditText mockEditText = new EditText(application);
        SelectedCommoditiesAdapter mockSelectedCommoditiesAdapter = mock(SelectedCommoditiesAdapter.class);

        when(mockListItemView.findViewById(R.id.editTextQuantity)).thenReturn(mockEditText);
        when(mockGridView.getAdapter()).thenReturn(mockSelectedCommoditiesAdapter);
        when(mockSelectedCommoditiesAdapter.getItem(anyInt())).thenReturn(new BaseCommodityViewModel(new Commodity("food")));
        when(mockSelectedCommoditiesAdapter.getCount()).thenReturn(1);
        when(mockSelectedCommoditiesAdapter.getView(anyInt(), org.mockito.Matchers.any(View.class), org.mockito.Matchers.any(ViewGroup.class))).thenReturn(mockListItemView);
        when(mockGridView.getChildAt(anyInt())).thenReturn(mockListItemView);
        when(mockGridView.getChildCount()).thenReturn(1);

        dispenseActivity.gridViewSelectedCommodities = mockGridView;
        dispenseActivity.findViewById(R.id.buttonSubmitDispense).performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(application.getString(R.string.dispense_submit_validation_message_filled)));
    }

    @Test
    public void testThatIfAllDispensingItemsHaveZeroQuantitiesAToastIsMade() throws Exception {
        DispenseActivity dispenseActivity = getDispenseActivity();

        GridView mockGridView = mock(GridView.class);
        View mockListItemView = mock(View.class);
        SelectedCommoditiesAdapter mockSelectedCommoditiesAdapter = mock(SelectedCommoditiesAdapter.class);
        EditText mockEditText = new EditText(application);

        mockEditText.setText("0");

        when(mockListItemView.findViewById(R.id.editTextQuantity)).thenReturn(mockEditText);
        when(mockListItemView.findViewById(R.id.editTextQuantity)).thenReturn(mockEditText);
        when(mockGridView.getAdapter()).thenReturn(mockSelectedCommoditiesAdapter);
        when(mockSelectedCommoditiesAdapter.getItem(anyInt())).thenReturn(new BaseCommodityViewModel(new Commodity("food")));
        when(mockSelectedCommoditiesAdapter.getCount()).thenReturn(1);
        when(mockSelectedCommoditiesAdapter.getView(anyInt(), org.mockito.Matchers.any(View.class), org.mockito.Matchers.any(ViewGroup.class))).thenReturn(mockListItemView);
        when(mockGridView.getChildAt(anyInt())).thenReturn(mockListItemView);
        when(mockGridView.getChildCount()).thenReturn(1);

        dispenseActivity.gridViewSelectedCommodities = mockGridView;
        dispenseActivity.findViewById(R.id.buttonSubmitDispense).performClick();
        ShadowHandler.idleMainLooper();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(application.getString(R.string.dispense_submit_validation_message_zero)));
    }

    @Test
    public void whenYouDispenseToaPatientYouShouldSeeAPrescriptionId() throws Exception {
        DispenseActivity dispenseActivity = getDispenseActivity();

        assertThat(dispenseActivity.findViewById(R.id.textViewPrescriptionId).getVisibility(), is(VISIBLE));
        assertThat(dispenseActivity.findViewById(R.id.textViewPrescriptionText).getVisibility(), is(VISIBLE));
    }

    @Test
    public void prescriptionIdShouldBeShownOnTheDispenseActivity() throws Exception {
        String resultExpected = "0003-Jul";
        when(dispenseService.getNextPrescriptionId()).thenReturn(resultExpected);
        DispenseActivity dispenseActivity = getDispenseActivity();

        assertThat(((TextView) dispenseActivity.findViewById(R.id.textViewPrescriptionId)).getText().toString(), is(resultExpected));
    }

    @Test
    public void getDispensingShouldHaveThePrescriptionId() throws Exception {
        String resultExpected = "0003-Jul";
        when(dispenseService.getNextPrescriptionId()).thenReturn(resultExpected);
        DispenseActivity dispenseActivity = getDispenseActivity();
        assertThat(dispenseActivity.getDispensing().getPrescriptionId(), is(resultExpected));
    }

    @Test
    public void shouldShowConfirmButtonIfDispensingIsValid() throws Exception {

        DispenseActivity dispenseActivity = getDispenseActivity();

        GridView mockGridView = mock(GridView.class);
        View mockListItemView = mock(View.class);
        SelectedCommoditiesAdapter mockSelectedCommoditiesAdapter = mock(SelectedCommoditiesAdapter.class);
        EditText mockEditText = new EditText(application);

        mockEditText.setText("12");

        when(mockListItemView.findViewById(R.id.editTextQuantity)).thenReturn(mockEditText);
        when(mockGridView.getAdapter()).thenReturn(mockSelectedCommoditiesAdapter);
        when(mockSelectedCommoditiesAdapter.getItem(anyInt())).thenReturn(new BaseCommodityViewModel(new Commodity("food")));
        when(mockSelectedCommoditiesAdapter.getCount()).thenReturn(1);
        when(mockSelectedCommoditiesAdapter.getView(anyInt(), org.mockito.Matchers.any(View.class), org.mockito.Matchers.any(ViewGroup.class))).thenReturn(mockListItemView);
        when(mockGridView.getChildAt(anyInt())).thenReturn(mockListItemView);
        when(mockGridView.getChildCount()).thenReturn(1);

        dispenseActivity.gridViewSelectedCommodities = mockGridView;
        dispenseActivity.findViewById(R.id.buttonSubmitDispense).performClick();

        Dialog dialog = ShadowDialog.getLatestDialog();
        assertThat(dialog, is(notNullValue()));


    }

    @Test
    public void shouldHaveAnAutoCompleteTextView() throws Exception {
        DispenseActivity activity = getDispenseActivity();
        assertThat(activity.autoCompleteTextViewCommodities, not(nullValue()));
    }


    private class CommodityToggledEventDetails {
        public DispenseActivity dispenseActivity;
        public CommodityToggledEvent commodityToggledEvent;

        public CommodityToggledEventDetails(DispenseActivity dispenseActivity, CommodityToggledEvent commodityToggledEvent) {
            this.dispenseActivity = dispenseActivity;
            this.commodityToggledEvent = commodityToggledEvent;
        }

        public BaseCommodityViewModel commodityViewModel() {
            return this.commodityToggledEvent.getCommodity();
        }
    }
}
