package bank_of_lewis.BOL.controller;

import bank_of_lewis.BOL.model.Atm;
import bank_of_lewis.BOL.model.CashToAdd;
import bank_of_lewis.BOL.repo.AtmRepo;
import bank_of_lewis.BOL.service.AtmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebMvcTest(AtmController.class)
class ATMControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private AtmRepo atmRepo;

    @MockBean
    private AtmService atmService;

    private Atm atm1;
    private Atm atm2;
    private Atm atm3;

    @BeforeEach
    public void setup() {
        atm1 = new Atm(1L, "ATM 1", "London", 10, 20);
        atm2 = new Atm(2L, "ATM 2", "Birmingham", 10, 20);
        atm3 = new Atm(3L, "ATM 3", "London", 8, 3);

        atmRepo.save(atm1);
        atmRepo.save(atm2);
        atmRepo.save(atm3);
    }

    @AfterEach
    public void tearDown() {
        atm1 = atm2 = atm3 = null;
        atmRepo.deleteAll();
    }

    @Test
    @DisplayName("Successful 2xx API call, POST Atm")
    void postAtmByIdTest__success() throws Exception {
        Atm atmPostTest = Atm.builder()
                        .name("ATM Post Test")
                        .location("My PC")
                        .note20(100)
                        .note50(100)
                        .build();

        Mockito.when(atmRepo.save(atmPostTest)).thenReturn(atmPostTest);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/atm/createAtm")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(atmPostTest));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Successful 2xx API call, generate ATM report")
    void getAtmReportByIdTest__success() throws Exception {
        Mockito.when(atmRepo.findById(atm1.getId())).thenReturn(Optional.of(atm1));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/atm/generateAtmReport/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Successful 2xx API call, get all Atms")
    void getAllAtmsTest__success() throws Exception {
        List<Atm> atms = new ArrayList<>(Arrays.asList(atm1, atm2, atm3));
        Mockito.when(atmRepo.findAll()).thenReturn(atms);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/atm/getAllAtms")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Successful 2xx API call, delete Atm")
    void deleteAtmByIdTest__success() throws Exception {
        Mockito.when(atmRepo.findById(atm1.getId())).thenReturn(Optional.of(atm1));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/atm/deleteAtm/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Successful 2xx API call, PUT ATM details")
    void editAtmByIdTest__success() throws Exception {
        Atm updatedAtm = Atm.builder()
                .id(1L)
                .name("PUT test")
                .location("In the cloud")
                .note20(100)
                .note50(100)
                .build();

        Mockito.when(atmRepo.findById(atm1.getId())).thenReturn(Optional.of(atm1));
        Mockito.when(atmRepo.save(updatedAtm)).thenReturn(updatedAtm);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/atm/editAtm/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updatedAtm));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Successful 2xx API call, PUT ATM details")
    void withdrawCashFromAtmByIdTest__success() throws Exception {
        Atm testAtm = Atm.builder()
                .id(1L)
                .name("PUT test")
                .location("In the cloud")
                .note20(100)
                .note50(100)
                .build();

        Mockito.when(atmRepo.findById(atm1.getId())).thenReturn(Optional.of(atm1));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/atm/withdrawCash/2/200/false")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(testAtm));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Successful 2xx API call, add cash to ATM")
    void addNotesToAtmByIdTest__success() throws Exception {
        Atm updatedAtm = Atm.builder()
                .id(1L)
                .name("add cash test")
                .location("In the cloud")
                .note20(100)
                .note50(100)
                .build();

        CashToAdd cashToAdd = new CashToAdd(20, 20);

        Mockito.when(atmRepo.findById(atm1.getId())).thenReturn(Optional.of(atm1));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/atm/addCash/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(cashToAdd));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

}