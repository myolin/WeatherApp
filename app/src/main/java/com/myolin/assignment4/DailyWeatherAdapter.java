package com.myolin.assignment4;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myolin.assignment4.databinding.DailyWeatherEntryBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherViewHolder> {

    private final List<Days> dailyWeatherList;

    public DailyWeatherAdapter(List<Days> dailyWeatherList) {
        this.dailyWeatherList = dailyWeatherList;
    }

    @NonNull
    @Override
    public DailyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DailyWeatherEntryBinding binding = DailyWeatherEntryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DailyWeatherViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyWeatherViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, M/d", Locale.US);

        Days day = dailyWeatherList.get(position);
        long dateTimeEpochInMillis = day.getDateTimeEpoch() * 1000;

        // set color gradient
        ColorMaker.setColorGradient(holder.binding.dailyDate, day.getMaxTemp(), MainActivity.unitLetter);
        ColorMaker.setColorGradient(holder.binding.main, day.getMaxTemp(), MainActivity.unitLetter);

        // set day and date
        holder.binding.dailyDate.setText(String.format(Locale.getDefault(), "%s",
                sdf.format(new Date(dateTimeEpochInMillis))));

        // set temp
        double maxTemp = day.getMaxTemp();
        double minTemp = day.getMinTemp();
        holder.binding.dailyTemperature.setText(String.format(Locale.getDefault(),
                "%.0f°%s/%.0f°%s", maxTemp, MainActivity.unitLetter, minTemp, MainActivity.unitLetter));

        // set description
        holder.binding.dailyDescription.setText(String.format(Locale.getDefault(),
                "%s", day.getDescription()));

        // set precipitation
        holder.binding.dailyPrecipitation.setText(String.format(Locale.getDefault(),
                "(%d%% precip.)", day.getPrecip()));

        // set uv
        holder.binding.dailyUv.setText(String.format(Locale.getDefault(),
                "UV Index: %d", day.getUv()));

        // set icon
        String icon = day.getIcon();
        icon = icon.replace("-", "_");
        int iconID = MainActivity.getId(icon, R.drawable.class);
        if (iconID == 0) {
            iconID = R.mipmap.ic_launcher;
        }
        holder.binding.dailyIcon.setImageResource(iconID);

        // set morning temp
        double[] dailyFourTemp = day.getDailyFourTemp();
        holder.binding.dailyMorningTemp.setText(String.format(Locale.getDefault(),
                "%.0f°%s", dailyFourTemp[0], MainActivity.unitLetter));

        // set afternoon temp
        holder.binding.dailyAfternoonTemp.setText(String.format(Locale.getDefault(),
                "%.0f°%s", dailyFourTemp[1], MainActivity.unitLetter));

        // set evening temp
        holder.binding.dailyEveningTemp.setText(String.format(Locale.getDefault(),
                "%.0f°%s", dailyFourTemp[2], MainActivity.unitLetter));

        // set night temp
        holder.binding.dailyNightTemp.setText(String.format(Locale.getDefault(),
                "%.0f°%s", dailyFourTemp[3], MainActivity.unitLetter));
    }

    @Override
    public int getItemCount() {
        return dailyWeatherList.size();
    }
}
