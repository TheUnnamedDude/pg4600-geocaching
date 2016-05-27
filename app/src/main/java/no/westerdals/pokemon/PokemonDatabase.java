package no.westerdals.pokemon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;

import no.westerdals.pokemon.model.Pokemon;

public class PokemonDatabase extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private final static String POKEMON_DATABASE = "pokemons.db";

    public PokemonDatabase(Context context) {
        super(context, POKEMON_DATABASE, null, VERSION);
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
        ContentValues contentValues = getContentValues(pokemon);
        return db.insertOrThrow(Pokemon.TABLE_NAME, null, contentValues);
    }

    public void updatePokemon(Pokemon pokemon) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Pokemon.FIELD_CAUGHT, pokemon.isCaught() ? 1 : 0);
        contentValues.put(Pokemon.FIELD_POKEMON_ID, pokemon.getPokemonId());
        contentValues.put(Pokemon.FIELD_IMAGE_URL, pokemon.getImageUrl());
        db.update(Pokemon.TABLE_NAME, contentValues, "mongodbId = ?", new String[] {pokemon.getMongodbId()});
    }

    public ContentValues getContentValues(Pokemon pokemon) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Pokemon.FIELD_MONGODB_ID, pokemon.getMongodbId());
        contentValues.put(Pokemon.FIELD_NAME, pokemon.getName());
        contentValues.put(Pokemon.FIELD_CAUGHT, pokemon.isCaught() ? 1 : 0);
        contentValues.put(Pokemon.FIELD_LAT, pokemon.getLat());
        contentValues.put(Pokemon.FIELD_LNG, pokemon.getLng());
        contentValues.put(Pokemon.FIELD_POKEMON_ID, pokemon.getPokemonId());
        contentValues.put(Pokemon.FIELD_IMAGE_URL, pokemon.getImageUrl());
        return contentValues;
    }


    public ArrayList<Pokemon> getAllPokemons() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Pokemon> pokemons = new ArrayList<>();
        Cursor cursor = db.query(Pokemon.TABLE_NAME, null, null, null, null, null, null);
        int sqliteIdIndex = cursor.getColumnIndex(Pokemon.FIELD_SQLITE_ID);
        int mongodbIdIndex = cursor.getColumnIndex(Pokemon.FIELD_MONGODB_ID);
        int pokemonIdIndex = cursor.getColumnIndex(Pokemon.FIELD_POKEMON_ID);
        int nameIndex = cursor.getColumnIndex(Pokemon.FIELD_NAME);
        int caughtIndex = cursor.getColumnIndex(Pokemon.FIELD_CAUGHT);
        int latIndex = cursor.getColumnIndex(Pokemon.FIELD_LAT);
        int lngIndex = cursor.getColumnIndex(Pokemon.FIELD_LNG);
        int imageUrlIndex = cursor.getColumnIndex(Pokemon.FIELD_IMAGE_URL);
        while (cursor.moveToNext()) {
            Pokemon pokemon = new Pokemon(
                    cursor.getString(mongodbIdIndex), cursor.getString(pokemonIdIndex),
                    cursor.getString(nameIndex), cursor.getInt(caughtIndex) == 1,
                    cursor.getDouble(latIndex), cursor.getDouble(lngIndex),
                    cursor.getString(imageUrlIndex));
            pokemon.setId(cursor.getInt(sqliteIdIndex));
            pokemons.add(pokemon);
        }
        cursor.close();
        return pokemons;
    }

    public int deleteUncaughtPokemon() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(Pokemon.TABLE_NAME, Pokemon.FIELD_CAUGHT + " = ?", new String[] {"0"});
    }
}
