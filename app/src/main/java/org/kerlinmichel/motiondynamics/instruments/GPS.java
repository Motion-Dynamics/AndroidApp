package org.kerlinmichel.motiondynamics.instruments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.kerlinmichel.motiondynamics.MainActivity;

import java.io.PrintWriter;
import java.net.Socket;

public class GPS {

    private static LocationManager locationManager;
    private static LocationListener locationListener;

    private static Socket socket;
    private static PrintWriter serverWriter;

    private static boolean isOn = false;

    private static double lat, lon, alt;
    private static float speed, acc;
    private static long time;

    private static boolean locationChange = false;

    //public static String data = "";

    private GPS() {
    }

    public static void init(final Activity activity) throws DeviceInitException {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                ((MainActivity) activity).setData(location.getLatitude(), location.getLongitude(),
                        location.getSpeed() * 2.23694f, location.getAltitude() * 3.28084,
                        location.getAccuracy());
                lat = location.getLatitude();
                lon = location.getLongitude();
                speed = location.getSpeed();
                alt = location.getAltitude();
                acc = location.getAccuracy();
                time = location.getTime();
                locationChange = true;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            isOn = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.REQUEST_GPS);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            isOn = true;
        }
    }

    public static void turnOff(final Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
            isOn = false;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.REQUEST_GPS);
            locationManager.removeUpdates(locationListener);
            isOn = false;
        }
    }

    public static double getLat() {
        return lat;
    }

    public static double getLon() {
        return lon;
    }

    public static double getAlt() {
        return alt;
    }

    public static float getSpeed() {
        return speed;
    }

    public static float getAcc() {
        return acc;
    }

    public static long getTime() {
        return time;
    }

    public static boolean isOn() {
        return isOn;
    }

    public static double distance_on_geoid(double lat1, double lon1, double lat2, double lon2) {

        // Convert degrees to radians
        lat1 = lat1 * Math.PI / 180.0;
        lon1 = lon1 * Math.PI  / 180.0;

        lat2 = lat2 * Math.PI  / 180.0;
        lon2 = lon2 * Math.PI  / 180.0;

        // radius of earth in metres
        double r = 6378100;

        // point P
        double rho1 = r * Math.cos(lat1);
        double z1 = r * Math.sin(lat1);
        double x1 = rho1 * Math.cos(lon1);
        double y1 = rho1 * Math.sin(lon1);

        // point Q
        double rho2 = r * Math.cos(lat2);
        double z2 = r * Math.sin(lat2);
        double x2 = rho2 * Math.cos(lon2);
        double y2 = rho2 * Math.sin(lon2);

        // Dot product
        double dot = (x1 * x2 + y1 * y2 + z1 * z2);
        double cos_theta = dot / (r * r);

        double theta = Math.acos(cos_theta);

        // Distance in Metres
        return r * theta;
    }
} 