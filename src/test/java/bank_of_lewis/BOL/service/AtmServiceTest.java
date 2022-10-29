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
    @DisplayName("Testing ATM service layer, POST/save test")
    public void saveAtmTest__serviceTest() {
        Atm atmTest = new Atm(4L, "Test ATM", "Samsung", 10, 10);
        given(atmRepo.save(atmTest)).willReturn(atmTest);

        Atm savedAtmTest = atmService.addNewATM(atmTest).getBody();

        assertThat(savedAtmTest).isNotNull();
    }

    @Test
    @DisplayName("Testing ATM service layer, getAllAtms test")
    public void getAllAtmsTest__serviceTest() {
        given(atmRepo.findAll()).willReturn(List.of(atm1, atm2, atm3));

        List<Atm> atmList = atmService.getAllAtms().getBody();

        assertThat(atmList).isNotNull();
        assertThat(atmRepo.findAll()).isNotNull();
        assertThat(atmRepo.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Testing ATM service layer, generateAtmReport successfully test")
    public void generateAtmReportTest__serviceTest() {
        given(atmRepo.findById(2L)).willReturn(Optional.of(atm2));

        String atmReport = atmService.generateAtmReport(atm2.getId()).getBody();
        int note20s = atm2.getNote20();
        int note50s = atm2.getNote50();

        String responseBody = "$20 notes: " + note20s + ". $50 notes: " + note50s + ". Total cash holding: $" + atm2.calculateTotalCash();

        assertThat(atmReport).isNotNull();
        assertThat(atmReport).isEqualTo(responseBody);
    }

    @Test
    @DisplayName("Testing ATM service layer, generateAtmReport fails, invalid ID")
    public void invalidIdUsed__generateAtmReportTest__serviceTest() {
        Long id = 70L;
        given(atmRepo.findById(id)).willReturn(Optional.empty());

        String atmReport = atmService.generateAtmReport(id).getBody();

        assertThat(atmReport).isNotNull();
        assertThat(atmReport).isEqualTo("Could not find ATM with ID " + id);
    }

    @Test
    @DisplayName("Testing ATM service layer, deleteAtm successful")
    public void deleteAtmTest__serviceTest() {
        Long id = 3L;
        given(atmRepo.findById(id)).willReturn(Optional.of(atm3));

        String atmResponse = atmService.deleteAtm(id).getBody();
        atmService.deleteAtm(id);

        assertThat(atmResponse).isNotNull();
        assertThat(atmResponse).isEqualTo("Deleted the " + atm3.getName() + ": " + atm3.getLocation() + " atm.");
    }

    @Test
    @DisplayName("Testing ATM service layer, deleteAtm fails, invalid ID")
    public void invalidIdUsed__deleteAtmTest__serviceTest() {
        Long id = 40L;
        given(atmRepo.findById(id)).willReturn(Optional.empty());

        String atmResponse = atmService.deleteAtm(id).getBody();

        assertThat(atmResponse).isNotNull();
        assertThat(atmResponse).isEqualTo("Could not find ATM with the specified ID.");
    }

    @Test
    @DisplayName("Testing ATM service layer, editAtm successful")
    public void editAtmTest__serviceTest() {
        Long id = 1L;
        given(atmRepo.findById(id)).willReturn(Optional.of(atm1));

        Atm updateAtmDetails = Atm.builder()
                .id(id)
                .name("Hello")
                .location("World")
                .note50(999)
                .note20(999)
                .build();

        atmService.editAtm(id, updateAtmDetails);
        String atmResponse = atmService.editAtm(id, updateAtmDetails).getBody();

        assertThat(atmResponse).isNotNull();
        assertThat(atmResponse).isEqualTo(updateAtmDetails.getLocation() + ", " +
                updateAtmDetails.getName() + ", 50 Notes: " + updateAtmDetails.getNote50() +
                ", 20 Notes: " + updateAtmDetails.getNote20());
    }

    @Test
    @DisplayName("Testing ATM service layer, editAtm fails, invalid ID")
    public void invalidIdUsed__editAtmTest__serviceTest() {
        Long id = 480L;
        given(atmRepo.findById(id)).willReturn(Optional.empty());
        Atm updateAtmDetails = Atm.builder()
                .id(id)
                .name("Hello")
                .location("World")
                .note50(999)
                .note20(999)
                .build();

        String atmResponse = atmService.editAtm(id, updateAtmDetails).getBody();

        assertThat(atmResponse).isNotNull();
        assertThat(atmResponse).isEqualTo("Could not find ATM with the specified ID.");
    }

    @Test
    @DisplayName("Testing ATM service layer, addNotes to ATM successful")
    public void addNotesToAtmTest__serviceTest() {
        Long id = atm2.getId();
        given(atmRepo.findById(id)).willReturn(Optional.of(atm2));
        CashToAdd cashToAdd = new CashToAdd(20, 20);

        atmService.addNotes(id, cashToAdd);
        String atmResponse = atmService.addNotes(id, cashToAdd).getBody();

        assertThat(atmResponse).isNotNull();
        String expectedResponse = atm2.getNote20().toString() + " 20 notes added. " + atm2.getNote50().toString() + " 50 notes added.";
        assertThat(atmResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Testing ATM service layer, addNotes to ATM fails, invalid ID")
    public void invalidIdUsed__addNotesToAtmTest__serviceTest() {
        Long id = -4L;
        given(atmRepo.findById(id)).willReturn(Optional.empty());
        CashToAdd cashToAdd = new CashToAdd(20, 20);

        String atmResponse = atmService.addNotes(id, cashToAdd).getBody();

        assertThat(atmResponse).isNotNull();
        assertThat(atmResponse).isEqualTo("Could not find ATM with the specified ID.");
    }

    @Test
    @DisplayName("Testing ATM service layer, dispenseNotes from ATM fails, invalid ID")
    public void invalidIdUsed__dispenseNotesFromAtmTest__serviceTest() {
        Long id = 83L;
        given(atmRepo.findById(id)).willReturn(Optional.empty());
        int cashRequired = 200;
        Boolean prefers20 = false;

        String atmResponseBody = atmService.dispenseNotes(id, cashRequired, prefers20).getBody();
        HttpStatus atmResponseStatusCode = atmService.dispenseNotes(id, cashRequired, prefers20).getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Could not find ATM with the specified ID.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Testing ATM service layer, dispenseNotes from ATM fails, invalid cashRequired")
    public void invalidCashRequired__dispenseNotesFromAtmTest__serviceTest() {
        Long id = 1L;
        given(atmRepo.findById(id)).willReturn(Optional.of(atm1));
        int cashRequired = 205;
        Boolean prefers20 = false;

        String atmResponseBody = atmService.dispenseNotes(id, cashRequired, prefers20).getBody();
        HttpStatus atmResponseStatusCode = atmService.dispenseNotes(id, cashRequired, prefers20).getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Cannot dispense this value (" + cashRequired + "). Please try another. 5.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Testing ATM service layer, dispenseNotes from ATM fails, negative cashRequired matching valid 20/50 input")
    public void negativeCashRequired__dispenseNotesFromAtmTest__serviceTest() {
        Long id = 1L;
        given(atmRepo.findById(id)).willReturn(Optional.of(atm1));
        int cashRequired = -50;
        Boolean prefers20 = false;

        String atmResponseBody = atmService.dispenseNotes(id, cashRequired, prefers20).getBody();
        HttpStatus atmResponseStatusCode = atmService.dispenseNotes(id, cashRequired, prefers20).getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Cannot dispense this value (" + cashRequired + "). Please try another. 5.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Testing ATM service layer, successful dispenseNotes from ATM, prefer20s triggered with low note50s")
    public void prefer20sTriggered__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Trigger prefer20s", "London, Oxford Circus", 100, 3);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 100;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note20sToDispense = 5;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed." + " 0 $50 notes dispensed. 4");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Testing ATM service layer, successful dispenseNotes from ATM, prefer20s specified successfully with cashRequired % 20 == 0")
    public void prefer20sSpecified__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Accept prefers20 parameter", "London, Oxford Circus", 100, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 140;
        Boolean prefers20 = true;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note20sToDispense = 7;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed." + " 0 $50 notes dispensed. 4");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Testing ATM service layer, successful dispenseNotes from ATM, prefer20s specified successfully with cashRequired % 20 != 0")
    public void prefer20sSpecifiedWithNon20DivisibleCashRequired__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Accept prefers20 parameter", "London, Oxford Circus", 100, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 230;
        Boolean prefers20 = true;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 1;
        int note20sToDispense = (cashRequired - 50) / 20;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 6");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Testing ATM service layer, successful dispenseNotes from ATM, prefer20s but no 20s available")
    public void prefer20sNo20sInAtmCashRequired__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Accept prefers20 parameter, no 20s", "London, Oxford Circus", 0, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 200;
        Boolean prefers20 = true;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = cashRequired / 50;

        assertThat(atmResponseBody).isEqualTo("0 $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 1");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Testing ATM service layer, successful dispenseNotes from ATM, multiple of 50")
    public void multipleOf50CashRequired__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Accept prefers20 parameter", "London, Oxford Circus", 100, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 1250;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 1250 / 50;

        assertThat(atmResponseBody).isEqualTo("0 $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 1");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Testing ATM service layer, successful dispenseNotes from ATM, multiple of 50 not enough 50s")
    public void multipleOf50CashRequiredNotEnough50s__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Accept prefers20 parameter", "London, Oxford Circus", 100, 2);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 310;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 1;
        int note20sToDispense = (cashRequired - 50) / 20;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 6");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Testing ATM service layer, successful dispenseNotes from ATM, mix of 50s 20s")
    public void mixOf50s20s__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Accept prefers20 parameter", "London, Oxford Circus", 100, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 190;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 3;
        int note20sToDispense = 2;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 2");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Testing ATM service layer, successful dispenseNotes from ATM, mix of 50s 20s, not enough 50s")
    public void mixOf50s20sNotEnough50s__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Accept prefers20 parameter", "London, Oxford Circus", 100, 1);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 190;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 1;
        int note20sToDispense = (cashRequired - 50) / 20;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 6");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Testing ATM service layer, successful dispenseNotes from ATM, mix of 50s 20s, not enough 20s")
    public void mixOf50s20sNotEnough20s__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Accept prefers20 parameter", "London, Oxford Circus", 0, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 220;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Cannot dispense this value (220). Please try another. 5.");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, mix of 50s 20s, minus 50 for success")
    public void mixOf50s20sMinus50ForSuccess__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Test", "London, Oxford Circus", 100, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 110;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note50sToDispense = 1;
        int note20sToDispense = 3;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 3");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, cashRequired % 20")
    public void cashRequiredMod20__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Test", "London, Oxford Circus", 100, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 40;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();
        int note20sToDispense = 2;

        assertThat(atmResponseBody).isEqualTo(note20sToDispense + " $20 notes dispensed." + " 0 $50 notes dispensed. 4");
        assertThat(atmResponseStatusCode).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("successful dispenseNotes from ATM, cashRequired % 20, not enough 20s")
    public void cashRequiredMod20NotEnough20s__dispenseNotesFromAtmTest__serviceTest() {
        Atm atm = new Atm(4L, "Test", "London, Oxford Circus", 0, 100);
        given(atmRepo.findById(atm.getId())).willReturn(Optional.of(atm));
        int cashRequired = 40;
        Boolean prefers20 = false;

        ResponseEntity<String> atmResponse = atmService.dispenseNotes(atm.getId(), cashRequired, prefers20);
        String atmResponseBody = atmResponse.getBody();
        HttpStatus atmResponseStatusCode = atmResponse.getStatusCode();

        assertThat(atmResponseBody).isEqualTo("Cannot dispense this value (" + cashRequired + "). Please try another. 5.");
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
        Atm atm = new Atm(1L, "Test", "Test Road", 1, 2);
        int note20sToDispense = 2;
        int note50sToDispense = 3;

        Boolean checkNoteAvailabilityResponse = atmService.checkNoteAvailability(note20sToDispense, note50sToDispense, atm);

        assertThat(checkNoteAvailabilityResponse).isEqualTo(false);
    }

    @Test
    @DisplayName("successfully check note availability, enough 20s and 50s available")
    public void checkNoteAvailability__returnTrue__test() {
        Atm atm = new Atm(1L, "Test", "Test Road", 100, 20);
        int note20sToDispense = 1;
        int note50sToDispense = 5;

        Boolean checkNoteAvailabilityResponse = atmService.checkNoteAvailability(note20sToDispense, note50sToDispense, atm);

        assertThat(checkNoteAvailabilityResponse).isEqualTo(true);
    }

    @Test
    @DisplayName("successfully calculate note50s required, more 50s in ATM than required")
    public void calculateNote50sRequired__moreInAtmThanRequired__test() {
        Atm atm = new Atm(1L, "Test", "Test Road", 10, 10);
        int cashRequired = 170;
        int numberOf50sRequired = 3;

        int calculateNote50sRequiredResponse = atmService.calculateNote50sRequired(cashRequired, atm);

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
        Atm atm = new Atm(1L, "Test", "Test Road", 100, 1);
        int cashRequired = 0;
        int numberOf50sRequired = 0;

        int calculateNote50sRequiredResponse = atmService.calculateNote50sRequired(cashRequired, atm);

        assertThat(calculateNote50sRequiredResponse).isEqualTo(numberOf50sRequired);
    }

    @Test
    @DisplayName("successfully calculate note50s required, cashRequired = -50")
    public void calculateNote50sRequired__cashRequiredEqualsMinus50__test() {
        Atm atm = new Atm(1L, "Test", "Test Road", 100, 1);
        int cashRequired = -50;
        int numberOf50sRequired = 0;

        int calculateNote50sRequiredResponse = atmService.calculateNote50sRequired(cashRequired, atm);

        assertThat(calculateNote50sRequiredResponse).isEqualTo(numberOf50sRequired);
    }

}