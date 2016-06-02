package no.westerdals.pokemon.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import no.westerdals.pokemon.tasks.BitmapCacheTask;
import no.westerdals.pokemon.ui.PokemonArrayAdapter;
import no.westerdals.pokemon.PokemonDatabase;
import no.westerdals.pokemon.R;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    private BitmapCacheTask bitmapCacheTask = new BitmapCacheTask(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        this.listView = (ListView) findViewById(R.id.pokemonListView);
        bitmapCacheTask.execute("pokemon_images");
        PokemonDatabase pokemonDatabase = new PokemonDatabase(this);
        listView.setAdapter(new PokemonArrayAdapter(this, pokemonDatabase.getCaughtPokemons(), bitmapCacheTask));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    public void closeActivity(MenuItem item) {
        finish();
    }
}
