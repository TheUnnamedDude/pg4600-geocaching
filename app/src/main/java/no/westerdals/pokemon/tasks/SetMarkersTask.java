package no.westerdals.pokemon.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import no.westerdals.pokemon.PokemonDatabase;
import no.westerdals.pokemon.R;
import no.westerdals.pokemon.model.Pokemon;

public class SetMarkersTask extends AsyncTask<Void, Void, List<Pokemon>> {
    private GoogleMap mMap;
    Context context;

    public SetMarkersTask(GoogleMap mMap, Context context) {
        this.mMap = mMap;
        this.context = context;
    }

    @Override
    protected List<Pokemon> doInBackground(Void... params) {
        PokemonDatabase pokemonDatabase = new PokemonDatabase(context);
        return pokemonDatabase.getAllPokemons();
    }

    @Override
    protected void onPostExecute(List<Pokemon> pokemons) {
        super.onPostExecute(pokemons);
        LatLngBounds.Builder bounds = LatLngBounds.builder();
        for (Pokemon pokemon : pokemons) {
            LatLng latLng = new LatLng(pokemon.getLat(), pokemon.getLng());
            bounds.include(latLng);
            MarkerOptions marker = new MarkerOptions().position(latLng).title(pokemon.getName());
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pokeball));
            if (!pokemon.isCaught()){
                marker.alpha(1f);
            } else {
                marker.alpha(0.3f);
            }

            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 200));
        }
    }
}