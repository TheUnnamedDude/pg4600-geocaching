package no.westerdals.pokemon.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import no.westerdals.pokemon.LehmannApi;
import no.westerdals.pokemon.PokemonDatabase;
import no.westerdals.pokemon.activities.PokemonMapActivity;
import no.westerdals.pokemon.model.Pokemon;

public class UpdateCacheTask extends AsyncTask<Void, Void, Void> {
    private LehmannApi lehmannApi;
    private PokemonDatabase pokemonDatabase;
    private PokemonMapActivity mapActivity;

    public UpdateCacheTask(PokemonMapActivity mapActivity, PokemonDatabase pokemonDatabase, LehmannApi lehmannApi) {
        this.mapActivity = mapActivity;
        this.lehmannApi = lehmannApi;
        this.pokemonDatabase = pokemonDatabase;
    }
    @Override
    protected Void doInBackground(Void... params) {
        pokemonDatabase.deleteUncaughtPokemon();
        Pokemon[] pokemons = lehmannApi.getLocations();
        for (Pokemon pokemon : pokemons) {
            pokemonDatabase.insertPokemon(pokemon);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        if (mapActivity.mMap != null) {
            mapActivity.updateMarkers();
        }
    }
}
