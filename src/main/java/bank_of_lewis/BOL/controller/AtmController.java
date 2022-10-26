package bank_of_lewis.BOL.controller;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.service.AtmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atm")
@RequiredArgsConstructor
public class AtmController {

    private final AtmService atmService;

    @PostMapping("/createAtm")
    public Atm createAtm (@RequestBody Atm atm) {
        return atmService.addNewATM(atm);
    }

    @GetMapping("/generateAtmReport/{id}")
    public String generateAtmReport(@RequestParam Long id) {
        return atmService.generateAtmReport(id);
    }

    @GetMapping("/getAllAtms")
    public List<Atm> getAllAtms () {
        return atmService.getAllAtms();
    }

    @DeleteMapping("/deleteAtm/{id}")
    public String deleteAtm(@RequestParam Long id) {
        atmService.deleteAtm(id);
        return "hi";
    }

    @PutMapping("/editAtm/{id}")
    public Atm editAtm(@RequestParam Long id, @RequestBody Atm atm) {
        atmService.editAtm(id, atm);
        return atm;
    }
}