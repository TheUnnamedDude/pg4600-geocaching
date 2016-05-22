package no.westerdals.pokemon.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class PokemonNfcReader {
    private Class<? extends Activity> activityClass;
    private Activity activity;

    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] nfcIntentFilter;
    private String[][] nfcTechList = new String[][] {new String[] {NfcF.class.getName()}, new String[] {NfcA.class.getName()}};

    public PokemonNfcReader(Class<? extends Activity> activityClass, Activity sourceActivity) {
        this.activityClass = activityClass;
        this.activity = sourceActivity;
    }

    public void initialize() {
        NfcManager nfcManager = (NfcManager) activity.getSystemService(Activity.NFC_SERVICE);
        if (nfcManager != null) {
            nfcPendingIntent = PendingIntent.getActivity(activity, 0, new Intent(activity, activity.getClass())
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            nfcAdapter = ((NfcManager) activity.getSystemService(Activity.NFC_SERVICE)).getDefaultAdapter();
            IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                filter.addDataType("text/plain");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                e.printStackTrace();
            }
            nfcIntentFilter = new IntentFilter[]{filter};
        }
    }

    public void enableNfc() {
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(activity, nfcPendingIntent, nfcIntentFilter, nfcTechList);
        }
    }

    public void disableNfc() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }

    public void handleIntent(Intent intent) {
        if (activityClass == null) {
            return; // TODO: Set the value
        }
        try {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            ndef.connect();
            ArrayList<NfcMessageFormat> messages = parseMessage(ndef.getNdefMessage());
            if (!messages.isEmpty()) {
                Intent nfcHandleActivity = new Intent(activity, activityClass);
                nfcHandleActivity.putExtra("pokemonId", messages.get(0).getContent());
                activity.startActivity(nfcHandleActivity);
            }
        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<NfcMessageFormat> parseMessage(NdefMessage message) throws IOException {
        ArrayList<NfcMessageFormat> result = new ArrayList<>();
        for (NdefRecord record : message.getRecords()) {
            if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(NdefRecord.RTD_TEXT, record.getType())) {
                result.add(parsePayload(record));
            }
        }
        return result;
    }

    private NfcMessageFormat parsePayload(NdefRecord record) throws IOException {
        byte[] payload = record.getPayload();
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload));
        byte statusCode = in.readByte();
        String charset = (statusCode & 0x80) == 0x80 ? "UTF-16" : "UTF-8";
        byte[] languageCode = new byte[statusCode & 0x3F]; // The six last bytes defines how long the language code is
        if (in.read(languageCode) != languageCode.length)
            throw new IOException("Corrupted Ndef format");
        byte[] content = new byte[in.available()];
        if (in.read(content) != content.length)
            throw new IOException("Corrupted Ndef format");
        return new NfcMessageFormat(charset, new String(languageCode, "US-ASCII"), new String(content, charset));
    }

}
