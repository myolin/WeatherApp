package com.myolin.assignment4;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myolin.assignment4.databinding.HourlyWeatherEntryBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherViewHolder> {

    private final List<Hours> hourlyWeatherList;

    public HourlyWeatherAdapter(List<Hours> hourlyWeatherList) {
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @NonNull
    @Override
    public HourlyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HourlyWeatherEntryBinding binding = HourlyWeatherEntryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HourlyWeatherViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
        SimpleDateFormat sdf2 = new SimpleDateFormat("h:mm a", Locale.US);

        Hours hour = hourlyWeatherList.get(position);
        long dateTimeEpochInMillis = hour.getDateTimeEpoch() * 1000;

        // set day
        holder.binding.hourlyDay.setText(String.format(Locale.getDefault(),
                "%s", sdf.format(new Date(dateTimeEpochInMillis))));

        // set time
        holder.binding.hourlyTime.setText(String.format(Locale.getDefault(),
                "%s", sdf2.format(new Date(dateTimeEpochInMillis))));

        // set icon
        String icon = hour.getIcon();
        icon = icon.replace("-", "_");
        int iconID = MainActivity.getId(icon, R.drawable.class);
        if (iconID == 0) {
            iconID = R.mipmap.ic_launcher;
        }
        holder.binding.hourlyIcon.setImageResource(iconID);

        // set temp
        holder.binding.hourlyTemperature.setText(String.format(Locale.getDefault(),
                "%.0f°%s", hour.getTemp(), MainActivity.unitLetter));

        // set conditions
        holder.binding.hourlyConditions.setText(String.format(Locale.getDefault(),
                "%s", hour.getConditions()));
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }
}
