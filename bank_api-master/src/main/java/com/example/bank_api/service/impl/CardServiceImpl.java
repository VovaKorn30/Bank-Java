package com.example.bank_api.service.impl;

import com.example.bank_api.dto.CardDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import com.example.bank_api.exception.AccountNotFoundException;
import com.example.bank_api.exception.CardNotFoundException;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.CardService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Log4j2
public class CardServiceImpl implements CardService {

    private final ClientRepository clientRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CardServiceImpl(ClientRepository clientRepository, CardRepository cardRepository) {
        this.clientRepository = clientRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    @Transactional
    public CardDto save(Long clientId, Long accountId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        Account theAccount = client.getAccounts()
                .stream()
                .filter(account -> account.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("У клієнта с id=" + clientId + " відсутній рахунок с id=" + accountId));

        String cardNumber = RandomStringUtils.randomNumeric(16);

        Date releaseDate = new Date();


        Card card = new Card(cardNumber, releaseDate);
        CardDto saved = CardDto.valueOf(cardRepository.save(card));

        cardRepository.updateCardSetAccount(theAccount, saved.getId());
        log.debug("Клиєpackage com.example.bank_api.service.impl;\n" +
                "\n" +
                "import com.example.bank_api.dto.CardDto;\n" +
                "import com.example.bank_api.entity.Account;\n" +
                "import com.example.bank_api.entity.Card;\n" +
                "import com.example.bank_api.entity.Client;\n" +
                "import com.example.bank_api.exception.AccountNotFoundException;\n" +
                "import com.example.bank_api.exception.CardNotFoundException;\n" +
                "import com.example.bank_api.exception.ClientNotFoundException;\n" +
                "import com.example.bank_api.repository.CardRepository;\n" +
                "import com.example.bank_api.repository.ClientRepository;\n" +
                "import com.example.bank_api.service.CardService;\n" +
                "import lombok.extern.log4j.Log4j2;\n" +
                "import org.apache.commons.lang.RandomStringUtils;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "import org.springframework.transaction.annotation.Transactional;\n" +
                "\n" +
                "import java.util.Date;\n" +
                "\n" +
                "@Service\n" +
                "@Log4j2\n" +
                "public class CardServiceImpl implements CardService {\n" +
                "\n" +
                "    private final ClientRepository clientRepository;\n" +
                "    private final CardRepository cardRepository;\n" +
                "\n" +
                "    @Autowired\n" +
                "    public CardServiceImpl(ClientRepository clientRepository, CardRepository cardRepository) {\n" +
                "        this.clientRepository = clientRepository;\n" +
                "        this.cardRepository = cardRepository;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    @Transactional\n" +
                "    public CardDto save(Long clientId, Long accountId) {\n" +
                "        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));\n" +
                "\n" +
                "        Account theAccount = client.getAccounts()\n" +
                "                .stream()\n" +
                "                .filter(account -> account.getId().equals(accountId))\n" +
                "                .findFirst()\n" +
                "                .orElseThrow(() -> new AccountNotFoundException(\"У клієнта с id=\" + clientId + \" відсутній рахунок с id=\" + accountId));\n" +
                "\n" +
                "        String cardNumber = RandomStringUtils.randomNumeric(16);\n" +
                "\n" +
                "        Date releaseDate = new Date();\n" +
                "\n" +
                "\n" +
                "        Card card = new Card(cardNumber, releaseDate);\n" +
                "        CardDto saved = CardDto.valueOf(cardRepository.save(card));\n" +
                "\n" +
                "        cardRepository.updateCardSetAccount(theAccount, saved.getId());\n" +
                "        log.debug(\"Клиєнту з id=\" + clientId + \" к рахунку з id=\" + accountId + \" була привязана карта: \" + saved);\n" +
                "\n" +
                "        return saved;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void delete(Long clientId, Long cardId) {\n" +
                "        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));\n" +
                "\n" +
                "        Card theCard = client.getAccounts()\n" +
                "                .stream()\n" +
                "                .flatMap(account -> account.getCards().stream())\n" +
                "                .filter(card -> card.getId().equals(cardId))\n" +
                "                .findFirst()\n" +
                "                .orElseThrow(() -> new CardNotFoundException(\"У клєєнта з id=\" + clientId + \" відсутня карта с id=\" + cardId));\n" +
                "\n" +
                "        log.debug(\"У клієнта с id=\" + clientId + \" видалити карту: \" + theCard);\n" +
                "        cardRepository.deleteById(theCard.getId());\n" +
                "    }\n" +
                "}\nнту з id=" + clientId + " к рахунку з id=" + accountId + " була привязана карта: " + saved);

        return saved;
    }

    @Override
    public void delete(Long clientId, Long cardId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        Card theCard = client.getAccounts()
                .stream()
                .flatMap(account -> account.getCards().stream())
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new CardNotFoundException("У клієнта з id=" + clientId + " відсутня карта з id=" + cardId));

        log.debug("У клієнта з id=" + clientId + " видалити карту: " + theCard);
        cardRepository.deleteById(theCard.getId());
    }
}
