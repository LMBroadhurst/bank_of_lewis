package bank_of_lewis.BOL.repo;

import bank_of_lewis.BOL.model.Atm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class AtmRepoTest {

    @Autowired
    private AtmRepo atmRepo;

    private Atm lewisTest;
    private Atm mahiTest;
    private Atm bolTest;
    private Atm bobTest;
    private Atm pcTest;

    @BeforeEach
    public void setup() {
        lewisTest = new Atm(1L, "ATM 1", "Birmingham", 20, 10);
        mahiTest = new Atm(2L, "Mahi Test", "London", 8, 3);
        bolTest = new Atm(3L, "Bank of Lewis", "Leeds", 100, 100);
        bobTest = new Atm(4L, "Bank of Broadhurst", "Manchester", 100, 100);


        atmRepo.save(lewisTest);
        atmRepo.save(mahiTest);
        atmRepo.save(bolTest);
        atmRepo.save(bobTest);
    }

    @AfterEach
    public void reset() {
        atmRepo.deleteAll();
        lewisTest = mahiTest = bolTest = pcTest = null;
    }

    @Test
    @DisplayName("Successfully finds ATM optional with ID")
    public void findByIdRepo__test() {

        Optional<Atm> atmTestOptional = atmRepo.findById(1L);
        Atm atmTest = null;

        if (atmTestOptional.isPresent()) {
            atmTest = atmTestOptional.get();
        }

        assertEquals(atmTest, atmRepo.findAll().get(0));
    }

    @Test
    @DisplayName("Successfully finds ATM via name")
    public void findByNameRepo__test() {

        Optional<Atm> atmTestOptional = atmRepo.findByName("Mahi Test");
        Atm atmTest = null;

        if (atmTestOptional.isPresent()) {
            atmTest = atmTestOptional.get();
        }

        assertEquals(atmTest, atmRepo.findAll().get(1));
    }

    @Test
    @DisplayName("Successfully saves ATM to repo")
    public void saveAtmToRepo__test() {
        pcTest = new Atm(5L, "PC Test", "Virtual", 100, 100);
        atmRepo.save(pcTest);

        assertEquals(5, atmRepo.findAll().size());
    }

    @Test
    @DisplayName("Successfully deletes ATM from repo")
    public void deleteAtmFromRepo__test() {
        atmRepo.delete(lewisTest);

        assertEquals(3, atmRepo.findAll().size());
    }
}