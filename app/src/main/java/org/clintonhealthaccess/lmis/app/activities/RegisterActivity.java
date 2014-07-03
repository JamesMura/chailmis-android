package org.clintonhealthaccess.lmis.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.Inject;

import org.clintonhealthaccess.lmis.app.R;
import org.clintonhealthaccess.lmis.app.services.UserService;

import roboguice.activity.RoboActionBarActivity;

import static android.util.Log.i;
import static android.view.View.OnClickListener;
import static java.lang.String.valueOf;
import static org.clintonhealthaccess.lmis.app.R.id;
import static org.clintonhealthaccess.lmis.app.R.layout;

public class RegisterActivity extends RoboActionBarActivity {
    @Inject
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_register);

        i("User service == null?", String.valueOf(userService == null));

        Button registerButton = (Button) findViewById(id.buttonRegister);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getTextFromInputField(id.textUsername);
                String password = getTextFromInputField(id.textPassword);
                userService.register(username, password);
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });
    }

    private String getTextFromInputField(int inputFieldId) {
        TextView inputField = (TextView) findViewById(inputFieldId);
        return valueOf(inputField.getText());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
