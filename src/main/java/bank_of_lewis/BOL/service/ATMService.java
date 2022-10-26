package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.repo.ATMRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ATMService {

    private final ATMRepo atmRepo;

}
