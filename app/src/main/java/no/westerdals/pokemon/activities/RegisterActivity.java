package no.westerdals.pokemon.activities;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import no.westerdals.pokemon.LehmannApi;
import no.westerdals.pokemon.R;
import no.westerdals.pokemon.nfc.PokemonNfcReader;

public class RegisterActivity extends AppCompatActivity {

    private final PokemonNfcReader nfcReader = new PokemonNfcReader(null, this);

    private EditText inputPokemonId;
    private Button submitBtn;
    private Button closeRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
        addListeners();

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

    private void initComponents() {
        inputPokemonId = (EditText) findViewById(R.id.inputPokemonId);
        submitBtn = (Button) findViewById(R.id.buttonCheckAndSubmit);
        closeRegisterBtn = (Button) findViewById(R.id.close_register_button);
    }

    private void addListeners() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
                finish();
            }
        });

        closeRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setInputPokemonIdFromIntent() {
        String pokemonId = getIntent().getStringExtra("pokemonId");
        if (pokemonId != null) {
            inputPokemonId.setText(pokemonId);
        }
    }

    private void submit() {
        // LehmannApi api = new LehmannApi();
    }
}
