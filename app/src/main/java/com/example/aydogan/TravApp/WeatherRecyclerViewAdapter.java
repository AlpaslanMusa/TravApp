package com.example.aydogan.TravApp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aydogan on 26.03.18.
 */

class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.ViewHolder> {
    private List<String[]> weatherList;
    private Context context;

    WeatherRecyclerViewAdapter(List<String[]> list, Context ctx) {
        this.weatherList = list;
        this.context = ctx;
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    @Override
    public WeatherRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherRecyclerViewAdapter.ViewHolder holder, final int position) {
        final int itemPos = position;
        final String[] weather = weatherList.get(position);
        holder.temperatuur.setText(weather[0]);
        holder.luchtvochtigheid.setText(weather[1]);
        holder.luchtdruk.setText(weather[2]);
        holder.weerbericht.setText(weather[3]);
        holder.tijd.setText(weather[4]);
        switch (weather[5]) {
            case "01d":
                holder.icon.setImageResource(R.drawable.ic_01d);
                break;
            case "01n":
                holder.icon.setImageResource(R.drawable.ic_01n);
                break;
            case "02d":
                holder.icon.setImageResource(R.drawable.ic_02d);
                break;
            case "02n":
                holder.icon.setImageResource(R.drawable.ic_02n);
                break;
            case "03d":
                holder.icon.setImageResource(R.drawable.ic_03d);
                break;
            case "03n":
                holder.icon.setImageResource(R.drawable.ic_03d);
                break;
            case "04d":
                holder.icon.setImageResource(R.drawable.ic_04d);
                break;
            case "04n":
                holder.icon.setImageResource(R.drawable.ic_04d);
                break;
            case "09d":
                holder.icon.setImageResource(R.drawable.ic_09d);
                break;
            case "09n":
                holder.icon.setImageResource(R.drawable.ic_09d);
                break;
            case "10d":
                holder.icon.setImageResource(R.drawable.ic_10d);
                break;
            case "10n":
                holder.icon.setImageResource(R.drawable.ic_10n);
                break;
            case "11d":
                holder.icon.setImageResource(R.drawable.ic_11d);
                break;
            case "11n":
                holder.icon.setImageResource(R.drawable.ic_11d);
                break;
            case "13d":
                holder.icon.setImageResource(R.drawable.ic_13d);
                break;
            case "13n":
                holder.icon.setImageResource(R.drawable.ic_13d);
                break;
            case "50d":
                holder.icon.setImageResource(R.drawable.ic_50d);
                break;
            case "50n":
                holder.icon.setImageResource(R.drawable.ic_50d);
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView temperatuur;
        TextView luchtvochtigheid;
        TextView luchtdruk;
        TextView weerbericht;
        TextView tijd;
        ImageView icon;

        ViewHolder(View view) {
            super(view);
            temperatuur = view.findViewById(R.id.temperatuur);
            luchtvochtigheid = view.findViewById(R.id.luchtvochtigheid);
            luchtdruk = view.findViewById(R.id.luchtdruk);
            weerbericht = view.findViewById(R.id.weerbericht);
            tijd = view.findViewById(R.id.tijd);
            icon = view.findViewById(R.id.icoon);
        }

    }
}
