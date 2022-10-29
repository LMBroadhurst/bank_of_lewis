package bank_of_lewis.BOL.controller;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.model.CashToAdd;
import bank_of_lewis.BOL.service.AtmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atm")
@RequiredArgsConstructor
public class AtmController {

    private final AtmService atmService;

    @PostMapping("/createAtm")
    public ResponseEntity<Atm> createAtm (@RequestBody Atm atm) {
        return atmService.addNewATM(atm);
    }

    @GetMapping("/generateAtmReport/{id}")
    public ResponseEntity<String> generateAtmReport(@PathVariable Long id) {
        return atmService.generateAtmReport(id);
    }

    @GetMapping("/getAllAtms")
    public ResponseEntity<List<Atm>> getAllAtms () {
        return atmService.getAllAtms();
    }

    @DeleteMapping("/deleteAtm/{id}")
    public ResponseEntity<String> deleteAtm(@PathVariable Long id) {
        return atmService.deleteAtm(id);
    }

    @PutMapping("/editAtm/{id}")
    public ResponseEntity<String> editAtm(@PathVariable Long id, @RequestBody Atm atm) {
        return atmService.editAtm(id, atm);
    }

    @PutMapping("/withdrawCash/{id}/{cashRequired}/{prefers20s}")
    public ResponseEntity<String> withdrawCash(@PathVariable Long id, @PathVariable int cashRequired, @PathVariable Boolean prefers20s) {
        return atmService.dispenseNotes(id, cashRequired, prefers20s);
    }

    @PutMapping("addCash/{id}")
    public ResponseEntity<String> addCash(@PathVariable Long id, @RequestBody CashToAdd cashToAdd) {
        return atmService.addNotes(id, cashToAdd);
    }

}