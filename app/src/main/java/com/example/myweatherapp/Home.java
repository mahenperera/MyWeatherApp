package com.example.myweatherapp;

import static com.example.myweatherapp.Settings.PREFS_NAME;
import static com.example.myweatherapp.Settings.PREF_CITY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity {
    private static final int SETTINGS_REQUEST_CODE = 1;
    private WeatherAdapter weatherAdapter;
    private ArrayList<WeatherItem> weatherDataList;
    private String temperatureUnit = "Celsius";
    private FetchData fetchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences(Settings.PREFS_NAME, MODE_PRIVATE);
        temperatureUnit = prefs.getString(Settings.PREF_TEMPERATURE_UNIT, "Celsius");

        SharedPreferences prefs1 = getSharedPreferences(Settings.PREFS_NAME, MODE_PRIVATE);
        String updatedCity = prefs1.getString(Settings.PREF_CITY, "Colombo");

        TextView textViewCity = findViewById(R.id.current_city);
        textViewCity.setText("City: " + updatedCity + ", Sri Lanka");

        ListView weatherListView = findViewById(R.id.forecast);

        weatherDataList = new ArrayList<>();

        weatherAdapter = new WeatherAdapter(this, weatherDataList, temperatureUnit);

        weatherListView.setAdapter(weatherAdapter);

        weatherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                WeatherItem selectedWeatherItem = (WeatherItem) weatherAdapter.getItem(position);

                Intent intent = new Intent(Home.this, Day.class);
                intent.putExtra("date", selectedWeatherItem.getDate());
                intent.putExtra("day", selectedWeatherItem.getDay());
                intent.putExtra("cityCountry", updatedCity + ", Sri Lanka");
                intent.putExtra("weatherIconResId", selectedWeatherItem.getWeatherIconResId());
                intent.putExtra("temperature", selectedWeatherItem.getTemperatureWithUnit(temperatureUnit));
                intent.putExtra("weatherState", selectedWeatherItem.getSkyState());
                intent.putExtra("humidity", selectedWeatherItem.getHumidity());

                startActivity(intent);
            }
        });

        fetchData = new FetchData(temperatureUnit);
        fetchData.execute();
    }


    public class FetchData extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = "";
        private String temperatureUnit;

        public FetchData(String temperatureUnit) {
            this.temperatureUnit = temperatureUnit;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(forecastJsonStr);
                JSONArray jsonArray = jsonObject.getJSONArray("list");

                weatherDataList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject weather = jsonArray.getJSONObject(i);

                    JSONObject temp = weather.getJSONObject("temp");
                    String temperature = temp.getString("day");

                    JSONArray weatherArray = weather.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String main = weatherObject.getString("main");

                    int humidity = weather.getInt("humidity");

                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE", Locale.getDefault());
                    Date date = new Date(weather.getLong("dt") * 1000);
                    String formattedDate = sdfDate.format(date);
                    String formattedDay = sdfDay.format(date);

                    int weatherIconResId = getWeatherIconResId(main);

                    WeatherItem weatherItem = new WeatherItem(formattedDate, formattedDay, temperature, main, weatherIconResId, humidity);
                    weatherDataList.add(weatherItem);
                }

                weatherAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String city = prefs.getString(PREF_CITY, "Colombo");

                final String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast/daily?q=" + city + "&cnt=7&appid=API_KEY";
                URL url = new URL(BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line1;

                while ((line1 = reader.readLine()) != null) {
                    buffer.append(line1).append("\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }


    public class WeatherItem {
        private String date;
        private String day;
        private String temperature;
        private String skyState;
        private int weatherIconResId;
        private int humidity;

        public WeatherItem(String date, String day, String temperature, String skyState, int weatherIconResId, int humidity) {
            this.day = day;
            this.date = date;
            this.temperature = temperature;
            this.skyState = skyState;
            this.weatherIconResId = weatherIconResId;
            this.humidity = humidity;
        }

        public String getDate() {
            return date;
        }

        public String getDay() {
            return day;
        }

        public String getTemperature() {
            return temperature;
        }

        public String getSkyState() {
            return skyState;
        }

        public int getWeatherIconResId() {
            return weatherIconResId;
        }

        public int getHumidity() {
            return humidity;
        }

        public String getTemperatureWithUnit(String unit) {
            double tempValue = Double.parseDouble(temperature);
            String tempSym = "";
            switch (unit) {
                case "Celsius":
                    tempValue -= 273.15;
                    tempSym = "°C";
                    break;
                case "Fahrenheit":
                    tempValue = (tempValue - 273.15) * 9 / 5 + 32;
                    tempSym = "°F";
                    break;
                default:
                    break;
            }

            return String.format(Locale.getDefault(), "%.2f %s", tempValue, tempSym);
        }
    }


    public class WeatherAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<WeatherItem> weatherDataList;
        private String temperatureUnit;

        public WeatherAdapter(Context context, ArrayList<WeatherItem> weatherDataList, String temperatureUnit) {
            this.context = context;
            this.weatherDataList = weatherDataList;
            this.temperatureUnit = temperatureUnit;
        }

        @Override
        public int getCount() {
            return weatherDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return weatherDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, parent, false);
            }

            WeatherItem weatherItem = (WeatherItem) getItem(position);

            TextView textViewDay = convertView.findViewById(R.id.day);
            TextView textViewSkyState = convertView.findViewById(R.id.sky_state);
            TextView textViewTemp = convertView.findViewById(R.id.temp);
            ImageView imageViewWeatherIcon = convertView.findViewById(R.id.weather_icon);

            textViewDay.setText(weatherItem.getDay());
            textViewSkyState.setText(weatherItem.getSkyState());
            textViewTemp.setText(weatherItem.getTemperatureWithUnit(temperatureUnit));
            imageViewWeatherIcon.setImageResource(weatherItem.getWeatherIconResId());

            return convertView;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.settings) {
            Intent intent = new Intent(Home.this, Settings.class);
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
            return true;
        } else if (itemId == R.id.about) {
            Intent intent = new Intent(Home.this, About.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private int getWeatherIconResId(String main) {
        switch (main) {
            case "Clear":
                return R.drawable.clear;
            case "Rain":
                return R.drawable.rain;
            case "Clouds":
                return R.drawable.cloud;
            case "Thunderstorm":
                return R.drawable.thunderstorm;
            case "Drizzle":
                return R.drawable.drizzle;
            default:
                return R.drawable.ic_default;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            SharedPreferences prefs = getSharedPreferences(Settings.PREFS_NAME, MODE_PRIVATE);
            temperatureUnit = prefs.getString(Settings.PREF_TEMPERATURE_UNIT, "Celsius");

            String updatedCity = prefs.getString(Settings.PREF_CITY, "Colombo");
            TextView textViewCity = findViewById(R.id.current_city);
            textViewCity.setText("City: " + updatedCity + ", Sri Lanka");
        }
    }
}
