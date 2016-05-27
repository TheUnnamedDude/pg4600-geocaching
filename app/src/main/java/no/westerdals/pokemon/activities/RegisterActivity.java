package no.westerdals.pokemon.activities;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Logger;

import no.westerdals.pokemon.LehmannApi;
import no.westerdals.pokemon.PokemonDatabase;
import no.westerdals.pokemon.R;
import no.westerdals.pokemon.model.Pokemon;
import no.westerdals.pokemon.nfc.PokemonNfcReader;

public class RegisterActivity extends AppCompatActivity {

    private final PokemonNfcReader nfcReader = new PokemonNfcReader(null, this);

    private PokemonDatabase db;
    private String accessToken;
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

    private void init() {
        db = new PokemonDatabase(this);
        accessToken = getString(R.string.accessToken);
        api = new LehmannApi(accessToken);

        txtError = (TextView) findViewById(R.id.errorDescription);
        txtInputPokemonId = (EditText) findViewById(R.id.inputPokemonId);
        btnSubmit = (Button) findViewById(R.id.buttonCheckAndSubmit);
        btnCloseRegister = (Button) findViewById(R.id.close_register_button);
    }

    private void addListeners() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        btnCloseRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setInputPokemonIdFromIntent() {
        String pokemonId = getIntent().getStringExtra("pokemonId");
        if (pokemonId != null) {
            txtInputPokemonId.setText(pokemonId);
        }
    }

    private void submit() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Pokemon pokemon = requestPokemonFromServer();
                boolean shouldPersistPokemon = (pokemon != null);

                if (shouldPersistPokemon) {
                    db.updatePokemon(requestPokemonFromServer());
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
