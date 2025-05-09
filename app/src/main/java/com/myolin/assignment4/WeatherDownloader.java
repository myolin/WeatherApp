package com.myolin.assignment4;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class WeatherDownloader {

    private static final String apiKey = "TT235E8ML3TTSG434RTG56EPS";
    private static final String baseURL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";

    private static MainActivity mainActivity;
    private static RequestQueue queue;
    private static Weather weatherObj;

    public static void downloadWeather(MainActivity mainActivityIn, String location, String unitLetter) {
        mainActivity = mainActivityIn;
        queue = Volley.newRequestQueue(mainActivity);

        String url = baseURL + location;

        Uri.Builder buildURL = Uri.parse(url).buildUpon();
        if (unitLetter.equals("F")) {
            buildURL.appendQueryParameter("unitGroup", "us");
        } else {
            buildURL.appendQueryParameter("unitGroup", "metric");
        }
        buildURL.appendQueryParameter("key", apiKey);
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener = response -> parseJSON(response.toString());

        Response.ErrorListener error = error1 -> mainActivity.buildDialog(mainActivity.getResources().getString(R.string.location_error_title), mainActivity.getResources().getString(R.string.location_error_message));

        //Request weather data from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse,
                null, listener, error);

        //Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void parseJSON(String s) {
        try {
            JSONObject jObjMain = new JSONObject(s);

            // "latitude" section
            double latitude = jObjMain.getDouble("latitude");

            // "longitude" section
            double longitude = jObjMain.getDouble("longitude");

            // "resolvedAddress section"
            String resolvedAddress = jObjMain.getString("resolvedAddress");

            // "days" section
            JSONArray daysArray = jObjMain.getJSONArray("days");

            Days[] days = new Days[daysArray.length()];

            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject dayObj = (JSONObject) daysArray.get(i);

                double[] dailyFourTemp = new double[4]; // morning, afternoon, evening, night temp

                // get data of a single day
                long daydatetimeEpoch = dayObj.getLong("datetimeEpoch");
                double tempmax = dayObj.getDouble("tempmax");
                double tempmin = dayObj.getDouble("tempmin");
                int precipprob = dayObj.optInt("precipprob", 0);
                int uvindex = dayObj.getInt("uvindex");
                String conditions = dayObj.getString("conditions");
                String description = dayObj.getString("description");
                String icon = dayObj.getString("icon");

                // hourly data of a single day
                JSONArray hoursArray = dayObj.getJSONArray("hours");

                Hours[] hours = new Hours[hoursArray.length()];
                //List<Hours> hours = new ArrayList<>();

                //int index = 0;
                for (int j = 0; j < hoursArray.length(); j++) {
                    JSONObject hourObj = (JSONObject) hoursArray.get(j);

                    // get data of an hour
                    String datetime = hourObj.getString("datetime");
                    long hourdatetimeEpoch = hourObj.getLong("datetimeEpoch");
                    double temp = hourObj.getDouble("temp");
                    double feelslike = hourObj.getDouble("feelslike");
                    double humidity = hourObj.getDouble("humidity");
                    double windgust = hourObj.getDouble("windgust");
                    double windspeed = hourObj.getDouble("windspeed");
                    double winddir = hourObj.getDouble("winddir");
                    double visibility = hourObj.getDouble("visibility");
                    int cloudcover = hourObj.getInt("cloudcover");
                    int houruv = hourObj.getInt("uvindex");
                    String hourConditions = hourObj.getString("conditions");
                    String hourIcon = hourObj.getString("icon");

                    hours[j] = new Hours(datetime, hourdatetimeEpoch, temp, feelslike, humidity,
                            windgust, windspeed, winddir, visibility, cloudcover, houruv,
                            hourConditions, hourIcon);

                    switch (datetime) {
                        case "08:00:00":
                            dailyFourTemp[0] = temp;
                            break;
                        case "13:00:00":
                            dailyFourTemp[1] = temp;
                            break;
                        case "17:00:00":
                            dailyFourTemp[2] = temp;
                            break;
                        case "23:00:00":
                            dailyFourTemp[3] = temp;
                            break;
                    }
                }

                days[i] = new Days(daydatetimeEpoch, tempmax, tempmin, precipprob, uvindex,
                        conditions, description, icon, hours, dailyFourTemp);
            }

            // "alerts" section
            JSONArray alertsArray = jObjMain.getJSONArray("alerts");

            String[] alerts = new String[alertsArray.length()];

            // "currentConditions" section
            JSONObject ccObj = (JSONObject) jObjMain.get("currentConditions");

            long ccdatetimeEpoch = ccObj.getLong("datetimeEpoch");
            double temp = ccObj.getDouble("temp");
            double feelslike = ccObj.getDouble("feelslike");
            double humidity = ccObj.getDouble("humidity");
            double windgust = ccObj.optDouble("windgust", 0);
            double windspeed = ccObj.getDouble("windspeed");
            double winddir = ccObj.getDouble("winddir");
            double visibility = ccObj.optDouble("visibility", 0);
            int cloudcover = ccObj.getInt("cloudcover");
            int uvindex = ccObj.getInt("uvindex");
            String conditions = ccObj.getString("conditions");
            String icon = ccObj.getString("icon");
            long sunriseEpoch = ccObj.getLong("sunriseEpoch");
            long sunsetEpoch = ccObj.getLong("sunsetEpoch");

            CurrentConditions cc = new CurrentConditions(ccdatetimeEpoch, temp, feelslike, humidity,
                    windgust, windspeed, winddir, visibility, cloudcover, uvindex, conditions,
                    icon, sunriseEpoch, sunsetEpoch);

            // Create a weather object
            weatherObj = new Weather(latitude, longitude, resolvedAddress, days, alerts, cc);

            mainActivity.updateData(weatherObj);

        } catch (Exception e) {
            Log.d("WeatherDownloader", "parseJSON: " + e.getMessage());
            mainActivity.buildDialog(mainActivity.getResources().getString(R.string.weather_data_error_title), mainActivity.getResources().getString(R.string.weather_data_error_message));
        }
    }

}
