package no.westerdals.pokemon.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import no.westerdals.pokemon.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent registerCode = new Intent(getApplicationContext(), RegisterCodeActivity.class);
        startActivity(registerCode);
    }
}
