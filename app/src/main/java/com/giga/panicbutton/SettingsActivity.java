package com.giga.panicbutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity
{
    private static String numero;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String numero = preferences.getString("numeroConfianza", "");

        EditText txtnumero = findViewById(R.id.txtnumero);
        txtnumero.setText(numero);
    }

    public void guardar(View view)
    {
        Toast.makeText(getBaseContext(),"El número se ha guardado",Toast.LENGTH_SHORT).show();
        EditText txtnumero = findViewById(R.id.txtnumero);
        numero = txtnumero.getText().toString();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("numeroConfianza", numero);
        editor.apply();
        txtnumero.setText("");
        finish();
    }
}