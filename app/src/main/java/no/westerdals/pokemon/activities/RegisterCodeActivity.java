package no.westerdals.pokemon.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import no.westerdals.pokemon.NfcHelper;
import no.westerdals.pokemon.R;

public class RegisterCodeActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] nfcIntentFilter;
    private String[][] nfcTechList = new String[][] {new String[] {NfcF.class.getName()}, new String[] {NfcA.class.getName()}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        if (nfcManager != null) {
            this.nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            this.nfcAdapter = ((NfcManager) getSystemService(NFC_SERVICE)).getDefaultAdapter();
            setContentView(R.layout.activity_register);
            IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                filter.addDataType("*/*");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                e.printStackTrace();
            }
            nfcIntentFilter = new IntentFilter[]{filter};
            Log.d("NFC", "NFC initialized");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("NFC", "Resume");
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, nfcTechList);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("NFC", "paused");
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("NFC", intent.getAction());
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Log.d("NFC", intent.toString());
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            try {
                ndef.connect();
                for (NfcHelper.MessageFormat messageFormat : NfcHelper.parseMessage(ndef.getNdefMessage())) {
                    Log.d("NFC", messageFormat.toString());
                }
            } catch (IOException | FormatException e) {
                e.printStackTrace();
            }
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

    // TODO: Move NFC logic to main activity (and maybe maps) and rather invoke this activity with
    // a ID whenever a NFC tag is scanned
}
