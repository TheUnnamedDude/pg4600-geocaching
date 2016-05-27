package no.westerdals.pokemon;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class PokemonLocationTask extends AsyncTask<Void, Void, PokemonLocation[]> {
    private final static Gson GSON = new Gson();
    private GoogleMap map;

    public PokemonLocationTask(GoogleMap map) {
        this.map = map;
    }

    @Override
    protected PokemonLocation[] doInBackground(Void... params) {
        try {
            PokemonLocation[] pokemonLocations = GSON.fromJson(
                    new InputStreamReader(
                            new URL("https://locations.lehmann.tech/locations")
                                    .openStream()), PokemonLocation[].class);
            return pokemonLocations;
        } catch (IOException e) {
            e.printStackTrace();
            return new PokemonLocation[0];
        }
    }

    @Override
    protected void onPostExecute(PokemonLocation[] locations) {
        LatLngBounds.Builder bounds = LatLngBounds.builder();
        for (PokemonLocation location : locations) {
            // TODO: check against db if pokemon is captured
            LatLng latLng = new LatLng(location.getLat(), location.getLng());
            map.addMarker(new MarkerOptions().position(latLng).title(location.getName()));
            bounds.include(latLng);

            System.out.println("Pokemon: " + location.toString());
        }
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 200));
    }
}
