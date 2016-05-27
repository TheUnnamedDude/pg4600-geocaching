package no.westerdals.pokemon;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import no.westerdals.pokemon.model.Pokemon;

public class PokemonDatabase extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private final static String POKEMON_DATABASE = "pokemons.db";

    public PokemonDatabase(Context context) {
        super(context, POKEMON_DATABASE, null, 0);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Pokemon.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertPokemon(Pokemon pokemon) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Pokemon.FIELD_MONGODB_ID, pokemon.getMongodbId());
        contentValues.put(Pokemon.FIELD_NAME, pokemon.getName());
        contentValues.put(Pokemon.FIELD_CAUGHT, false);
        contentValues.put(Pokemon.FIELD_LAT, pokemon.getLat());
        contentValues.put(Pokemon.FIELD_LNG, pokemon.getLng());
        return db.insert(Pokemon.TABLE_NAME, null, contentValues);
    }

    public void updatePokemon(Pokemon pokemon) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Pokemon.FIELD_CAUGHT, pokemon.isCaught());
        contentValues.put(Pokemon.FIELD_POKEMON_ID, pokemon.getPokemonId());
        contentValues.put(Pokemon.FIELD_IMAGE_URL, pokemon.getImageUrl());
        db.update(Pokemon.TABLE_NAME, contentValues, "mongodbId", new String[] {pokemon.getMongodbId()});
    }
}
