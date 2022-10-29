package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.repo.AtmRepo;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AtmServiceTest {

    @Mock
    private AtmRepo atmRepo;

    @Autowired
    @InjectMocks
    private AtmService atmService;

    private Atm atm1;
    private Atm atm2;
    private Atm atmMahiTest;
    List<Atm> atmList;

    @BeforeEach
    public void setup() {
        atmList = new ArrayList<>();

        atm1 = new Atm(1L, "ATM 1", "London", 10, 20);
        atm2 = new Atm(2L, "ATM 2", "Birmingham", 10, 20);
        atmMahiTest = new Atm(3L, "Mahi Test", "London", 8, 3);

        atmList.add(atm1);
        atmList.add(atm2);
        atmList.add(atmMahiTest);
    }

    @AfterEach
    public void tearDown() {
        atm1 = atm2 = atmMahiTest = null;
        atmList = null;
    }

    @Test
    @DisplayName("Testing save ATM service layer, POST/save test")
    public void saveAtmTest__serviceTest() {
        when(atmRepo.save(any())).thenReturn(atm1);
        atmService.addNewATM(atm1);
        verify(atmRepo, times(1)).save(any());
        verify(atmRepo, times(1)).findAll();
    }

//    @Test
//    @DisplayName("Testing save ATM service layer, delete test")
//    public void deleteAtmTest__serviceTest() {
//        when(atmService.deleteAtm(atmMahiTest.getId()).thenReturn(ResponseEntity.ok().body("Successfully deleted."));
//        verify(atmRepo, times(1)).findAll();
//    }

}