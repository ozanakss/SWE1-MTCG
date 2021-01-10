

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class Benutzertest {
    private Benutzer benutzer;


    @Test
    public void test_getName() {
        String result = benutzer.getName();
        assertEquals(result, "name");
    }
    @Test
    public void test_getNachname() {
        String result = benutzer.getNachname();
        assertEquals(result, "nachname");
    }
}