package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferRequest;
import com.techelevator.tenmo.model.UsernameAndId;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private String authToken = null;
    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public UserService(String url) {
        this.baseUrl = url;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = null;
        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(baseUrl + "balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            balance = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public void updateBalance(int id) {
        try {
            restTemplate.exchange(baseUrl + "balance", HttpMethod.PUT, makeAuthEntity(), Void.class);
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<TransferRequest> makeTransferEntity(TransferRequest transferRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transferRequest, headers);
    }

    public void transferRequest(TransferRequest transferRequest) {
        // type, status, user_id_from, user_id_to, amount
        try {
            restTemplate.exchange(baseUrl + "transfer", HttpMethod.POST, makeTransferEntity(transferRequest), TransferRequest.class);
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public List<UsernameAndId> listUsers() {
        List<UsernameAndId> usernameAndIdList = new ArrayList<>();
        UsernameAndId[] tempArray = null;
        try {
            ResponseEntity<UsernameAndId[]> response  = restTemplate.exchange(baseUrl + "users", HttpMethod.GET, makeAuthEntity(), UsernameAndId[].class);
            tempArray = response.getBody();
            usernameAndIdList.addAll(List.of(tempArray));
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return usernameAndIdList;
    }
}