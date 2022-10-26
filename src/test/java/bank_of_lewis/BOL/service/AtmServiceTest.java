package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.repo.AtmRepo;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

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
    List<Atm> atmList;

    @BeforeEach
    public void setup() {
        atmList = new ArrayList<>();

        atm1 = new Atm(1L, "ATM 1", "London", 10, 20);
        atm2 = new Atm(2L, "ATM 2", "Birmingham", 10, 20);

        atmList.add(atm1);
        atmList.add(atm2);
    }

    @AfterEach
    public void tearDown() {
        atm1 = atm2 = null;
        atmList = null;
    }

}