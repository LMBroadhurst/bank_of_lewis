package bank_of_lewis.BOL.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AtmTest {

    @Test
    void calculateTotalCash() {
        Atm atmTest = new Atm(1L, "Test", "Test", 10, 10);

        int totalCash = atmTest.calculateTotalCash();

        assertEquals(700, totalCash);
    }
}