package no.westerdals.pokemon.activities;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import no.westerdals.pokemon.R;
import no.westerdals.pokemon.nfc.PokemonNfcReader;

public class RegisterActivity extends AppCompatActivity {
    private final PokemonNfcReader nfcReader = new PokemonNfcReader(null, this);

    private EditText inputPokemonId;
    private Button closeRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
        addListeners();

        getPokemonIdFromIntent();
        nfcReader.initialize();
    }

    private void initComponents() {
        inputPokemonId = (EditText) findViewById(R.id.inputPokemonId);
        closeRegisterBtn = (Button) findViewById(R.id.close_register_button);
    }

    private void addListeners() {
        closeRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getPokemonIdFromIntent() {
        String pokemonId = getIntent().getStringExtra("pokemonId");
        if (pokemonId != null) {
            inputPokemonId.setText(pokemonId);
        }
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
}
