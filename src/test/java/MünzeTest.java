import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class MünzeTest {

    @Test
    public void test_getCoinAmount(){
        Münze münze = new Münze(10);
        assertTrue(münze.getMünzen() >= 0);
    }

}
