package com.myolin.assignment4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.myolin.assignment4.databinding.ActivityMainBinding;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private final List<Hours> hourlyWeatherList = new ArrayList<>();

    private ActivityMainBinding binding;
    private HourlyWeatherAdapter hourlyWeatherAdapter;
    private ConnectivityManager connectivityManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 10;
    private ChartMaker chartMaker;
    private Weather weather;
    private String city;
    public static String unitLetter = "F";
    private String windUnit = "mph";
    private String visiUnit = "mi";
    private String postalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        connectivityManager = getSystemService(ConnectivityManager.class);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        chartMaker = new ChartMaker(this, binding);

        hourlyWeatherAdapter = new HourlyWeatherAdapter(hourlyWeatherList);
        binding.hourlyRecycler.setAdapter(hourlyWeatherAdapter);
        binding.hourlyRecycler.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        // check for internet connection
        if (checkNetwork()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            determineLocation();
        }

        binding.main.setOnRefreshListener(() -> {
            WeatherDownloader.downloadWeather(this, city, unitLetter);
            binding.main.setRefreshing(false);
        });

    }

    public void updateData(Weather weatherIn) {
        binding.progressBar.setVisibility(View.GONE);
        if (weatherIn == null) {
            return;
        }
        weather = weatherIn;

        SimpleDateFormat sdf = new SimpleDateFormat("E MMM d h:mm a", Locale.US);
        SimpleDateFormat sdf2 = new SimpleDateFormat("h:mm a", Locale.US);

        CurrentConditions cc = weather.getCurrentConditions();

        // set color gradient for main layout
        ColorMaker.setColorGradient(binding.main, cc.getTemp(), unitLetter);

        // set color gradient for icon bar
        ColorMaker.setColorGradient(binding.mainIconBar, cc.getTemp(), unitLetter);

        // set the location and current time
        StringBuilder sb = new StringBuilder();
        String[] resolvedAddressArray = weather.getResolvedAddress().split(",");
        city = resolvedAddressArray[0];
        sb.append(city);
        sb.append(", ");
        sb.append(sdf.format(new Date(System.currentTimeMillis())));
        binding.mainLocationTime.setText(sb.toString());

        binding.mainTemperature.setText(String.format(Locale.getDefault(),
                "%.0f°%s", cc.getTemp(), unitLetter));
        binding.mainFeelLikes.setText(String.format(Locale.getDefault(),
                "Feels Like %.0f°%s", cc.getFeelslike(), unitLetter));
        binding.mainConditions.setText(String.format(Locale.getDefault(),
                "%s (%d%% clouds)", cc.getConditions(), cc.getCloudcover()));
        binding.mainWind.setText(String.format(Locale.getDefault(),
                "Winds: %s at %.0f %s gusting to %.0f %s", getDirection(cc.getWinddir()),
                cc.getWindspeed(), windUnit, cc.getWindgust(), windUnit));
        binding.mainHumidity.setText(String.format(Locale.getDefault(),
                "Humidity: %.0f%%", cc.getHumidity()));
        binding.mainUv.setText(String.format(Locale.getDefault(), "UV Index: %d", cc.getUvindex()));
        binding.mainVisibility.setText(String.format(Locale.getDefault(),
                "Visibility: %.1f %s", cc.getVisibility(), visiUnit));

        // Get resource ID of desired icon and set icon
        String icon = cc.getIcon();
        icon = icon.replace("-", "_");
        int iconID = getId(icon, R.drawable.class);
        if (iconID == 0) {
            iconID = R.mipmap.ic_launcher;
        }
        binding.mainIcon.setImageResource(iconID);

        // convert sunrise epoch time to Date
        long sunriseEpoch = cc.getSunriseEpoch();
        long sunriseEpochInMillis = sunriseEpoch * 1000;
        binding.mainSunrise.setText(String.format(Locale.getDefault(), "Sunrise: %s",
                sdf2.format(new Date(sunriseEpochInMillis))));

        // convert sunset epoch time to Date
        long sunsetEpoch = cc.getSunsetEpoch();
        long sunsetEpochInMillis = sunsetEpoch * 1000;
        binding.mainSunset.setText(String.format(Locale.getDefault(), "Sunset: %s",
                sdf2.format(new Date(sunsetEpochInMillis))));

        // create a tree map of temperature points with hourly time and draw chart
        TreeMap<String, Double> tempPoints = makeTemperaturePoints(weather);
        chartMaker.makeChart(tempPoints);

        // set hourly weather data and notify adapter
        hourlyWeatherList.clear();
        Days[] days = weather.getDays();
        for (Days day : days) {
            Hours[] hours = day.getHours();
            for (Hours hour : hours) {
                if (System.currentTimeMillis() < (hour.getDateTimeEpoch() * 1000)) {
                    hourlyWeatherList.add(hour);
                }
            }
        }
        hourlyWeatherAdapter.notifyItemRangeChanged(0, hourlyWeatherList.size());

        // set alpha to hourly recycler view background
        binding.hourlyRecycler.setBackgroundColor(getResources().getColor(R.color.white));
        binding.hourlyRecycler.getBackground().setAlpha(50);

    }

    public boolean checkNetwork() {
        Network currentNetwork = connectivityManager.getActiveNetwork();

        if (currentNetwork == null) {
            buildDialog(getString(R.string.no_internet_title), getString(R.string.no_internet_message));
            return false;
        }

        return true;
    }

    public void buildDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.alert);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, id) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void determineLocation() {
        // Check for location permission - if not then start the request and return
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        // location permission is granted
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // get location name
                        String currentLoc = getLocationName(location.getLatitude(), location.getLongitude());
                        WeatherDownloader.downloadWeather(this, currentLoc, unitLetter);
                    } else {
                        Log.d("MainActivity", "determineLocation: NULL LOCATION");
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.d("MainActivity", "determineLocation: FAILURE");
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    //error
                }
            }
        }
    }

    private String getLocationName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        StringBuilder sb = new StringBuilder();
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String cityName = addresses.get(0).getLocality();
                postalCode = addresses.get(0).getPostalCode(); // get postal code of location
                sb.append(cityName);
            }
        } catch (IOException e) {
            Log.d("MainActivity", "getLocationName: " + e.getMessage());
        }

        return sb.toString();
    }

    private TreeMap<String, Double> makeTemperaturePoints(Weather weather) {
        TreeMap<String, Double> timeTempValues = new TreeMap<>();

        Days day1 = weather.getDays()[0];
        Hours[] day1HourlyData = day1.getHours();
        for (Hours data : day1HourlyData) {
            timeTempValues.put(data.getDateTime(), data.getTemp());
        }

        Days day2 = weather.getDays()[1];
        Hours day2FirstHour = day2.getHours()[0];
        timeTempValues.put("24:00:00", day2FirstHour.getTemp());

        return timeTempValues;
    }

    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            return 0;
        }
    }

    public void goToDailyForecastActivity(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra("Data", weather);
        intent.putExtra("City", city);
        startActivity(intent);
    }

    public void enterLocationDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        builder.setPositiveButton("OK", (dialog, id) -> WeatherDownloader.downloadWeather(this, et.getText().toString(), unitLetter));

        builder.setNegativeButton("CANCEL", (dialog, id) -> {
        });

        builder.setTitle(getString(R.string.location_input_title));
        builder.setMessage(getString(R.string.location_input_message));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void toggleUnit(View view) {
        if (unitLetter.equals("F")) {
            unitLetter = "C";
            windUnit = "kph";
            visiUnit = "km";
            binding.mainScaleIcon.setImageResource(R.drawable.units_c);
        } else {
            unitLetter = "F";
            windUnit = "mph";
            visiUnit = "mi";
            binding.mainScaleIcon.setImageResource(R.drawable.units_f);
        }
        WeatherDownloader.downloadWeather(this, city, unitLetter);
    }

    public void resetToCurrentLocation(View view) {
        determineLocation();
    }

    public void goToMap(View view) {
        String location = weather.getResolvedAddress();

        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);

        // Check if there is an app that can handle geo intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No Application found that handles ACTION_VIEW (geo) intents", Toast.LENGTH_SHORT).show();
        }
    }

    public void doShare(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        sendIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(Locale.getDefault(),
                "Weather for %s (%s)", city, postalCode));

        CurrentConditions cc = weather.getCurrentConditions();
        Days day1 = weather.getDays()[0];
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.getDefault(), "Weather for %s (%s):\n\n", city, postalCode));
        sb.append(String.format(Locale.getDefault(), "Forecast: %s with a high of %.1f°%s and a low of %.1f°%s.\n\n", cc.getConditions(), day1.getMaxTemp(), unitLetter, day1.getMinTemp(), unitLetter));
        sb.append(String.format(Locale.getDefault(), "Now: %.1f°%s, %s (Feels like: %.1f°%s)\n\n", cc.getTemp(), unitLetter, cc.getConditions(), cc.getFeelslike(), unitLetter));
        sb.append(String.format(Locale.getDefault(), "Humidity: %.1f%%\n", cc.getHumidity()));
        sb.append(String.format(Locale.getDefault(), "Winds: %s at %.1f %s\n", getDirection(cc.getWinddir()), cc.getWindspeed(), windUnit));
        sb.append(String.format(Locale.getDefault(), "UV Index: %d\n", cc.getUvindex()));

        // convert sunrise epoch time to Date
        long sunriseEpoch = cc.getSunriseEpoch();
        long sunriseEpochInMillis = sunriseEpoch * 1000;
        sb.append(String.format(Locale.getDefault(), "Sunrise: %s\n", sdf.format(new Date(sunriseEpochInMillis))));

        // convert sunset epoch time to Date
        long sunsetEpoch = cc.getSunsetEpoch();
        long sunsetEpochInMillis = sunsetEpoch * 1000;
        sb.append(String.format(Locale.getDefault(), "Sunset: %s\n", sdf.format(new Date(sunsetEpochInMillis))));

        sb.append(String.format(Locale.getDefault(), "Visibility: %.1f %s", cc.getVisibility(), visiUnit));

        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());

        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share weather to...");
        startActivity(shareIntent);
    }

    public void displayChartTemp(float time, float tempVal) {
        SimpleDateFormat sdf = new SimpleDateFormat("h a", Locale.US);
        Date d = new Date((long) time);
        binding.chartTemp.setText(String.format(Locale.getDefault(), "%s, %.0f°", sdf.format(d), tempVal));
        binding.chartTemp.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                runOnUiThread(() -> binding.chartTemp.setVisibility(View.GONE));
            } catch (InterruptedException e) {
                Log.d("MainActivity", "displayChartTemp: " + e.getMessage());
            }
        }).start();
    }
}