package bank_of_lewis.BOL.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Atm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String location;
    private Integer note20;
    private Integer note50;

    public int calculateTotalCash() {
        int totalIn20s = note20 * 20;
        int totalIn50s = note50 * 50;

        return totalIn20s + totalIn50s;
    }

    public int totalInNote20() {
        return note20 * 20;
    }

    public int totalInNote50() {
        return note50 * 50;
    }
}
