package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.repo.AtmRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AtmService {

    private final AtmRepo atmRepo;

    public ResponseEntity<Atm> addNewATM(Atm atm) {
        atmRepo.save(atm);
        return new ResponseEntity<>(atm, HttpStatus.OK);
    }

    public ResponseEntity<List<Atm>> getAllAtms() {
        return new ResponseEntity<>(atmRepo.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<String> generateAtmReport(Long id) {
        Optional<Atm> atmOptional = atmRepo.findById(id);

        if (atmOptional.isEmpty()) {
            return new ResponseEntity<>("Specified ATM could not be found.", HttpStatus.BAD_REQUEST);
        }

        Atm atm = atmOptional.get();
        String note20s = String.valueOf(atm.getNote20());
        String note50s = String.valueOf(atm.getNote50());
        String returnStatement = "$20 notes: " + note20s + "& $50 notes: " + note50s;

        return new ResponseEntity<>(returnStatement, HttpStatus.OK);
    }

    public ResponseEntity<Atm> deleteAtm(Long id) {
        Atm deletedAtm = atmRepo.getById(id);
        atmRepo.delete(deletedAtm);
        return new ResponseEntity<>(deletedAtm, HttpStatus.OK);
    }

    public ResponseEntity<Atm> editAtm(Long id, Atm atm) {
        Atm editedAtm = atmRepo.getById(id);
        atmRepo.save(atm);
        return new ResponseEntity<>(editedAtm, HttpStatus.OK);
    }
}