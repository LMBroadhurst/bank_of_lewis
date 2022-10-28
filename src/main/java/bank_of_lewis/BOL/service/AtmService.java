package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.model.CashToAdd;
import bank_of_lewis.BOL.model.Response;
import bank_of_lewis.BOL.repo.AtmRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AtmService {

    private final AtmRepo atmRepo;

    public ResponseEntity<Response> addNewATM(Atm atm) {

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("Created ATM data", atmRepo.save(atm)))
                        .message("ATM " + atm.getName() + "/" + atm.getLocation() + " created.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
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

    public String addNotes(Long id, CashToAdd cashToAdd) {
        Atm atm = atmRepo.getById(id);
        int note20sToAdd = cashToAdd.getNote20s();
        int note50sToAdd = cashToAdd.getNote50s();

        atm.setNote20(atm.getNote20() + note20sToAdd);
        atm.setNote50(atm.getNote50() + note50sToAdd);
        atmRepo.save(atm);

        return atm.getNote20().toString() + " " + atm.getNote50().toString();
    }

    public String dispenseNotes (Long id, int cashRequired, Boolean prefers20) {
        try {
            Atm atm = atmRepo.getById(id);

            if (prefers20) {
                String handlePrefers20s = attemptMultiple20sDispense(cashRequired, atm);
                if (!handlePrefers20s.equals("No match")) {
                    return handlePrefers20s;
                }
            }

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

        } catch (Exception e){
            System.out.println(e.getMessage());
            return "There has been an error with the ATM choice or input value. Ensure you have entered a valid integer and ATM ID.";
        }
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
            below10notesNotification(atm);
            zeroNote20sNote50sAvailable(atm);

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
            below10notesNotification(atm);
            zeroNote20sNote50sAvailable(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return "0 $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 1";
        }

        System.out.println("NM1");
        return "No match";
    }

    public String attempt20s50sCombination(int cashRequired, Atm atm) {
        int note50sToDispense = calculateNote50sRequired(cashRequired, atm);
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
            below10notesNotification(atm);
            zeroNote20sNote50sAvailable(atm);

            System.out.println(atm.getNote20().toString() + " - " + atm.getNote50().toString());
            return note20sToDispense + " $20 notes dispensed. " + note50sToDispense + " $50 notes dispensed. 2";
        }

        System.out.println("NM2");
        return "No match";
    }

    public String attempt20s50sComboMinus50 (int cashRequired, Atm atm) {
        int note50sToDispense = calculateNote50sRequired(cashRequired, atm) - 1;
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
            below10notesNotification(atm);
            zeroNote20sNote50sAvailable(atm);

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

    public int calculateNote50sRequired(int cashRequired, Atm atm) {
        int note50sToDispense = Math.floorDiv(cashRequired, 50);
        System.out.println(note50sToDispense);
        if (note50sToDispense > atm.getNote50()) {
            note50sToDispense = atm.getNote50();
        }
        System.out.println(note50sToDispense);

        return note50sToDispense;
    }

    public void below10notesNotification(Atm atm) {
        String note20note50message = "";
        Boolean lowOnNotes = false;
        if (atm.getNote20() < 10 & atm.getNote50() < 10) {
            note20note50message = "20s and 50s.";
            lowOnNotes = true;
        }
        else if (atm.getNote50() < 10) {
            note20note50message = "50s.";
            lowOnNotes = true;
        }
        else if (atm.getNote20() < 10) {
            note20note50message = "20s.";
            lowOnNotes = true;
        }

        if (lowOnNotes.equals(true)) {
            System.out.println(atm.getName() + ": " + atm.getLocation() + "... is running low on " + note20note50message);
        }
    }

    public void zeroNote20sNote50sAvailable(Atm atm) {
        String note20note50message = "";
        Boolean zeroNote20sOr50s = false;

        if (atm.getNote20().equals(0) && atm.getNote50().equals(0)) {
            zeroNote20sOr50s = true;
            note20note50message = "has 0 20 notes and 0 50 notes.";
        }
        else if (atm.getNote50().equals(0)) {
            zeroNote20sOr50s = true;
            note20note50message = "has 0 50 notes.";
        }
        else if (atm.getNote20().equals(0)) {
            zeroNote20sOr50s = true;
            note20note50message = "has 0 20 notes.";
        }

        if (zeroNote20sOr50s.equals(true)) {
            System.out.println(atm.getName() + ": " + atm.getLocation() + "... " + note20note50message);
        }
    }

}