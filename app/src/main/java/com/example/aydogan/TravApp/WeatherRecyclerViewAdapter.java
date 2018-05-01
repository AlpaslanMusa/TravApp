package com.example.aydogan.TravApp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aydogan on 26.03.18.
 */

public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.ViewHolder> {
    private List<String[]> weatherList;
    private Context context;

    public WeatherRecyclerViewAdapter(List<String[]> list, Context ctx) {
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
        WeatherRecyclerViewAdapter.ViewHolder viewHolder = new WeatherRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
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
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView temperatuur;
        public TextView luchtvochtigheid;
        public TextView luchtdruk;
        public TextView weerbericht;
        public TextView tijd;

        public ViewHolder(View view) {
            super(view);
            temperatuur = view.findViewById(R.id.temperatuur);
            luchtvochtigheid = view.findViewById(R.id.luchtvochtigheid);
            luchtdruk = view.findViewById(R.id.luchtdruk);
            weerbericht = view.findViewById(R.id.weerbericht);
            tijd = view.findViewById(R.id.tijd);
        }

    }
}
