package no.westerdals.pokemon.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import no.westerdals.pokemon.PokemonLocationTask;
import no.westerdals.pokemon.R;
import no.westerdals.pokemon.nfc.PokemonNfcReader;

public class PokemonMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int FINE_LOCATION_AVAILABLE = 0;

    private GoogleMap mMap;
    PokemonNfcReader nfcReader = new PokemonNfcReader(RegisterCodeActivity.class, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        ArrayList<String> requestedPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            // Request the permission
            requestedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_AVAILABLE);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        if (!requestedPermissions.isEmpty()) {
            String[] request = new String[requestedPermissions.size()];
            requestedPermissions.toArray(request);
        }
        new PokemonLocationTask(mMap).execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != FINE_LOCATION_AVAILABLE)
            return;
        for (int i = 0; i < grantResults.length; i++) {
            if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    continue; // This is just here for android studio...
                }
                mMap.setMyLocationEnabled(true);
            }
        }
    }
}
