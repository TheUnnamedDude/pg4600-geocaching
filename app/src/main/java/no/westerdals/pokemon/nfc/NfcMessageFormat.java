package no.westerdals.pokemon.nfc;


public class NfcMessageFormat {
    private String charset;
    private String languageCode;
    private String content;

    public NfcMessageFormat(String charset, String languageCode, String content) {
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