package org.kerlinmichel.motiondynamics.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import org.kerlinmichel.motiondynamics.MainActivity;
import org.kerlinmichel.motiondynamics.R;

public class DeviceSelection extends AppCompatActivity {

    public static boolean runGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_selection);
        runGPS = false;
        findViewById(R.id.run_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(runGPS) {
                    Intent intent = new Intent(DeviceSelection.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.select_to_files).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceSelection.this, FileView.class);
                startActivity(intent);
            }
        });
    }

    public void onCheckboxClicked(View view) {
        // Is the button now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.run_GPS:
                if(checked) {
                    runGPS = true;
                } else {
                    runGPS = false;
                }
                break;
        }
    }
}
