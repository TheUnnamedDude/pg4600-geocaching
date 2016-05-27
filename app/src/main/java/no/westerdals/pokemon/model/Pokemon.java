package no.westerdals.pokemon.model;

import com.google.gson.annotations.SerializedName;

public class Pokemon {
    public static final String TABLE_NAME = "pokemon";
    public static final String FIELD_SQLITE_ID = "_id";
    public static final String FIELD_MONGODB_ID = "mongodbId";
    public static final String FIELD_POKEMON_ID = "pokemonId";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CAUGHT = "caught";
    public static final String FIELD_LAT = "lat";
    public static final String FIELD_LNG = "lng";
    public static final String FIELD_IMAGE_URL = "imageUrl";

    public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            FIELD_SQLITE_ID + " INT PRIMARY KEY, " +
            FIELD_MONGODB_ID + " CHAR(24) NOT NULL UNIQUE, " +
            FIELD_POKEMON_ID +" CHAR(64), " +
            FIELD_NAME + " CHAR(64) NOT NULL, " +
            FIELD_CAUGHT + " INT DEFAULT 0, " +
            FIELD_LAT + " DOUBLE NOT NULL, " +
            FIELD_LNG + " DOUBLE NOT NULL, " +
            FIELD_IMAGE_URL + " CHAR(255));";

    private String id;
    @SerializedName("_id")
    private String mongodbId;
    @SerializedName("id")
    private String pokemonId;
    private String name;
    private boolean caught;
    private double lat;
    private double lng;
    private String imageUrl;

    public Pokemon(String sqliteId, String mongodbId, String pokemonId, String name, boolean caught,
                   double lat, double lng, String imageUrl) {
        this.id = sqliteId;
        this.mongodbId = mongodbId;
        this.pokemonId = pokemonId;
        this.name = name;
        this.caught = caught;
        this.lat = lat;
        this.lng = lng;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getMongodbId() {
        return mongodbId;
    }

    public String getPokemonId() {
        return pokemonId;
    }

    public String getName() {
        return name;
    }

    public boolean isCaught() {
        return caught;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
