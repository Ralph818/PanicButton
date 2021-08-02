package com.giga.panicbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;



public class MainActivity extends AppCompatActivity
{
    static final String tag = "log-principal";
    private String numero = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SuppressLint("MissingPermission")
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
                    //obtener y enviar ubicación
                    LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    LocationListener locationListener = new LocationListener()
                    {
                        @Override
                        public void onLocationChanged(@NonNull Location location)
                        {
                            //Log.d(tag,"entré a onLocationChagned...");
                            //SendSMS("+522291521851", location);
                        }
                    };

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
                    //get Location
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    SendSMS("+522291521851",location);

                }
                else
                {
                    Toast.makeText(getBaseContext(),"You must enable permissions", Toast.LENGTH_SHORT).show();
                    //requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                }
                break;
        }
    }

    public void sendingLocation(View view)
    {
        Log.d(tag,"Trying to send location, first check for permission...");
        try
        {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
                    && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                    &&(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            {
                Log.d(tag,"permissions checked, proceed to get and send location...");
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                LocationListener locationListener = new LocationListener()
                {
                    @Override
                    public void onLocationChanged(@NonNull Location location)
                    {
                        //Log.d(tag,"entré a onLocationChagned...");
                        //SendSMS("+522291521851", location);
                    }
                };
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
                //get Location
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                SendSMS("+522291521851", location);
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

    public void SendSMS(String numero, Location location)
    {
        try
        {
            String ubicación = "Latitud: " + location.getLatitude() + "\nLongitud: " + location.getLongitude() + "\nAltitud: " + location.getAltitude();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, ubicación, null, null);
            Log.d(tag, "SMS send message" + " number: " + numero + " texto: " + location.toString());
        }catch(Exception ex){
            Log.d(tag,"SMS fail => " + ex.getMessage());
        }
        Toast.makeText(getBaseContext(), "Message has been sent", Toast.LENGTH_LONG).show();
    }
}