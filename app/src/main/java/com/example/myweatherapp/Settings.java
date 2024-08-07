package com.example.myweatherapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefs";
    public static final String PREF_CITY = "city";
    public static final String PREF_TEMPERATURE_UNIT = "temperatureUnit";
    private Button LogoutBtn;
    private Button RefreshBtn;

    private ListView listViewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LogoutBtn =  (Button) findViewById(R.id.logoutbtn);
        RefreshBtn = (Button) findViewById(R.id.refresh_btn);

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginPage();
            }
        });

        RefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeatherHome();
            }
        });


        listViewSettings = findViewById(R.id.listview_settings);

        String[] settingsOptions = {"Change City", "Change Temperature Unit"};
        String[] settingsDescriptions = {"Change the city setting", "Change the temperature unit setting"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_settings, R.id.textViewOption, settingsOptions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textViewDescription = view.findViewById(R.id.textViewDescription);
                textViewDescription.setText(settingsDescriptions[position]);

                return view;
            }
        };

        listViewSettings.setAdapter(adapter);

        listViewSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = settingsOptions[position];
                String selectedDescription = settingsDescriptions[position];

                handleListItemClick(position);
            }
        });
    }

    private void handleListItemClick(int position) {
        switch (position) {
            case 0:
                showCityDialog();
                break;
            case 1:
                showTemperatureUnitDialog();
                break;
        }
    }

    private void showCityDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.city_dialogbox, null);
        final EditText editTextCity = dialogView.findViewById(R.id.city_edit_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change City")
                .setView(dialogView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newCity = editTextCity.getText().toString();
                        saveCity(newCity);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("city", newCity);
                        setResult(RESULT_OK, resultIntent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void showTemperatureUnitDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.select_unit_dialogbox, null);
        final RadioButton radioButtonCelsius = dialogView.findViewById(R.id.radioButtonCelsius);
        final RadioButton radioButtonFahrenheit = dialogView.findViewById(R.id.radioButtonFahrenheit);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedTemperatureUnit = prefs.getString(PREF_TEMPERATURE_UNIT, "Celsius");

        if (savedTemperatureUnit.equals("Celsius")) {
            radioButtonCelsius.setChecked(true);
        } else {
            radioButtonFahrenheit.setChecked(true);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Temperature Unit")
                .setView(dialogView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveTemperatureUnit(radioButtonCelsius.isChecked() ? "Celsius" : "Fahrenheit");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveCity(String city) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(PREF_CITY, city);
        editor.apply();

        Toast.makeText(this, "City changed to " + city, Toast.LENGTH_SHORT).show();
    }

    private void saveTemperatureUnit(String temperatureUnit) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(PREF_TEMPERATURE_UNIT, temperatureUnit);
        editor.apply();

        Toast.makeText(this, "Temperature unit changed to " + temperatureUnit, Toast.LENGTH_SHORT).show();
    }

    public void openLoginPage() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void openWeatherHome() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}

