package com.example.aydogan.TravApp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;

import java.util.List;

/**
 * Edited 01.05.2018 by Aydogan Musa
 * Hier word een recyclerview aangemaakt voor onze nearbyplaces sectie van de app de data die in de nearbyplacesfragment klasse worden
 * binnen gehaald vanuit de Google Places API worden hier doorgegeven aan de view via een recyclerview
 */

class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder> {
    private List<Place> placesList;
    private Context context;

    PlacesRecyclerViewAdapter(List<Place> list, Context ctx) {
        this.placesList = list;
        this.context = ctx;
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    @Override
    public PlacesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesRecyclerViewAdapter.ViewHolder holder, final int position) {
        final int itemPos = position;
        final Place place = placesList.get(position);
        holder.name.setText(place.getName());
        holder.address.setText(place.getAddress());
        holder.phone.setText(place.getPhoneNumber());

        if (place.getWebsiteUri() != null) {
            holder.website.setText(place.getWebsiteUri().toString());
        }

        if (place.getRating() > -1) {
            holder.ratingBar.setNumStars((int) place.getRating());
        } else {
            holder.ratingBar.setVisibility(View.GONE);
        }

        holder.viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOnMap(place);
            }
        });
    }

    private void showOnMap(Place place) {
        Intent intent = null;
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + place.getAddress()));
        intent.setPackage("com.google.android.apps.maps");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + place.getAddress()));
                context.startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(context, "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView address;
        TextView phone;
        TextView website;
        RatingBar ratingBar;
        Button viewOnMap;


        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            address = view.findViewById(R.id.address);
            phone = view.findViewById(R.id.phone);
            website = view.findViewById(R.id.website);
            ratingBar = view.findViewById(R.id.rating);
            viewOnMap = view.findViewById(R.id.view_map_b);
        }

    }
}
