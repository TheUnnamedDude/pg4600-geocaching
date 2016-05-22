package no.westerdals.pokemon.activities;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import no.westerdals.pokemon.R;
import no.westerdals.pokemon.nfc.PokemonNfcReader;

public class RegisterCodeActivity extends AppCompatActivity {
    private final PokemonNfcReader nfcReader = new PokemonNfcReader(null, this);
    private EditText inputPokemonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputPokemonId = (EditText) findViewById(R.id.inputPokemonId);
        String pokemonId = getIntent().getStringExtra("pokemonId");
        if (pokemonId != null) {
            inputPokemonId.setText(pokemonId);
        }
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
        Log.d("NFC", intent.getAction());
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            nfcReader.handleIntent(intent);
        }
    }

    private String formatHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toString(b & 0xFF, 16);
            result.append(' ');
            if (hex.length() < 2)
                result.append("0");
            result.append(hex);
        }
        return result.substring(1);
    }
}
