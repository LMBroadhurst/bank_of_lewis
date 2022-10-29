package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.repo.AtmRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;

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

}