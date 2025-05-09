package com.myolin.assignment4;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.myolin.assignment4.databinding.ActivityDailyForecastBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DailyForecastActivity extends AppCompatActivity {

    private ActivityDailyForecastBinding binding;
    private Weather weather;
    private final List<Days> dailyWeatherList = new ArrayList<>();
    private DailyWeatherAdapter dailyWeatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDailyForecastBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dailyWeatherAdapter = new DailyWeatherAdapter(dailyWeatherList);
        binding.dailyRecycler.setAdapter(dailyWeatherAdapter);
        binding.dailyRecycler.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent.hasExtra("Data")) {
            weather = (Weather) intent.getSerializableExtra("Data");
        }

        if (intent.hasExtra("City")) {
            binding.dailyForecastTitle.setText(String.format(Locale.getDefault(),
                    "%s 15-Day Forecast", intent.getStringExtra("City")));
        }

        setDailyWeatherData();
    }

    private void setDailyWeatherData() {
        if (weather == null) {
            return;
        }
        dailyWeatherList.addAll(Arrays.asList(weather.getDays()));
        dailyWeatherAdapter.notifyItemRangeChanged(0, dailyWeatherList.size());
    }
}