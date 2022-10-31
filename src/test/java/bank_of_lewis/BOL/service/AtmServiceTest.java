package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.model.CashToAdd;
import bank_of_lewis.BOL.repo.AtmRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class AtmServiceTest {

    @Mock
    private AtmRepo atmRepo;

    @InjectMocks
    private AtmService atmService;

    private Atm atm1;
    private Atm atm2;
    private Atm atm3;
    List<Atm> atmList;

    @BeforeEach
    public void setup() {
        atmList = new ArrayList<>();

        atm1 = new Atm(1L, "ATM 1", "London", 10, 20);
        atm2 = new Atm(2L, "ATM 2", "Birmingham", 10, 20);
        atm3 = new Atm(3L, "ATM 3", "London", 8, 3);

        atmList.add(atm1);
        atmList.add(atm2);
        atmList.add(atm3);
    }

    @AfterEach
    public void tearDown() {
        atm1 = atm2 = atm3 = null;
        atmList = null;
    }

    @Test
    @DisplayName("POST/save test")
    public void saveAtmTest__serviceTest() {
        Atm atmTest = new Atm(0L, "Test ATM", "Samsung", 10, 10);
        given(atmRepo.save(atmTest)).willReturn(atmTest);

        ResponseEntity<Atm> atmResponse = atmService.addNewATM(atmTest);
        Atm atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo(atmTest);
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("getAllAtms test")
    public void getAllAtmsTest__test() {
        given(atmRepo.findAll()).willReturn(List.of(atm1, atm2, atm3));

        ResponseEntity<List<Atm>> atmResponse = atmService.getAllAtms();
        List<Atm> atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmRepo.findAll()).isNotNull();
        assertThat(atmResponseBody.size()).isEqualTo(3);
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("generateAtmReport successfully test")
    public void generateAtmReportTest__test() {
        given(atmRepo.findById(2L)).willReturn(Optional.of(atm2));
        int note20s = atm2.getNote20();
        int note50s = atm2.getNote50();


        ResponseEntity<String> atmResponse = atmService.generateAtmReport(atm2.getId());
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        String expectedResponseBody = "$20 notes: " + note20s + ". $50 notes: " + note50s + ". Total cash holding: $" + atm2.calculateTotalCash();
        assertThat(atmResponseBody).isEqualTo(expectedResponseBody);
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("generateAtmReport fails, invalid ID")
    public void invalidIdUsed__generateAtmReportTest__test() {
        Long id = 70L;
        given(atmRepo.findById(id)).willReturn(Optional.empty());

        ResponseEntity<String> atmResponse = atmService.generateAtmReport(id);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();


        assertThat(atmResponseBody).isEqualTo("Could not find ATM with ID " + id);
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("deleteAtm successful")
    public void deleteAtmTest__test() {
        Long id = 3L;
        given(atmRepo.findById(id)).willReturn(Optional.of(atm3));

        ResponseEntity<String> atmResponse = atmService.deleteAtm(id);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Deleted the " + atm3.getName() + ": " + atm3.getLocation() + " atm.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("deleteAtm fails, invalid ID")
    public void invalidIdUsed__deleteAtmTest__test() {
        Long id = 40L;
        given(atmRepo.findById(id)).willReturn(Optional.empty());

        ResponseEntity<String> atmResponse = atmService.deleteAtm(id);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Could not find ATM with the specified ID.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("editAtm successful")
    public void editAtmTest__test() {
        Long id = 1L;
        given(atmRepo.findById(id)).willReturn(Optional.of(atm1));
        Atm updateAtmDetails = new Atm(id, "Test ATM", "London", 100, 100);

        ResponseEntity<String> atmResponse = atmService.editAtm(id, updateAtmDetails);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo(updateAtmDetails.getLocation() + ", " +
                updateAtmDetails.getName() + ", 50 Notes: " + updateAtmDetails.getNote50() +
                ", 20 Notes: " + updateAtmDetails.getNote20());
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("editAtm fails, invalid ID")
    public void invalidIdUsed__editAtmTest__test() {
        Long id = 480L;
        given(atmRepo.findById(id)).willReturn(Optional.empty());
        Atm updateAtmDetails = new Atm(id, "Test ATM", "London", 100, 100);

        ResponseEntity<String> atmResponse = atmService.editAtm(id, updateAtmDetails);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Could not find ATM with the specified ID.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("addNotes to ATM successful")
    public void addNotesToAtmTest__test() {
        Long id = atm2.getId();
        given(atmRepo.findById(id)).willReturn(Optional.of(atm2));
        CashToAdd cashToAdd = new CashToAdd(20, 20);

        ResponseEntity<String> atmResponse = atmService.addNotes(id, cashToAdd);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        String expectedResponse = atm2.getNote20().toString() + " 20 notes added. " + atm2.getNote50().toString() + " 50 notes added.";

        assertThat(atmResponseBody).isEqualTo(expectedResponse);
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("addNotes to ATM fails, invalid ID")
    public void invalidIdUsed__addNotesToAtmTest__test() {
        Long id = -4L;
        given(atmRepo.findById(id)).willReturn(Optional.empty());
        CashToAdd cashToAdd = new CashToAdd(20, 20);

        ResponseEntity<String> atmResponse = atmService.addNotes(id, cashToAdd);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Could not find ATM with the specified ID.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("dispenseNotes from ATM fails, invalid ID")
    public void invalidIdUsed__dispenseNotesFromAtmTest__test() {
        Long id = 83L;
        given(atmRepo.findById(id)).willReturn(Optional.empty());
        int cashRequired = 200;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(id, cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Could not find ATM with the specified ID.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("dispenseNotes from ATM fails, invalid cashRequired")
    public void invalidCashRequired__dispenseNotesFromAtmTest__test() {
        Long id = 1L;
        given(atmRepo.findById(id)).willReturn(Optional.of(atm1));
        int cashRequired = 205;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(id, cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Cannot dispense this value (" + cashRequired + "). Please try another.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("dispenseNotes from ATM fails, negative cashRequired matching valid 20/50 input")
    public void negativeCashRequired__dispenseNotesFromAtmTest__test() {
        Long id = 1L;
        given(atmRepo.findById(id)).willReturn(Optional.of(atm1));
        int cashRequired = -50;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(id, cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Cannot dispense this value (" + cashRequired + "). Please try another.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, prefer20s triggered with low note50s")
    public void prefer20sTriggered__dispenseNotesFromAtmTest__test() {
        Atm atm = new Atm(4L, "Test ATM", "London, Oxford Circus", 100, 3);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 100;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note20sToDispense = 5;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed." + " 0 $50 notes dispensed.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, prefer20s specified successfully with cashRequired % 20 == 0")
    public void prefer20sSpecified__dispenseNotesFromAtmTest__test() {
        Atm atm = new Atm(4L, "Test ATM", "London, Oxford Circus", 100, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 140;
        Boolean prefers20 = true;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note20sToDispense = 7;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed." + " 0 $50 notes dispensed.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, prefer20s specified successfully with cashRequired % 20 != 0")
    public void prefer20sSpecifiedWithNon20DivisibleCashRequired__dispenseNotesFromAtmTest__test() {
        Atm atm = new Atm(4L, "Test ATM", "London, Oxford Circus", 100, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 230;
        Boolean prefers20 = true;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 1;
        int note20sToDispense = (cashRequired - 50) / 20;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, prefer20s but no 20s available")
    public void prefer20sNo20sInAtmCashRequired__dispenseNotesFromAtmTest__test() {
        Atm atm = new Atm(4L, "Test ATM", "London, Oxford Circus", 0, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 200;
        Boolean prefers20 = true;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = cashRequired / 50;

        assertThat(atmResponseBody).isEqualTo("0 $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, multiple of 50")
    public void multipleOf50CashRequired__dispenseNotesFromAtmTest__test() {
        Atm atm = new Atm(1L, "Test ATM", "London", 100, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 1250;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 1250 / 50;

        assertThat(atmResponseBody).isEqualTo("0 $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, multiple of 50 not enough 50s")
    public void multipleOf50CashRequiredNotEnough50s__dispenseNotesFromAtmTest__test() {
        Atm atm = new Atm(4L, "Test ATM", "London, Oxford Circus", 100, 2);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 310;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 1;
        int note20sToDispense = (cashRequired - 50) / 20;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, mix of 50s 20s")
    public void mixOf50s20s__dispenseNotesFromAtmTest__test() {
        Atm atm = new Atm(4L, "Test ATM", "London, Oxford Circus", 100, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 190;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 3;
        int note20sToDispense = 2;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, mix of 50s 20s, not enough 50s")
    public void mixOf50s20sNotEnough50s__dispenseNotesFromAtmTest__test() {
        Atm atm = new Atm(4L, "Test ATM", "London, Oxford Circus", 100, 1);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 190;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 1;
        int note20sToDispense = (cashRequired - 50) / 20;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, mix of 50s 20s, not enough 20s")
    public void mixOf50s20sNotEnough20s__dispenseNotesFromAtmTest__test() {
        Atm atm = new Atm(4L, "Test ATM", "London, Oxford Circus", 0, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 220;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Cannot dispense this value (220). Please try another.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, mix of 50s 20s, minus 50 for success")
    public void mixOf50s20sMinus50ForSuccess__dispenseNotesFromAtmTest__test() {
        given(atmRepo.findById(atm1.getId())).willReturn(Optional.of(atm1));
        int cashRequired = 110;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm1.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 1;
        int note20sToDispense = 3;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, cashRequired % 20")
    public void cashRequiredMod20__dispenseNotesFromAtmTest__test() {
        given(atmRepo.findById(atm1.getId())).willReturn(Optional.of(atm1));
        int cashRequired = 40;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm1.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note20sToDispense = 2;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed." + " 0 $50 notes dispensed.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, cashRequired % 20, not enough 20s")
    public void cashRequiredMod20NotEnough20s__dispenseNotesFromAtmTest__test() {
        Atm atm = new Atm(4L, "Test", "London, Oxford Circus", 0, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 40;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Cannot dispense this value (" + cashRequired + "). Please try another.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("successfully check note availability, false, not enough 20s")
    public void checkNoteAvailability__returnFalseNotEnough20s__test() {
        Atm atm = new Atm(1L, "Test", "Test Road", 2, 5);
        int note20sToDispense = 3;
        int note50sToDispense = 5;

        Boolean checkNoteAvailabilityResponse = atmService.checkNoteAvailability(note20sToDispense, note50sToDispense, atm);

        assertThat(checkNoteAvailabilityResponse).isEqualTo(false);
    }

    @Test
    @DisplayName("successfully check note availability, false, not enough 50s")
    public void checkNoteAvailability__returnFalseNotEnough50s__test() {
        Atm atm = new Atm(1L, "Test", "Test Road", 22, 5);
        int note20sToDispense = 1;
        int note50sToDispense = 6;

        Boolean checkNoteAvailabilityResponse = atmService.checkNoteAvailability(note20sToDispense, note50sToDispense, atm);

        assertThat(checkNoteAvailabilityResponse).isEqualTo(false);
    }

    @Test
    @DisplayName("successfully check note availability, false, not enough 20s or 50s")
    public void checkNoteAvailability__returnFalseNotEnough20sOr50s__test() {
        Atm atm = new Atm(1L, "Test", "Test Road", 1, 1);
        int note20sToDispense = 2;
        int note50sToDispense = 3;

        Boolean checkNoteAvailabilityResponse = atmService.checkNoteAvailability(note20sToDispense, note50sToDispense, atm);

        assertThat(checkNoteAvailabilityResponse).isEqualTo(false);
    }

    @Test
    @DisplayName("successfully check note availability, enough 20s and 50s available")
    public void checkNoteAvailability__returnTrue__test() {
        int note20sToDispense = 1;
        int note50sToDispense = 5;

        Boolean checkNoteAvailabilityResponse = atmService.checkNoteAvailability(note20sToDispense, note50sToDispense, atm1);

        assertThat(checkNoteAvailabilityResponse).isEqualTo(true);
    }

    @Test
    @DisplayName("successfully calculate note50s required, more 50s in ATM than required")
    public void calculateNote50sRequired__moreInAtmThanRequired__test() {
        int cashRequired = 170;
        int numberOf50sRequired = 3;

        int calculateNote50sRequiredResponse = atmService.calculateNote50sRequired(cashRequired, atm1);

        assertThat(calculateNote50sRequiredResponse).isEqualTo(numberOf50sRequired);
    }

    @Test
    @DisplayName("successfully calculate note50s required, LESS 50s in ATM than required")
    public void calculateNote50sRequired__lessInAtmThanRequired__test() {
        Atm atm = new Atm(1L, "Test", "Test Road", 100, 1);
        int cashRequired = 250;
        int numberOf50sRequired = 1;

        int calculateNote50sRequiredResponse = atmService.calculateNote50sRequired(cashRequired, atm);

        assertThat(calculateNote50sRequiredResponse).isEqualTo(numberOf50sRequired);
    }

    @Test
    @DisplayName("successfully calculate note50s required, cashRequired = 0")
    public void calculateNote50sRequired__cashRequiredEquals0__test() {
        int cashRequired = 0;
        int numberOf50sRequired = 0;

        int calculateNote50sRequiredResponse = atmService.calculateNote50sRequired(cashRequired, atm3);

        assertThat(calculateNote50sRequiredResponse).isEqualTo(numberOf50sRequired);
    }

    @Test
    @DisplayName("successfully calculate note50s required, cashRequired = -50")
    public void calculateNote50sRequired__cashRequiredEqualsMinus50__test() {
        int cashRequired = -50;
        int numberOf50sRequired = 0;

        int calculateNote50sRequiredResponse = atmService.calculateNote50sRequired(cashRequired, atm2);

        assertThat(calculateNote50sRequiredResponse).isEqualTo(numberOf50sRequired);
    }
}