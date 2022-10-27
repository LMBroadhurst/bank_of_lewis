package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.model.CashToAdd;
import bank_of_lewis.BOL.repo.AtmRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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
        String returnStatement = "$20 notes: " + note20s + ". $50 notes: " + note50s + ". Total cash holding: $" + atm.calculateTotalCash();

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

    public String dispenseNotes (Long id, int cashRequired) {
        Atm atm = atmRepo.getById(id);

        if (cashRequired > atm.calculateTotalCash()) {
            return "No way sire";
        }

//        Checking for "perfect match"
        int numberOf50s = Math.floorDiv(cashRequired, 50);
        if (numberOf50s > 0 && cashRequired % 50 == 0 && numberOf50s < atm.getNote50()) {
            int note50sAfterDispense = atm.getNote50() - numberOf50s;
            atm.setNote50(note50sAfterDispense);
            atmRepo.save(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return "0 $20 notes dispensed. " + numberOf50s + " $50 notes dispensed. 1";
        }
        else if (numberOf50s > 0 && cashRequired % 50 == 0 && numberOf50s > atm.getNote50()) {
            numberOf50s = atm.getNote50();
        }

//        If $50 denominations cannot be matched, move to combo of $50 and $20
        int cashLeftToDispense = cashRequired - (numberOf50s * 50);
        int numberOf20s = 0;
        if (numberOf50s > 0 && cashLeftToDispense % 20 == 0 && cashLeftToDispense > 0) {
            numberOf20s = cashLeftToDispense / 20;
            atm.setNote50(atm.getNote50() - numberOf50s);
            atm.setNote20(atm.getNote20() - numberOf20s);
            atmRepo.save(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return numberOf20s + " $20 notes dispensed. " + numberOf50s + " $50 notes dispensed. 2";
        }

//        If more than 1 50, checking if removing a 50 can solve the math
        cashLeftToDispense = cashRequired;
        numberOf50s = numberOf50s - 1;
        cashLeftToDispense = cashLeftToDispense - (numberOf50s * 50);
        if (numberOf50s >= 1 && cashLeftToDispense % 20 == 0) {
            numberOf20s = cashLeftToDispense / 20;
            atm.setNote50(atm.getNote50() - numberOf50s);
            atm.setNote20(atm.getNote20() - numberOf20s);
            atmRepo.save(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return numberOf20s + " $20 notes dispensed. " + numberOf50s + " $50 notes dispensed. 3";
        }

//        Final check to see if multiple of $20
        if (cashRequired % 20 == 0 && atm.totalInNote20() > cashRequired) {
            int note20sToDispense = cashRequired / 20;
            atm.setNote20(atm.getNote20() - note20sToDispense);
            atmRepo.save(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return note20sToDispense + " $20 notes dispensed." + " 0 $50 notes dispensed. 4";
        }

        atmRepo.save(atm);

        return "Cannot dispense this value. Please try another. 5.";
    }

    public String addNotes(Long id, CashToAdd cashToAdd) {
        Atm atm = atmRepo.getById(id);
        int note20sToAdd = cashToAdd.getNote20s();
        int note50sToAdd = cashToAdd.getNote50s();

        atm.setNote20(atm.getNote20() + note20sToAdd);
        atm.setNote50(atm.getNote50() + note50sToAdd);

        atmRepo.save(atm);

        return atm.getNote20().toString() + " " + atm.getNote50().toString();
    }

}