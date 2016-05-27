package no.westerdals.pokemon;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.westerdals.pokemon.model.Pokemon;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PokemonDatabaseTest {
    private PokemonDatabase pokemonDatabase;

    @Before
    public void setUp() throws Exception {
        RenamingDelegatingContext context = new RenamingDelegatingContext(
                InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        this.pokemonDatabase = new PokemonDatabase(context);
    }

    @Test
    public void testInsertPokemon() throws Exception {
        pokemonDatabase.insertPokemon(new Pokemon("5747311b70a90e63f44c2971", null,
                "Pikachu", false, 1.3, 3.7, null));
        assertEquals(1, pokemonDatabase.getAllPokemons().size());
    }

    @Test
    public void testAllFieldsReturned() throws Exception {
        pokemonDatabase.insertPokemon(new Pokemon("5747311b70a90e63f44c2971", "pikachuid", "Pikachu",
                true, 1.3, 3.7, "image.png"));
        Pokemon pokemon = pokemonDatabase.getAllPokemons().get(0);
        assertEquals("5747311b70a90e63f44c2971", pokemon.getMongodbId());
        assertEquals("pikachuid", pokemon.getPokemonId());
        assertEquals("Pikachu", pokemon.getName());
        assertEquals(true, pokemon.isCaught());
        assertEquals(1.3, pokemon.getLat(), 0.0);
        assertEquals(3.7, pokemon.getLng(), 0.0);
        assertEquals("image.png", pokemon.getImageUrl());
    }

    @Test
    public void testUpdatePokemon() throws Exception {
        pokemonDatabase.insertPokemon(new Pokemon("5747311b70a90e63f44c2971", null,
                "Pikachu", false, 1.3, 3.7, null));
        pokemonDatabase.updatePokemon(new Pokemon("5747311b70a90e63f44c2971", "pikachuid", null, true, 0, 0, "image.png"));
        Pokemon pokemon = pokemonDatabase.getAllPokemons().get(0);
        assertEquals("Pikachu", pokemon.getName());
        assertEquals(true, pokemon.isCaught());
        assertEquals("image.png", pokemon.getImageUrl());

    }

    @Test
    public void testDeleteUncaughtPokemons() throws Exception {
        pokemonDatabase.insertPokemon(new Pokemon("5747311b70a90e63f44c2971", null,
                "Pikachu", false, 1.3, 3.7, null));
        pokemonDatabase.insertPokemon(new Pokemon("5747311b70a90e63f44c2981", "pikachuid", "Pikachu2",
                true, 1.3, 3.7, "image.png"));
        assertEquals(2, pokemonDatabase.getAllPokemons().size());
        pokemonDatabase.deleteUncaughtPokemon();
        Pokemon pokemon = pokemonDatabase.getAllPokemons().get(0);
        assertEquals("5747311b70a90e63f44c2981", pokemon.getMongodbId());
        assertEquals("pikachuid", pokemon.getPokemonId());
        assertEquals("Pikachu2", pokemon.getName());
        assertEquals(true, pokemon.isCaught());
        assertEquals(1.3, pokemon.getLat(), 0.0);
        assertEquals(3.7, pokemon.getLng(), 0.0);
        assertEquals("image.png", pokemon.getImageUrl());
        assertEquals(1, pokemonDatabase.getAllPokemons().size());
    }
}