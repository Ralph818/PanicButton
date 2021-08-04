package com.giga.panicbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
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

import java.util.List;


public class MainActivity extends AppCompatActivity {
    static final String tag = "log-principal";
    private String numero = "2291521851";

    // newest
    LocationManager lm;
    Location l;
    double lon;
    double lat;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(tag,"OnCreate: Lacking permissions");
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 10, locListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 12000, 10, locListener);
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
                    SendSMS(numero);

                    //get Location
                    /*Location location = getLastKnownLocation();
                    if (location != null){}
                    //SendSMS(numero,location);
                    else
                        Toast.makeText(getBaseContext(), "No location found", Toast.LENGTH_SHORT).show();*/
                }
                else
                {
                    Toast.makeText(getBaseContext(),"You must enable permissions", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                }
                break;
        }
    }

    // Buscar ubicación con distintos proveedores
    /*private Location getLastKnownLocation()
    {
        LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers)
        {
            if(ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l== null)
            {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
            {
                bestLocation = l;
            }
        }
        return bestLocation;
    }*/

    public void sendingLocation(View view)
    {
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
                SendSMS(numero);

                /*Location location = getLastKnownLocation();

                if (location != null){
                    Log.d(tag, "[sendingLocation] lon: " + location.getLongitude() + " lat: " + location.getLatitude());
                    SendSMS(numero, location);
                }
                else
                    Toast.makeText(getBaseContext(), "No location found", Toast.LENGTH_SHORT).show();*/

                //get Location
                /*Location location = getLastKnownLocation();
                if (location != null){
                    Log.d(tag, "[sendingLocation] lon: " + location.getLongitude() + " lat: " + location.getLatitude());
                    SendSMS(numero, location);
                }
                else
                    Toast.makeText(getBaseContext(), "No location found", Toast.LENGTH_SHORT).show();*/

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

    public void SendSMS(String numero)
    {
        try
        {
            //String latitud = String.valueOf(location.getLatitude());
            //String longitud = String.valueOf(location.getLongitude());
            String url = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, url, null, null);
            //Log.d(tag, "SMS send message" + " number: " + numero + " texto: " + location.toString());
            Toast.makeText(getBaseContext(), "Mensaje enviado", Toast.LENGTH_LONG).show();
        }catch(Exception ex)
        {
            Toast.makeText(getBaseContext(), "No se pudo enviar el mensaje", Toast.LENGTH_LONG).show();
            Log.d(tag,"SMS fail => " + ex.getMessage());
        }

    }
}