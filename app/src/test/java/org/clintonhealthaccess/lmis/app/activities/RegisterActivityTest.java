package org.clintonhealthaccess.lmis.app.activities;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.AbstractModule;

import org.clintonhealthaccess.lmis.app.R;
import org.clintonhealthaccess.lmis.app.models.User;
import org.clintonhealthaccess.lmis.app.LmisException;
import org.clintonhealthaccess.lmis.app.services.CommodityService;
import org.clintonhealthaccess.lmis.app.services.OrderService;
import org.clintonhealthaccess.lmis.app.services.StockService;
import org.clintonhealthaccess.lmis.app.services.UserService;
import org.clintonhealthaccess.lmis.utils.RobolectricGradleTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowHandler;

import static org.clintonhealthaccess.lmis.app.R.id;
import static org.clintonhealthaccess.lmis.utils.TestInjectionUtil.setUpInjection;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;
import static org.robolectric.shadows.ShadowToast.getTextOfLatestToast;

@RunWith(RobolectricGradleTestRunner.class)
public class RegisterActivityTest {
    private RegisterActivity registerActivity;
    private UserService mockUserService;
    private CommodityService mockCommodityService;
    private OrderService mockOrderService;
    private StockService mockStockService;

    @Before
    public void setUp() throws Exception {
        mockUserService = mock(UserService.class);
        mockCommodityService = mock(CommodityService.class);
        mockStockService = mock(StockService.class);
        mockOrderService = mock(OrderService.class);

        setUpInjection(this, new AbstractModule() {
            @Override
            protected void configure() {
                bind(UserService.class).toInstance(mockUserService);
                bind(CommodityService.class).toInstance(mockCommodityService);
                bind(StockService.class).toInstance(mockStockService);
                bind(OrderService.class).toInstance(mockOrderService);
            }
        });

        registerActivity = buildActivity(RegisterActivity.class).create().get();
    }

    @Test
    public void testShouldRedirectToHomePageAfterSuccessfulRegistration() throws Exception {
        performSuccessfulRegistration();

        Intent homeIntent = new Intent(registerActivity, HomeActivity.class);
        assertThat(shadowOf(registerActivity).getNextStartedActivity(), equalTo(homeIntent));
    }

    @Test
    public void testMessageShouldBeShownIfRegistrationIsSuccessful() {
        performSuccessfulRegistration();

        ShadowHandler.idleMainLooper();
        assertThat(getTextOfLatestToast(), equalTo(registerActivity.getString(R.string.registration_successful_message)));
    }

    @Test
    public void shouldCallInitialiseForCommoditiesOnSuccessfulRegistration() {
        performSuccessfulRegistration();
        verify(mockCommodityService, times(1)).initialise((User) anyObject());
    }

    @Test
    public void shouldInitialiseOrderReasonsOnSuccessfulRegistration() {
        performSuccessfulRegistration();
        verify(mockOrderService, times(1)).syncReasons();
    }

    private void performSuccessfulRegistration() {
        when(mockUserService.register(anyString(), anyString())).thenReturn(new User());

        fillTextField(id.textUsername, "admin");
        fillTextField(id.textPassword, "district");
        getRegisterButton().performClick();
    }

    @Test
    public void testShouldNotRegisterIfUsernameOrPasswordIsAbsent() throws Exception {
        getRegisterButton().performClick();
        verify(mockUserService, never()).register(anyString(), anyString());

        fillTextField(id.textUsername, "admin");
        getRegisterButton().performClick();
        verify(mockUserService, never()).register(anyString(), anyString());

        fillTextField(id.textUsername, "");
        fillTextField(id.textPassword, "district");
        getRegisterButton().performClick();
        verify(mockUserService, never()).register(anyString(), anyString());
    }

    @Test
    public void testShouldStayOnSamePageIfRegistrationFails() throws Exception {
        when(mockUserService.register(anyString(), anyString())).thenThrow(new LmisException());

        fillTextField(id.textUsername, "admin");
        fillTextField(id.textPassword, "district");
        getRegisterButton().performClick();

        assertThat(shadowOf(registerActivity).getNextStartedActivity(), nullValue());
    }


    @Test
    public void testErrorShouldBeShownIfRegisterButtonIsClickedBeforeSupplyingUsername() {
        fillTextField(id.textPassword, "district");
        getRegisterButton().performClick();

        TextView textUsername = getInputField(id.textUsername);
        assertThat(textUsername.getError(), is(notNullValue()));

    }

    @Test
    public void testErrorShouldBeShownIfRegisterButtonIsClickedBeforeSupplyingPassword() {

        fillTextField(id.textUsername, "admin");
        getRegisterButton().performClick();

        TextView textPassword = getInputField(id.textPassword);
        assertThat(textPassword.getError(), is(notNullValue()));

    }

    @Test
    public void testErrorShouldBeShownIfRegistrationFailed() {
        String errorMessage = "Some failure message";
        when(mockUserService.register(anyString(), anyString())).thenThrow(new LmisException(errorMessage));
        fillTextField(id.textUsername, "adminsdsd");
        fillTextField(id.textPassword, "districtsds");
        getRegisterButton().performClick();

        ShadowHandler.idleMainLooper();
        assertThat(getTextOfLatestToast(), equalTo(errorMessage));

    }

    private void fillTextField(int inputFieldId, String text) {
        TextView usernameInputField = (TextView) registerActivity.findViewById(inputFieldId);
        usernameInputField.setText(text);
    }

    private Button getRegisterButton() {
        return (Button) registerActivity.findViewById(id.buttonRegister);
    }

    private TextView getInputField(int id) {
        return (TextView) registerActivity.findViewById(id);
    }
}