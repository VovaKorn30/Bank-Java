package com.example.bank_api.integration;

import com.example.bank_api.dto.CardDto;
import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@ActiveProfiles("test")
public class ClientControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CardService cardService;

    private static final String BASE_URL = "/api/v1/client";

    @AfterEach
    void tearDown() {
        cardRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();
    }

    private Client saveClientInDB() {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(last, first, mid, age, accounts);
        log.debug("client: " + client);

        Client savedClient = clientRepository.save(client);
        log.debug("savedClient: " + savedClient);

        return savedClient;
    }

    private Client createClient() {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(last, first, mid, age, accounts);
        log.debug("client: " + client);

        return client;
    }

    @Test
    @DisplayName("Успішний пошук всіх клієнтів")
    public void findAllSuccess() throws Exception {
        ClientDto saved1 = ClientDto.valueOf(saveClientInDB());
        ClientDto saved2 = ClientDto.valueOf(saveClientInDB());

        List<ClientDto> list = new ArrayList<>();
        list.add(saved1);
        list.add(saved2);
        log.debug("list: " + list);

        String savedAsJson = objectMapper.writeValueAsString(list);
        log.debug("savedAsJson: " + savedAsJson);

        mockMvc.perform(get(BASE_URL))
                .andDo(print()) // распечатать в консоль MockHttpServletRequest и MockHttpServletResponse
                .andExpect(status().isOk()) // ожидаем статус
                .andExpect(content().json(savedAsJson, true)); // ожидаем JSON
    }

    @Test
    @DisplayName("Успішний пошук ")
    public void findByIdSuccess() throws Exception {
        ClientDto saved = ClientDto.valueOf(saveClientInDB());

        String savedAsJson = objectMapper.writeValueAsString(saved);
        log.debug("savedAsJson: " + savedAsJson);

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(savedAsJson, true));
    }

    @Test
    @DisplayName("Клієнт по id не знайдено")
    public void findByIdFail() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успішне додавання клієнтів ")
    public void saveSuccess() throws Exception {
        ClientDto clientDto = ClientDto.valueOf(createClient());

        String clientDtoAsJson = objectMapper.writeValueAsString(clientDto);
        log.debug("clientDtoAsJson: " + clientDtoAsJson);

        mockMvc.perform(post(BASE_URL).content(clientDtoAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.lastname").value(clientDto.getLastname()))
                .andExpect(jsonPath("$.firstname").value(clientDto.getFirstname()))
                .andExpect(jsonPath("$.middlename").value(clientDto.getMiddlename()))
                .andExpect(jsonPath("$.age").value(clientDto.getAge()));

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Клієнта в БД не знайдено ")
    public void saveFail() throws Exception {
        ClientDto saved = ClientDto.valueOf(saveClientInDB());

        // Создаём дубликат по ФИО
        Client duplicate = new Client(
                saved.getLastname(),
                saved.getFirstname(),
                saved.getMiddlename(),
                ThreadLocalRandom.current().nextInt(18, 120),
                Collections.emptyList()
        );
        ClientDto duplicateDto = ClientDto.valueOf(duplicate);
        log.debug("duplicateDto: " + duplicateDto);

        String duplicateDtoAsJson = objectMapper.writeValueAsString(duplicateDto);
        log.debug("duplicateDtoAsJson: " + duplicateDtoAsJson);

        mockMvc.perform(post(BASE_URL).content(duplicateDtoAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Успішне оновлення кліжєтів")
    public void updateSuccess() throws Exception {
        Long id = saveClientInDB().getId();
        ClientDto updateDto = ClientDto.valueOf(createClient());

        String updateAsJson = objectMapper.writeValueAsString(updateDto);
        log.debug("updateAsJson: " + updateAsJson);

        mockMvc.perform(put(BASE_URL + "/" + id).content(updateAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);

        Client updated = clientRepository.findById(id).get();

        assertThat(updated.getLastname()).isEqualTo(updateDto.getLastname());
        assertThat(updated.getFirstname()).isEqualTo(updateDto.getFirstname());
        assertThat(updated.getMiddlename()).isEqualTo(updateDto.getMiddlename());
        assertThat(updated.getAge()).isEqualTo(updateDto.getAge());
    }

    @Test
    @DisplayName("Клієнтів не знайдено")
    public void updateFail() throws Exception {
        Long id = 1L;
        ClientDto updateDto = ClientDto.valueOf(createClient());

        String updateAsJson = objectMapper.writeValueAsString(updateDto);
        log.debug("updateAsJson: " + updateAsJson);

        mockMvc.perform(put(BASE_URL + "/" + id).content(updateAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        assertThat(clientRepository.findAll().size()).isEqualTo(0);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Видалення клієнтів без рахунків ")
    public void deleteSuccess() throws Exception {
        Long id = saveClientInDB().getId();

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(clientRepository.findAll().size()).isEqualTo(0);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Успішне видалення рахунків")
    public void deleteWithAccountsAndCardsSuccess() throws Exception {
        Long clientId = saveClientInDB().getId();

        Long accountId1 = accountService.save(clientId).getId();
        Long accountId2 = accountService.save(clientId).getId();

        CardDto card11 = cardService.save(clientId, accountId1);
        CardDto card12 = cardService.save(clientId, accountId1);
        CardDto card21 = cardService.save(clientId, accountId2);
        CardDto card22 = cardService.save(clientId, accountId2);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(4);

        mockMvc.perform(delete(BASE_URL + "/" + clientId))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(clientRepository.findAll().size()).isEqualTo(0);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Клієнт не знайдено")
    public void deleteFail() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());

        assertThat(clientRepository.findAll().size()).isEqualTo(0);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }
}
