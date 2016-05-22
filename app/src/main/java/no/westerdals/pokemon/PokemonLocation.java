package no.westerdals.pokemon;

import com.google.gson.annotations.SerializedName;

public class PokemonLocation {
    @SerializedName("_id")
    private String id;
    private String name;
    private double lat;
    private double lng;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return "PokemonLocation{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
