package bank_of_lewis.BOL.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Atm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

}
