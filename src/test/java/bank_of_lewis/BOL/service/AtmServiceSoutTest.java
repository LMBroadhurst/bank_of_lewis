package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class AtmServiceSoutTest {

    @InjectMocks
    private AtmService atmService;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;


    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("successful SOUT, zero 20s only")
    public void zeroNotesNotification__zero20s__test() {
        Atm atm = new Atm(1L, "SOUT test", "Java Land", 0, 100);

        atmService.zeroOrLowNotesNotifications(atm);
        String expectedMessage = atm.getName() + " - " + atm.getLocation() + " has ran out of $20 notes.";

        assertEquals(expectedMessage, outContent.toString().stripTrailing());
    }

    @Test
    @DisplayName("successful SOUT, zero 50s only")
    public void zeroNotesNotification__zero50s__test() {
        Atm atm = new Atm(1L, "SOUT test", "Java Land", 100, 0);

        atmService.zeroOrLowNotesNotifications(atm);
        String expectedMessage = atm.getName() + " - " + atm.getLocation() + " has ran out of $50 notes.";

        assertEquals(expectedMessage, outContent.toString().stripTrailing());
    }

    @Test
    @DisplayName("successful SOUT, low 20s only")
    public void zeroNotesNotification__low20s__test() {
        Atm atm = new Atm(1L, "SOUT test", "Java Land", 5, 100);

        atmService.zeroOrLowNotesNotifications(atm);
        String expectedMessage = atm.getName() + " - " + atm.getLocation() + " has less than 10 $20 notes.";

        assertEquals(expectedMessage, outContent.toString().stripTrailing());
    }

    @Test
    @DisplayName("successful SOUT, low 50s only")
    public void zeroNotesNotification__low50s__test() {
        Atm atm = new Atm(1L, "SOUT test", "Java Land", 100, 5);

        atmService.zeroOrLowNotesNotifications(atm);
        String expectedMessage = atm.getName() + " - " + atm.getLocation() + " has less than 10 $50 notes.";

        assertEquals(expectedMessage, outContent.toString().stripTrailing());
    }

    @Test
    @DisplayName("successful SOUT, zero 20s & low 50s")
    public void zeroNotesNotification__Zero20slow50s__test() {
        Atm atm = new Atm(1L, "SOUT test", "Java Land", 0, 5);

        atmService.zeroOrLowNotesNotifications(atm);
        String expectedMessage1 = atm.getName() + " - " + atm.getLocation() + " has ran out of $20 notes.";
        String expectedMessage2 = atm.getName() + " - " + atm.getLocation() + " has less than 10 $50 notes.";

        assertThat(outContent.toString().stripTrailing()).contains(expectedMessage1, expectedMessage2);
    }

    @Test
    @DisplayName("successful SOUT, zero 20s & zero 50s")
    public void zeroNotesNotification__Zero20sZero50s__test() {
        Atm atm = new Atm(1L, "SOUT test", "Java Land", 0, 0);

        atmService.zeroOrLowNotesNotifications(atm);
        String expectedMessage1 = atm.getName() + " - " + atm.getLocation() + " has ran out of $20 notes.";
        String expectedMessage2 = atm.getName() + " - " + atm.getLocation() + " has ran out of $50 notes.";

        assertThat(outContent.toString().stripTrailing()).contains(expectedMessage1, expectedMessage2);
    }

    @Test
    @DisplayName("successful SOUT, low 20s & low 50s")
    public void zeroNotesNotification__Low20sLow50s__test() {
        Atm atm = new Atm(1L, "SOUT test", "Java Land", 5, 5);

        atmService.zeroOrLowNotesNotifications(atm);
        String expectedMessage1 = atm.getName() + " - " + atm.getLocation() + " has less than 10 $20 notes.";
        String expectedMessage2 = atm.getName() + " - " + atm.getLocation() + " has less than 10 $50 notes.";

        assertThat(outContent.toString().stripTrailing()).contains(expectedMessage1, expectedMessage2);
    }

    @Test
    @DisplayName("successful SOUT, low 20s & zero 50s")
    public void zeroNotesNotification__Low20sZero50s__test() {
        Atm atm = new Atm(1L, "SOUT test", "Java Land", 5, 0);

        atmService.zeroOrLowNotesNotifications(atm);
        String expectedMessage1 = atm.getName() + " - " + atm.getLocation() + " has less than 10 $20 notes.";
        String expectedMessage2 = atm.getName() + " - " + atm.getLocation() + " has ran out of $50 notes.";

        assertThat(outContent.toString().stripTrailing()).contains(expectedMessage1, expectedMessage2);
    }

    @Test
    @DisplayName("no SOUT, 20s & 50s above threshold of 10")
    public void zeroNotesNotification__NoSout__test() {
        Atm atm = new Atm(1L, "SOUT test", "Java Land", 15, 20);

        atmService.zeroOrLowNotesNotifications(atm);
        String expectedMessage1 = atm.getName() + " - " + atm.getLocation() + " has less than 10 $20 notes.";
        String expectedMessage2 = atm.getName() + " - " + atm.getLocation() + " has ran out of $50 notes.";

        assertThat(outContent.toString().stripTrailing()).isEqualTo("");
    }

}
