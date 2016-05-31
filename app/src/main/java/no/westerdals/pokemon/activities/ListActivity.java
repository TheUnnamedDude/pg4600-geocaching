package no.westerdals.pokemon.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import no.westerdals.pokemon.ui.PokemonArrayAdapter;
import no.westerdals.pokemon.PokemonDatabase;
import no.westerdals.pokemon.R;

public class ListActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        this.listView = (ListView) findViewById(R.id.pokemonListView);
        PokemonDatabase pokemonDatabase = new PokemonDatabase(this);
        listView.setAdapter(new PokemonArrayAdapter(this, pokemonDatabase.getCaughtPokemons()));
    }
}
