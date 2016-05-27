package no.westerdals.pokemon.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import no.westerdals.pokemon.LehmannApi;
import no.westerdals.pokemon.PokemonDatabase;
import no.westerdals.pokemon.R;
import no.westerdals.pokemon.nfc.PokemonNfcReader;
import no.westerdals.pokemon.tasks.SetMarkersTask;
import no.westerdals.pokemon.tasks.UpdateCacheTask;

public class PokemonMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final int FINE_LOCATION_AVAILABLE = 0;

    public GoogleMap mMap;
    private LehmannApi lehmannApi;
    private PokemonDatabase pokemonDatabase;
    PokemonNfcReader nfcReader = new PokemonNfcReader(RegisterActivity.class, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_map);
        createFabMenu();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        nfcReader.initialize();
    }

    private void createFabMenu() {
        FloatingActionButton captureBtn = (FloatingActionButton) findViewById(R.id.capture_button);
        FloatingActionButton listBtn = (FloatingActionButton) findViewById(R.id.list_button);

        if (captureBtn != null) {
            captureBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PokemonMapActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (listBtn != null) {
            listBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PokemonMapActivity.this, ListActivity.class);
                    startActivity(intent);
                }
            });
        }

        this.pokemonDatabase = new PokemonDatabase(this);
        this.lehmannApi = new LehmannApi(getString(R.string.accessToken));
        new UpdateCacheTask(this, pokemonDatabase, lehmannApi).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcReader.enableNfc();
        if (mMap != null) {
            updateMarkers();
        }
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
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_AVAILABLE);
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

        updateMarkers();
        new SetMarkersTask(mMap, this).execute();
        createFabMenuAnim(mMap);
    }

    private void createFabMenuAnim(final GoogleMap mMap) {
        FloatingActionMenu fabMenu = (FloatingActionMenu)findViewById(R.id.fab_menu);
        final ObjectAnimator moveUp = ObjectAnimator.ofFloat(fabMenu, "y", -230);
        final ObjectAnimator moveDown = ObjectAnimator.ofFloat(fabMenu, "y", 0);
        final boolean[] markerIsSelected = {false};

        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!markerIsSelected[0]){
                    System.out.println("Clicked on " + marker.getTitle());
                    moveUp.setDuration(500);
                    moveUp.start();
                }
                markerIsSelected[0] = true;
                return false;
            }
        });

        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                moveDown.setDuration(500);
                moveDown.start();
                markerIsSelected[0] = false;
            }
        });
    }

    public void updateMarkers() {
        new SetMarkersTask(mMap, this).execute();
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
