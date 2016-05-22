package no.westerdals.pokemon;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class NfcHelper {

    public static ArrayList<MessageFormat> parseMessage(NdefMessage message) throws IOException {
        ArrayList<MessageFormat> result = new ArrayList<>();
        for (NdefRecord record : message.getRecords()) {
            if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(NdefRecord.RTD_TEXT, record.getType())) {
                result.add(parsePayload(record));
            }
        }
        return result;
    }

    private static MessageFormat parsePayload(NdefRecord record) throws IOException {
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
        return new MessageFormat(charset, new String(languageCode, "US-ASCII"), new String(content, charset));
    }

    public static class MessageFormat {
        private String charset;
        private String languageCode;
        private String content;

        public MessageFormat(String charset, String languageCode, String content) {
            this.charset = charset;
            this.languageCode = languageCode;
            this.content = content;
        }

        public String getCharset() {
            return charset;
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "MessageFormat{" +
                    "charset='" + charset + '\'' +
                    ", languageCode='" + languageCode + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}
