package com.uuhnaut69.example.api;

import com.uuhnaut69.example.api.dto.TransferMoneyDTO;
import com.uuhnaut69.example.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping("/init")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Init dummy data")
  public void initCustomers() {
    customerService.dummyData();
  }

  @PostMapping("/transfer-money")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Transfer money from customer to customer")
  public boolean transferMoney(@RequestBody @Valid TransferMoneyDTO transferMoneyDTO) {
    return customerService.transferMoney(transferMoneyDTO);
  }

  @DeleteMapping("/clear")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Clear all data")
  public void clearDatabase() {
    customerService.clearDatabase();
  }
}
