package com.myolin.assignment4;

import androidx.recyclerview.widget.RecyclerView;

import com.myolin.assignment4.databinding.DailyWeatherEntryBinding;

public class DailyWeatherViewHolder extends RecyclerView.ViewHolder {

    DailyWeatherEntryBinding binding;

    public DailyWeatherViewHolder(DailyWeatherEntryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
