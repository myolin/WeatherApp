package com.myolin.assignment4;

import java.io.Serializable;

public class Weather implements Serializable {

    private final double latitude;
    private final double longitude;
    private final String resolvedAddress;
    private final Days[] days;
    private final String[] alerts;
    private final CurrentConditions currentConditions;

    public Weather(double latitude, double longitude, String resolvedAddress, Days[] days,
                   String[] alerts, CurrentConditions currentConditions) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.resolvedAddress = resolvedAddress;
        this.days = days;
        this.alerts = alerts;
        this.currentConditions = currentConditions;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getResolvedAddress() {
        return resolvedAddress;
    }

    public Days[] getDays() {
        return days;
    }

    public String[] getAlerts() {
        return alerts;
    }

    public CurrentConditions getCurrentConditions() {
        return currentConditions;
    }
}

class Days implements Serializable {
    private final long dateTimeEpoch;
    private final double maxTemp;
    private final double minTemp;
    private final int precip;
    private final int uv;
    private final String conditions;
    private final String description;
    private final String icon;
    private final Hours[] hours;
    private final double[] dailyFourTemp;

    public double[] getDailyFourTemp() {
        return dailyFourTemp;
    }

    public Days(long dateTimeEpoch, double maxTemp, double minTemp, int precip, int uv, String conditions,
                String description, String icon, Hours[] hours, double[] dailyFourTemp) {
        this.dateTimeEpoch = dateTimeEpoch;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.precip = precip;
        this.uv = uv;
        this.conditions = conditions;
        this.description = description;
        this.icon = icon;
        this.hours = hours;
        this.dailyFourTemp = dailyFourTemp;
    }

    public long getDateTimeEpoch() {
        return dateTimeEpoch;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public int getPrecip() {
        return precip;
    }

    public int getUv() {
        return uv;
    }

    public String getConditions() {
        return conditions;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public Hours[] getHours() {
        return hours;
    }
}

class Hours implements Serializable {
    private final String dateTime;
    private final long dateTimeEpoch;
    private final double temp;
    private final double feelslike;
    private final double humidity;
    private final double windgust;
    private final double windspeed;
    private final double winddir;
    private final double visibility;
    private final int cloudcover;
    private final int uvindex;
    private final String conditions;
    private final String icon;

    Hours(String dateTime, long dateTimeEpoch, double temp, double feelslike, double humidity,
          double windgust, double windspeed, double winddir, double visibility, int cloudcover,
          int uvindex, String conditions, String icon) {
        this.dateTime = dateTime;
        this.dateTimeEpoch = dateTimeEpoch;
        this.temp = temp;
        this.feelslike = feelslike;
        this.humidity = humidity;
        this.windgust = windgust;
        this.windspeed = windspeed;
        this.winddir = winddir;
        this.visibility = visibility;
        this.cloudcover = cloudcover;
        this.uvindex = uvindex;
        this.conditions = conditions;
        this.icon = icon;
    }

    public String getDateTime() {
        return dateTime;
    }

    public long getDateTimeEpoch() {
        return dateTimeEpoch;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeelslike() {
        return feelslike;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindgust() {
        return windgust;
    }

    public double getWindspeed() {
        return windspeed;
    }

    public double getWinddir() {
        return winddir;
    }

    public double getVisibility() {
        return visibility;
    }

    public int getCloudcover() {
        return cloudcover;
    }

    public int getUvindex() {
        return uvindex;
    }

    public String getConditions() {
        return conditions;
    }

    public String getIcon() {
        return icon;
    }
}

class CurrentConditions implements Serializable {
    private final long dateTimeEpoch;
    private final double temp;
    private final double feelslike;
    private final double humidity;
    private final double windgust;
    private final double windspeed;
    private final double winddir;
    private final double visibility;
    private final int cloudcover;
    private final int uvindex;
    private final String conditions;
    private final String icon;
    private final long sunriseEpoch;
    private final long sunsetEpoch;

    public CurrentConditions(long dateTimeEpoch, double temp, double feelslike, double humidity,
                             double windgust, double windspeed, double winddir, double visibility,
                             int cloudcover, int uvindex, String conditions, String icon,
                             long sunriseEpoch, long sunsetEpoch) {

        this.dateTimeEpoch = dateTimeEpoch;
        this.temp = temp;
        this.feelslike = feelslike;
        this.humidity = humidity;
        this.windgust = windgust;
        this.windspeed = windspeed;
        this.winddir = winddir;
        this.visibility = visibility;
        this.cloudcover = cloudcover;
        this.uvindex = uvindex;
        this.conditions = conditions;
        this.icon = icon;
        this.sunriseEpoch = sunriseEpoch;
        this.sunsetEpoch = sunsetEpoch;
    }

    public long getDateTimeEpoch() {
        return dateTimeEpoch;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeelslike() {
        return feelslike;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindgust() {
        return windgust;
    }

    public double getWindspeed() {
        return windspeed;
    }

    public double getWinddir() {
        return winddir;
    }

    public double getVisibility() {
        return visibility;
    }

    public int getCloudcover() {
        return cloudcover;
    }

    public int getUvindex() {
        return uvindex;
    }

    public String getConditions() {
        return conditions;
    }

    public String getIcon() {
        return icon;
    }

    public long getSunriseEpoch() {
        return sunriseEpoch;
    }

    public long getSunsetEpoch() {
        return sunsetEpoch;
    }
}

