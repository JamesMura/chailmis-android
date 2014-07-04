package org.clintonhealthaccess.lmis.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.Inject;

import org.clintonhealthaccess.lmis.app.R;
import org.clintonhealthaccess.lmis.app.services.ServiceException;
import org.clintonhealthaccess.lmis.app.services.UserService;

import roboguice.activity.RoboActionBarActivity;

import static android.view.View.OnClickListener;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.clintonhealthaccess.lmis.app.R.id;
import static org.clintonhealthaccess.lmis.app.R.layout;

public class RegisterActivity extends RoboActionBarActivity {
    @Inject
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_register);

        Button registerButton = (Button) findViewById(id.buttonRegister);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getTextFromInputField(id.textUsername);
                String password = getTextFromInputField(id.textPassword);
                if (isBlank(username) || isBlank(password)) {
                    return;
                }
                doRegister(username, password);
            }
        });
    }

    private void doRegister(final String username, final String password) {
        AsyncTask<Void, Void, Void> registerTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    userService.register(username, password);
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } catch (ServiceException e) {
                    // TODO: show error message or something...
                }
                return null;
            }
        };
        registerTask.execute();
    }

    private String getTextFromInputField(int inputFieldId) {
        TextView inputField = (TextView) findViewById(inputFieldId);
        return valueOf(inputField.getText());
    }


}
