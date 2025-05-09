package com.myolin.assignment4;

import androidx.recyclerview.widget.RecyclerView;

import com.myolin.assignment4.databinding.HourlyWeatherEntryBinding;

public class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {

    HourlyWeatherEntryBinding binding;

    public HourlyWeatherViewHolder(HourlyWeatherEntryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
