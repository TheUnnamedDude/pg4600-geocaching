package no.westerdals.pokemon.utils;

public class TestHelpers {
    public static String formatHex(byte[] bytes) {
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
