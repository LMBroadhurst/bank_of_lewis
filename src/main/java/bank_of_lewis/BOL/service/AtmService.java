package bank_of_lewis.BOL.service;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.repo.AtmRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AtmService {

    private final AtmRepo atmRepo;

    public Atm addNewATM(Atm atm) {
        atmRepo.save(atm);
        return atm;
    }

    public List<Atm> getAllAtms() {
        return atmRepo.findAll();
    }

    public String generateAtmReport(Long id) {
        Atm atm = atmRepo.getById(id);
        String note20s = String.valueOf(atm.getNote20());
        String note50s = String.valueOf(atm.getNote50());
        return "$20 notes:" + note20s;
    }

    public void deleteAtm(Long id) {
        Atm deletedAtm = atmRepo.getById(id);
        atmRepo.delete(deletedAtm);
    }

    public void editAtm(Long id, Atm atm) {
        Atm editedAtm = atmRepo.getById(id);
        atmRepo.save(atm);
    }
}