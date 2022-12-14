package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.model.CashToAdd;
import bank_of_lewis.BOL.repo.AtmRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class AtmService {

    private final AtmRepo atmRepo;

    public ResponseEntity<Atm> addNewATM(Atm atm) {
        Atm addedAtm = atmRepo.save(atm);

//        Just to avoid return of incorrect atm details with e.g. id = 0, as id is auto generated
        int atmRepoSize = atmRepo.findAll().size();
        if (atm.getId() != atmRepoSize) {
            addedAtm = atmRepo.findAll().get(atmRepoSize - 1);
        }

        return ResponseEntity.status(OK)
                .header("Message", "Successfully created ATM." )
                .body(addedAtm);
    }

    public ResponseEntity<List<Atm>> getAllAtms() {

        return ResponseEntity.status(OK)
                .header("Message", "Returns a list of ATMs")
                .body(atmRepo.findAll());
    }

    public ResponseEntity<String> generateAtmReport(Long id) {
        Optional<Atm> atmOptional = atmRepo.findById(id);
        if (atmOptional.isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST)
                    .header("Message", "Could not find ATM with the specified ID.")
                    .body("Could not find ATM with ID " + id);
        }

        Atm atm = atmOptional.get();
        String note20s = String.valueOf(atm.getNote20());
        String note50s = String.valueOf(atm.getNote50());
        String returnStatement = "$20 notes: " + note20s + ". $50 notes: " + note50s + ". Total cash holding: $" + atm.calculateTotalCash();

        return ResponseEntity.status(OK)
                .header("Message", "Successfully generated ATM report.")
                .body(returnStatement);
    }

    public ResponseEntity<String> deleteAtm(Long id) {
        Optional<Atm> atmOptional = atmRepo.findById(id);
        if (atmOptional.isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST)
                    .header("Message", "Could not find ATM with the specified ID.")
                    .body("Could not find ATM with the specified ID.");
        }

        Atm atm = atmOptional.get();
        atmRepo.delete(atm);

        return ResponseEntity.status(OK)
                .header("Message", "Could not find ATM with the specified ID.")
                .body("Deleted the " + atm.getName() + ": " + atm.getLocation() + " atm.");
    }

    public ResponseEntity<String> editAtm(Long id, Atm atm) {
        Optional<Atm> atmOptional = atmRepo.findById(id);
        if (atmOptional.isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST)
                    .header("Message", "Could not find ATM with the specified ID.")
                    .body("Could not find ATM with the specified ID.");
        }

        atm.setId(id);
        atmRepo.save(atm);

        return ResponseEntity.status(OK)
                .header("Message", "Successfully edited ATM.")
                .body(atm.getLocation() + ", " + atm.getName() + ", 50 Notes: " + atm.getNote50() + ", 20 Notes: " + atm.getNote20());
    }

    public ResponseEntity<String> addNotes(Long id, CashToAdd cashToAdd) {
        Optional<Atm> atmOptional = atmRepo.findById(id);
        if (atmOptional.isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST)
                    .header("Message", "Could not find ATM with the specified ID.")
                    .body("Could not find ATM with the specified ID.");
        }

        Atm atm = atmOptional.get();
        int note20sToAdd = cashToAdd.getNote20s();
        int note50sToAdd = cashToAdd.getNote50s();

        atm.setNote20(atm.getNote20() + note20sToAdd);
        atm.setNote50(atm.getNote50() + note50sToAdd);
        atmRepo.save(atm);

        return ResponseEntity.status(OK)
                .header("Message", "Could not find ATM with the specified ID.")
                .body(atm.getNote20().toString() + " 20 notes added. " + atm.getNote50().toString() + " 50 notes added.");
    }

    public ResponseEntity<String> dispenseNotes (Long id, int cashRequired, Boolean prefers20) {
        Optional<Atm> atmOptional = atmRepo.findById(id);
        if (atmOptional.isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST)
                    .header("Message", "Could not find ATM with the specified ID.")
                    .body("Could not find ATM with the specified ID.");
        }

        Atm atm = atmOptional.get();
        if (atm.getNote50() < 5) {
            prefers20 = true;
        }

        if (cashRequired < 20) {
            return ResponseEntity.status(BAD_REQUEST)
                    .header("Message", "Cannot dispense the value specified.")
                    .body("Cannot dispense this value (" + cashRequired + "). Please try another.");

        }

        if (prefers20) {
            String handlePrefers20s = attemptMaximiseNote20sWith50(cashRequired, atm);
            if (!handlePrefers20s.equals("No match")) {
                return ResponseEntity.status(OK)
                        .header("Message", "Dispensed notes successfully.")
                        .body(handlePrefers20s);
            }

            String handlePrefers20sWith50 = attemptMultiple20sDispense(cashRequired, atm);
            if (!handlePrefers20sWith50.equals("No match")) {
                return ResponseEntity.status(OK)
                        .header("Message", "Dispensed notes successfully.")
                        .body(handlePrefers20sWith50);
            }
        }

        String handleAttemptMultiple50sDispense = attemptMultiple50sDispense(cashRequired, atm);
        if (!handleAttemptMultiple50sDispense.equals("No match")) {
            return ResponseEntity.status(OK)
                    .header("Message", "Dispensed notes successfully.")
                    .body(handleAttemptMultiple50sDispense);
        }

        String handleAttempt20s50sCombination = attempt20s50sCombination(cashRequired, atm);
        if (!handleAttempt20s50sCombination.equals("No match")) {
            return ResponseEntity.status(OK)
                    .header("Message", "Dispensed notes successfully.")
                    .body(handleAttempt20s50sCombination);
        }

        String handleAttempt20s50sComboMinus50 = attempt20s50sComboMinus50(cashRequired, atm);
        if (!handleAttempt20s50sComboMinus50.equals("No match")) {
            return ResponseEntity.status(OK)
                    .header("Message", "Dispensed notes successfully.")
                    .body(handleAttempt20s50sComboMinus50);
        }

        String handleAttemptMultiple20sDispense = attemptMultiple20sDispense(cashRequired, atm);
        if (!handleAttemptMultiple20sDispense.equals("No match")) {
            return ResponseEntity.status(OK)
                    .header("Message", "Dispensed notes successfully.")
                    .body(handleAttemptMultiple20sDispense);
        }

        return ResponseEntity.status(BAD_REQUEST)
                .header("Message", "Cannot dispense the value specified.")
                .body("Cannot dispense this value (" + cashRequired + "). Please try another.");
    }

    public String attemptMultiple20sDispense(int cashRequired, Atm atm) {
        if (cashRequired % 20 == 0) {
            int note20sToDispense = cashRequired / 20;

            Boolean handleCheckNoteAvailability = checkNoteAvailability(note20sToDispense, 0, atm);
            if (!handleCheckNoteAvailability) {
                return "No match";
            }

            int note20sRemainingInAtm = atm.getNote20() - note20sToDispense;
            atm.setNote20(note20sRemainingInAtm);
            atmRepo.save(atm);
            zeroOrLowNotesNotifications(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return note20sToDispense + " $20 notes dispensed." + " 0 $50 notes dispensed.";
        }

        return "No match";
    }

    public String attemptMultiple50sDispense(int cashRequired, Atm atm) {
        if (cashRequired % 50 == 0) {
            int note50sToDispense = cashRequired / 50;

            Boolean handleCheckNoteAvailability = checkNoteAvailability(0, note50sToDispense, atm);
            if (!handleCheckNoteAvailability) {
                return "No match";
            }

            int note50sRemainingInAtm = atm.getNote50() - note50sToDispense;
            atm.setNote50(note50sRemainingInAtm);
            atmRepo.save(atm);
            zeroOrLowNotesNotifications(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return "0 $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.";
        }

        return "No match";
    }

    public String attempt20s50sCombination(int cashRequired, Atm atm) {
        int note50sToDispense = calculateNote50sRequired(cashRequired, atm);
        int cashLeftToDispense = cashRequired - (50 * note50sToDispense);

        if (note50sToDispense > 0 && cashLeftToDispense % 20 == 0) {
            int note20sToDispense = cashLeftToDispense / 20;

            Boolean handleCheckNoteAvailability = checkNoteAvailability(note20sToDispense, note50sToDispense, atm);
            if (!handleCheckNoteAvailability) {
                return "No match";
            }

            int note20sRemainingInAtm = atm.getNote20() - note20sToDispense;
            int note50sRemainingInAtm = atm.getNote50() - note50sToDispense;
            atm.setNote20(note20sRemainingInAtm);
            atm.setNote50(note50sRemainingInAtm);
            atmRepo.save(atm);
            zeroOrLowNotesNotifications(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.";
        }

        return "No match";
    }

    public String attempt20s50sComboMinus50 (int cashRequired, Atm atm) {
        int note50sToDispense = calculateNote50sRequired(cashRequired, atm) - 1;
        int cashLeftToDispense = cashRequired - (50 * note50sToDispense);

        if (note50sToDispense > 0 && cashLeftToDispense % 20 == 0) {
            int note20sToDispense = cashLeftToDispense / 20;

            Boolean handleCheckNoteAvailability = checkNoteAvailability(note20sToDispense, note50sToDispense, atm);
            if (!handleCheckNoteAvailability) {
                return "No match";
            }

            int note20sRemainingInAtm = atm.getNote20() - note20sToDispense;
            int note50sRemainingInAtm = atm.getNote50() - note50sToDispense;
            atm.setNote20(note20sRemainingInAtm);
            atm.setNote50(note50sRemainingInAtm);
            atmRepo.save(atm);
            zeroOrLowNotesNotifications(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.";
        }

        return "No match";
    }

    public String attemptMaximiseNote20sWith50(int cashRequired, Atm atm) {
        int cashLeftToDispense = cashRequired - 50;
        if (cashLeftToDispense % 20 == 0 && cashLeftToDispense > 0) {
            int note50sToDispense = 1;
            int note20sToDispense = cashLeftToDispense / 20;
            int note50sRemainingInAtm = atm.getNote50() - note50sToDispense;
            int note20sRemainingInAtm = atm.getNote20() - note20sToDispense;
            atm.setNote50(note50sRemainingInAtm);
            atm.setNote20(note20sRemainingInAtm);
            atmRepo.save(atm);
            zeroOrLowNotesNotifications(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed.";
        }

        return "No match";
    }

    public Boolean checkNoteAvailability(int numberOfNote20s, int numberOfNote50s, Atm atm) {
        return numberOfNote20s <= atm.getNote20() && numberOfNote50s <= atm.getNote50();
    }

    public int calculateNote50sRequired(int cashRequired, Atm atm) {
        int note50sToDispense = Math.floorDiv(cashRequired, 50);

        if (note50sToDispense > atm.getNote50()) {
            note50sToDispense = atm.getNote50();
        }
        if (note50sToDispense < 0) {
            return 0;
        }

        return note50sToDispense;
    }

    public void zeroOrLowNotesNotifications(Atm atm) {
        String note20message = null;
        String note50message = null;

        if (atm.getNote50() == 0) {
            note50message = atm.getName() + " - " + atm.getLocation() + " has ran out of $50 notes.";
        }
        else if (atm.getNote50() < 10) {
            note50message = atm.getName() + " - " + atm.getLocation() + " has less than 10 $50 notes.";
        }

        if (atm.getNote20() == 0) {
            note20message = atm.getName() + " - " + atm.getLocation() + " has ran out of $20 notes.";
        }
        else if (atm.getNote20() < 10) {
            note20message = atm.getName() + " - " + atm.getLocation() + " has less than 10 $20 notes.";
        }

        if (note20message != null) {
            System.out.println(note20message);
        }
        if (note50message != null) {
            System.out.println(note50message);
        }
    }

//    Added last minute to quickly clear the DB
    public ResponseEntity<String> deleteAllAtms() {
        if (atmRepo.findAll().size() > 0) {
            atmRepo.deleteAll();
            return ResponseEntity.status(OK).body("Successfully deleted all ATMs");
        }

        return ResponseEntity.status(BAD_REQUEST).body("No ATMs in system to delete");
    }

}