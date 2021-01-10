import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KarteTest {

    @Test
    public void test_getName() {
        Karte karte = new Karte("Name", 10, DieElemente.WATER, KartenKategorie.SPELL);
        assertEquals(karte.getName(), "Name");
    }
}