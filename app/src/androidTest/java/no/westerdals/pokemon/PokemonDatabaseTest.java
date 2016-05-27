package no.westerdals.pokemon;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.westerdals.pokemon.activities.PokemonMapActivity;
import no.westerdals.pokemon.model.Pokemon;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PokemonDatabaseTest {
    private PokemonDatabase pokemonDatabase;

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(PokemonMapActivity.class);

    @Before
    public void setUp() throws Exception {
        RenamingDelegatingContext context = new RenamingDelegatingContext(activityRule.getActivity(),
                "test_");
        this.pokemonDatabase = new PokemonDatabase(context);
    }

    @Test
    public void testInsertPokemon() throws Exception {
        pokemonDatabase.insertPokemon(new Pokemon(null, "5747311b70a90e63f44c2971", null,
                "Pikachu", false, 1.3, 3.7, null));
        assertEquals(1, pokemonDatabase.getAllPokemons().size());
    }

    @Test
    public void testUpdatePokemon() throws Exception {

    }
}