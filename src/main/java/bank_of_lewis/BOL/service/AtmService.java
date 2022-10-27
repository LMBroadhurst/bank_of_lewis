package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.model.CashToAdd;
import bank_of_lewis.BOL.repo.AtmRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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

    public String dispenseNotes (Long id, int cashRequired) {
        Atm atm = atmRepo.getById(id);
        int cashToDispense = cashRequired;

        if (cashRequired > atm.calculateTotalCash()) {
            return "No way sire";
        }

        if (!Objects.equals(checkForPerfectMatch(atm, cashToDispense), "No match")) {
            return checkForPerfectMatch(atm, cashToDispense);
        }



        int numberOf50s = Math.floorDiv(cashToDispense, 50);
        if (numberOf50s > 0) {
            cashToDispense = cashToDispense - (numberOf50s * 50);
        }

        if (numberOf50s > atm.getNote50()) {
            return "Not enough 50s";
        } else {
            int note50sAfterDispense = atm.getNote50() - numberOf50s;
            atm.setNote50(note50sAfterDispense);
            System.out.println(atm.getNote50());
        }

        int numberOf20s = Math.floorDiv(cashToDispense, 20);
        if (numberOf20s > 0) {
            cashToDispense = cashToDispense - (numberOf20s * 20);
        }

        if (numberOf20s > atm.getNote20()) {
            return "Not enough 20s";
        } else {
            int note20sAfterDispense = atm.getNote20() - numberOf20s;
            atm.setNote20(note20sAfterDispense);
            System.out.println(atm.getNote20());
        }

        if (cashToDispense != 0) {
            return "Cannot dispense this value.";
        }

        atmRepo.save(atm);

        return numberOf20s + " 20s and " + numberOf50s + " 50s dispensed.";
    }

    public String checkForPerfectMatch(Atm atm, int cashRequired) {
        String output = "No match";

        if (cashRequired % 50 == 0 && atm.totalInNote50() > cashRequired) {
            int note50sToDispense = cashRequired / 50;
            atm.setNote50(atm.getNote50() - note50sToDispense);
            System.out.println(atm.getNote50());
            atmRepo.save(atm);
            output = note50sToDispense + " 50s";
        }

        if (cashRequired % 20 == 0 && atm.totalInNote20() > cashRequired) {
            int note20sToDispense = cashRequired / 20;
            atm.setNote20(atm.getNote20() - note20sToDispense);
            System.out.println(atm.getNote20());
            atmRepo.save(atm);
            output = note20sToDispense + " 20s";
        }

        return output;
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