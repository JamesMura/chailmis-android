package org.clintonhealthaccess.lmis.app.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.clintonhealthaccess.lmis.app.R;
import org.clintonhealthaccess.lmis.app.fragments.ItemSelectFragment;

import roboguice.inject.InjectView;

public class DispenseActivity extends BaseActivity {
    @InjectView(R.id.button1)
    Button button1;

    @InjectView(R.id.button2)
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispense);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ItemSelectFragment dialog = ItemSelectFragment.newInstance("Some Title", "");

                dialog.show(fm, "as");
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Button 2 clicked",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}
