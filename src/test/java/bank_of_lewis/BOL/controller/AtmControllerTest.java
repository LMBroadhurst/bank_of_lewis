package bank_of_lewis.BOL.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class ATMControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Successful 2xx API call, get all Atms")
    void getAllAtmsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/atm/getAllAtms"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Successful 2xx API call, delete Atm")
    void deleteAtmTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/atm/getAllAtms"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }
}