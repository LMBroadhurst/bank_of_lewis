package bank_of_lewis.BOL.controller;

import bank_of_lewis.BOL.service.ATMService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atm")
@RequiredArgsConstructor
public class ATMController {

    private final ATMService atmService;
}
