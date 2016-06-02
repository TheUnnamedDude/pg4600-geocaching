package no.westerdals.pokemon.activities;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import no.westerdals.pokemon.LehmannApi;
import no.westerdals.pokemon.PokemonDatabase;
import no.westerdals.pokemon.R;
import no.westerdals.pokemon.model.Pokemon;
import no.westerdals.pokemon.nfc.PokemonNfcReader;

public class RegisterActivity extends AppCompatActivity {

    private final PokemonNfcReader nfcReader = new PokemonNfcReader(null, this);

    private PokemonDatabase db;
    private LehmannApi api;

    private TextView txtError;
    private EditText txtInputPokemonId;
    private Button btnSubmit;
    private Button btnCloseRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        addListeners();

        txtError.setVisibility(View.GONE);
        setInputPokemonIdFromIntent();
        nfcReader.initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcReader.enableNfc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcReader.disableNfc();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            nfcReader.handleIntent(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    public void closeActivity(MenuItem item) {
        finish();
    }

    private void init() {
        db = new PokemonDatabase(this);
        api = new LehmannApi(getString(R.string.accessToken));

        txtError = (TextView) findViewById(R.id.errorDescription);
        txtInputPokemonId = (EditText) findViewById(R.id.inputPokemonId);
        btnSubmit = (Button) findViewById(R.id.buttonCheckAndSubmit);
    }

    private void addListeners() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void setInputPokemonIdFromIntent() {
        String pokemonId = getIntent().getStringExtra("pokemonId");
        if (pokemonId != null) {
            txtInputPokemonId.setText(pokemonId.trim());
        }
    }

    private void submit() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Pokemon pokemon = requestPokemonFromServer();
                boolean shouldPersistPokemon = (pokemon != null);
                if (shouldPersistPokemon) {
                    db.catchPokemon(pokemon);
                }
                return shouldPersistPokemon;
            }

            @Override
            protected void onPostExecute(Boolean hasPersistedPokemon) {
                if (hasPersistedPokemon) {
                    finish();
                } else {
                    txtError.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    private Pokemon requestPokemonFromServer() {
        String pokemonId = txtInputPokemonId.getText().toString();
        return api.getPokemonInfo(pokemonId);
    }
}
