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

        String handle1 = attemptMultiple50sDispense(cashRequired, atm);
        if (!handle1.equals("No match")) {
            return handle1;
        }

        String handle2 = attempt20s50sCombination(cashRequired, atm);
        if (!handle2.equals("No match")) {
            return handle2;
        }

        String handle3 = attempt20s50sComboMinus50(cashRequired, atm);
        if (!handle3.equals("No match")) {
            return handle3;
        }

        String handle4 = attemptMultiple20sDispense(cashRequired, atm);
        if (!handle4.equals("No match")) {
            return handle4;
        }

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


    public String attemptMultiple20sDispense(int cashRequired, Atm atm) {
        if (cashRequired % 20 == 0) {
            int note20sToDispense = cashRequired / 20;

            Boolean handleCheckNoteAvailability = checkNoteAvailability(note20sToDispense, 0, atm);
            if (!handleCheckNoteAvailability) {
                System.out.println("NM4");
                return "No match";
            }

            int note20sRemainingInAtm = atm.getNote20() - note20sToDispense;
            atm.setNote20(note20sRemainingInAtm);
            atmRepo.save(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return note20sToDispense + " $20 notes dispensed." + " 0 $50 notes dispensed. 4";
        }

        System.out.println("NM4");
        return "No match";
    }

    public String attemptMultiple50sDispense(int cashRequired, Atm atm) {
        if (cashRequired % 50 == 0) {
            int note50sToDispense = cashRequired / 50;

            Boolean handleCheckNoteAvailability = checkNoteAvailability(0, note50sToDispense, atm);
            if (!handleCheckNoteAvailability) {
                System.out.println("NM1");
                return "No match";
            }

            int note50sRemainingInAtm = atm.getNote50() - note50sToDispense;
            atm.setNote50(note50sRemainingInAtm);
            atmRepo.save(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return "0 $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 1";
        }

        System.out.println("NM1");
        return "No match";
    }

    public String attempt20s50sCombination(int cashRequired, Atm atm) {
        int note50sToDispense = Math.floorDiv(cashRequired, 50);
        int cashLeftToDispense = cashRequired - (50 * note50sToDispense);

        if (note50sToDispense > 0 && cashLeftToDispense % 20 == 0) {
            int note20sToDispense = cashLeftToDispense / 20;

            Boolean handleCheckNoteAvailability = checkNoteAvailability(note20sToDispense, note50sToDispense, atm);
            if (!handleCheckNoteAvailability) {
                System.out.println("NM2");
                return "No match";
            }

            int note20sRemainingInAtm = atm.getNote20() - note20sToDispense;
            int note50sRemainingInAtm = atm.getNote50() - note50sToDispense;
            atm.setNote20(note20sRemainingInAtm);
            atm.setNote50(note50sRemainingInAtm);
            atmRepo.save(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 2";
        }

        System.out.println("NM2");
        return "No match";
    }

    public String attempt20s50sComboMinus50 (int cashRequired, Atm atm) {
        int note50sToDispense = Math.floorDiv(cashRequired, 50) - 1;
        int cashLeftToDispense = cashRequired - (50 * note50sToDispense);

        if (note50sToDispense > 0 && cashLeftToDispense % 20 == 0) {
            int note20sToDispense = cashLeftToDispense / 20;

            Boolean handleCheckNoteAvailability = checkNoteAvailability(note20sToDispense, note50sToDispense, atm);
            if (!handleCheckNoteAvailability) {
                System.out.println("NM3");
                return "No match";
            }

            int note20sRemainingInAtm = atm.getNote20() - note20sToDispense;
            int note50sRemainingInAtm = atm.getNote50() - note50sToDispense;
            atm.setNote20(note20sRemainingInAtm);
            atm.setNote50(note50sRemainingInAtm);
            atmRepo.save(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 3";
        }

        System.out.println("NM3");
        return "No match";
    }

    public Boolean checkNoteAvailability(int numberOfNote20s, int numberOfNote50s, Atm atm) {
        if (numberOfNote20s > atm.getNote20() || numberOfNote50s > atm.getNote50()) {
            return false;
        }
        return true;
    }


}