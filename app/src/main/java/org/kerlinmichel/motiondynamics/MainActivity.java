package org.kerlinmichel.motiondynamics;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kerlinmichel.motiondynamics.instruments.DeviceInitException;
import org.kerlinmichel.motiondynamics.instruments.GPS;
import org.kerlinmichel.motiondynamics.instruments.InstrumentNetworkClient;
import org.kerlinmichel.motiondynamics.views.DeviceSelection;
import org.kerlinmichel.motiondynamics.views.FileView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_GPS = 0;
    private InstrumentNetworkClient client;
    private String data;
    private AlertDialog.Builder saveFileDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = "";
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.toggleMeasuring)).setText("Start Measuring");
        findViewById(R.id.savefile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFile();
            }
        });
        findViewById(R.id.toggleMeasuring).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GPS.isOn()) {
                    GPS.turnOff(MainActivity.this);
                    ((TextView)findViewById(R.id.toggleMeasuring)).setText("Start Measuring");
                    saveFileDialog.show();
                } else {
                    try {
                        GPS.init(MainActivity.this);
                        ((TextView)findViewById(R.id.toggleMeasuring)).setText("Stop Measuring");
                    } catch (DeviceInitException e) {
                        System.out.println(e);
                    }
                }
            }
        });
        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToServer();
            }
        });
        saveFileDialog = new AlertDialog.Builder(this);
        saveFileDialog.setTitle("Save Data");
        final EditText fileNameInput = new EditText(this);
        saveFileDialog.setView(fileNameInput);
        saveFileDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(MainActivity.this.getApplicationContext().getFilesDir().getAbsolutePath(), fileNameInput.getText().toString());
                try {
                    if(file.exists())
                        file.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write("time,lat,lon,speed,alt,acc\n".getBytes());
                    outputStream.write(data.getBytes());
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    System.out.println(e);
                }

            }
        });
        findViewById(R.id.go_to_files).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileView.class);
                startActivity(intent);
            }
        });
        new AsyncTask<String, Void, Void>() {
            long time = 0;

            @Override
            protected Void doInBackground(String... params) {
                while(true) {
                    if (time != GPS.getTime()) {
                        data += GPS.getTime() + "," + GPS.getLat() + "," + GPS.getLon() + "," +
                                GPS.getSpeed() * 2.23694f + "," + GPS.getAlt() * 3.28084 + "," +
                                GPS.getAcc() + "\n";
                    }
                    time = GPS.getTime();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void connectToServer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect to server");
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText hostInput = new EditText(this);
        final EditText portInput = new EditText(this);
        layout.addView(hostInput);
        layout.addView(portInput);
        portInput.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        builder.setView(layout);
        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startClient(hostInput.getText().toString(), Integer.parseInt(portInput.getText().toString()));
            }
        });
        builder.show();
    }

    public void startClient(String host, int port) {
        if(client != null)
            client.cancel(true);
        client = (InstrumentNetworkClient) new InstrumentNetworkClient(host, port) {
            long time;
            double lat = 0, lon;

            @Override
            public void update() {
                if(time != GPS.getTime())
                    triggerSend();
                time = GPS.getTime();
            }

            @Override
            public String generateMsg() {
                double speed = 0;
                double distance = GPS.distance_on_geoid(lat, lon, GPS.getLat(), GPS.getLon());
                double dtime_s = (GPS.getTime() - time) / 1000.0;
                System.out.println("dist: " + distance);
                System.out.println("time: " + dtime_s);
                System.out.println("mps: " + (distance / dtime_s));
                speed = distance / dtime_s;
                lat = GPS.getLat();
                lon = GPS.getLon();
                time = GPS.getTime();
                if(Double.isInfinite(speed) || Double.isNaN(speed))
                    speed = 0;
                return "lat:" +GPS.getLat() + ",lon:" + GPS.getLon() + ",speed(calculated):" +
                        speed*2.23694f + ",speed(device):" + GPS.getSpeed() + ",alt:" +
                        GPS.getAlt()*3.28084 + ",acc:" + GPS.getAcc();
            };
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setData(double lat, double lon, float speed, double alt, float acc) {
        ((TextView)findViewById(R.id.latitude)).setText("Lat: " + lat);
        ((TextView)findViewById(R.id.longitude)).setText("Lon: " + lon);
        ((TextView)findViewById(R.id.speed)).setText("Speed: " + speed + " mph");
        ((TextView)findViewById(R.id.altitude)).setText("Alt: " + alt);
        ((TextView)findViewById(R.id.accuracy)).setText("Accuracy: " + acc + "%");
    }

    public void writeToFile() {
        File dir = getExternalFilesDir(null);
        File[] files = dir.listFiles();
        System.out.println(dir.getAbsoluteFile());
        for(File f: files)
            System.out.println(f);
        FileOutputStream outputStream;
        String string = "time,lat,lon,speed,alt,acc\n";
        //string += GPS.data;
        try {
            /*File file = new File(getExternalFilesDir(null)+"/fight-data");
            if(!file.exists()){
                file.mkdirs();
            }*/
            System.out.println(getExternalFilesDir(null));
            File dataFile = new File(getExternalFilesDir(null), files.length + ".csv");
            outputStream = new FileOutputStream(dataFile);
            outputStream.write(string.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GPS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied
                }
                return;
            }

        }
    }
}
