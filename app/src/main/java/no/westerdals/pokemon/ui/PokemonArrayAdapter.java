package no.westerdals.pokemon.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import no.westerdals.pokemon.R;
import no.westerdals.pokemon.model.Pokemon;
import no.westerdals.pokemon.tasks.BitmapCacheTask;
import no.westerdals.pokemon.tasks.LoadBitmapTask;

public class PokemonArrayAdapter extends ArrayAdapter<Pokemon> {
    private ArrayList<Pokemon> pokemons;
    private BitmapCacheTask bitmapCache;
    public PokemonArrayAdapter(Context context, ArrayList<Pokemon> pokemons, BitmapCacheTask bitmapCache) {
        super(context, R.layout.pokemon_list_item, pokemons);
        this.pokemons = pokemons;
        this.bitmapCache = bitmapCache;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PokemonViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pokemon_list_item, parent, false);
            viewHolder = new PokemonViewHolder();
            viewHolder.pokemonImage = (ImageView) convertView.findViewById(R.id.pokemonImage);
            viewHolder.pokemonName = (TextView) convertView.findViewById(R.id.pokemonName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PokemonViewHolder) convertView.getTag();
            if (viewHolder.updateImageTask != null && !viewHolder.updateImageTask.isCancelled()) {
                viewHolder.updateImageTask.cancel(true);
            }
        }

        Pokemon pokemon = pokemons.get(position);
        if (pokemon.getImageUrl() != null) {
            viewHolder.updateImageTask = new LoadBitmapTask(bitmapCache, viewHolder.pokemonImage)
                    .execute(pokemon.getImageUrl());
            viewHolder.pokemonImage.setAlpha(1.0f);
        } else {
            viewHolder.pokemonImage.setImageResource(R.drawable.marker_pokeball);
            viewHolder.pokemonImage.setAlpha(0.5f);
        }
        viewHolder.pokemonName.setText(pokemon.getName());
        return convertView;
    }
}
