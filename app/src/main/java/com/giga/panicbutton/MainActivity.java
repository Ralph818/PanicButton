package com.giga.panicbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    static final String tag = "log-principal";
    static final int requestCode = 1;

    // newest
    LocationManager lm;
    Location l;
    double lon;
    double lat;

    // to check location enabled
    boolean gps_enabled = false;
    boolean network_enabled = false;

    private final LocationListener locListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(@NonNull Location location)
        {
            lon = location.getLongitude();
            lat = location.getLatitude();
            Log.d(tag,"onLocationChanged =>" + lat + "," + lon);
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //validate location enabled
        CheckLocationEnabled();
    }

    @SuppressLint("MissingPermission")
    private void CheckLocationEnabled()
    {
        // Validate whether the GPS location service is on.
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try
        {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {}

        try
        {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {}

        if (!gps_enabled && !network_enabled)
        {
            try
            {
                //notify user
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(R.string.gps_network_not_enabled)
                        .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.Cancel, null)
                        .show();
            } catch (Exception ex)
            {
                Log.d(tag, "Exception => " + ex.getMessage());
                Toast.makeText(getBaseContext(), "GPS not enabled, please enable and try again", Toast.LENGTH_LONG);
            }
        }
        if (gps_enabled == true && network_enabled == true)
        {
            try {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, locListener);
            } catch (Exception ex) {
                Log.d(tag, "Error en requestLocationUpdates");
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions, grantResults);
        Log.d(tag,"permissions => " + permissions.length + " grantResults: " + grantResults.length + " request code: " + requestCode);

        switch(requestCode)
        {
            case 0:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(tag,"permissions checked, proceed to get and send location...");
                    Log.d(tag, "lat: " + lat + " lon: " + lon);
                    SendSMS();
                }
                else
                {
                    Toast.makeText(getBaseContext(),"You must enable permissions", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    public void sendingLocation(View view)
    {
        CheckLocationEnabled();
        Log.d(tag,"Trying to send location, first check for permission...");
        try
        {
            if ((ContextCompat.checkSelfPermission(
                    this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
                    && (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                    &&(ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            {
                Log.d(tag,"permissions checked, proceed to get and send location...");
                Log.d(tag, "lat: " + lat + " lon: " + lon);
                SendSMS();
            }
            else
            {
                Log.d(tag,"no permissions detected!");
                requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
            }
        }catch(Exception ex)
        {
            Log.d(tag,"problems when checking for permissions");
        }
    }

    public void SendSMS()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String numero = preferences.getString("numeroConfianza", "");
        Log.d(tag,"Número telefónico => " + numero);
        try
        {
            String url = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, url, null, null);
            Toast.makeText(getBaseContext(), "Mensaje enviado", Toast.LENGTH_LONG).show();
        }catch(Exception ex)
        {
            Toast.makeText(getBaseContext(), "No se pudo enviar el mensaje", Toast.LENGTH_LONG).show();
            Log.d(tag,"SMS fail => " + ex.getMessage());
        }
    }

    public void settings(View view)
    {
        Intent i = new Intent( this, SettingsActivity.class);
        startActivityForResult(i,requestCode);
    }

    public void salir()
    {
        finish();
        System.exit(0);
    }
}