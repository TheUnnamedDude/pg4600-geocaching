package no.westerdals.pokemon;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LehmannApi {
    private final static String BASE_URL = "https://locations.lehmann.tech";
    private final static Gson GSON = new Gson();

    private String apiKey;

    public LehmannApi(String apiKey) {
        this.apiKey = apiKey;
    }

    public PokemonLocation[] getLocations() {
        return get(BASE_URL + "/locations", false, PokemonLocation[].class);
    }

    public PokemonInfo getPokemonInfo(String pokemonId) {
        return get(BASE_URL + "/pokemons/" + pokemonId, true, PokemonInfo.class);
    }

    private <T>T get(String url, boolean setKey, Class<T> t) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            if (setKey) {
                connection.setRequestProperty("X-TOKEN", apiKey);
            }
            connection.setDoInput(true);
            connection.connect();
            switch (connection.getResponseCode()) {
                case 200:
                    return GSON.fromJson(new InputStreamReader(connection.getInputStream()), t);
                default:
                    return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
