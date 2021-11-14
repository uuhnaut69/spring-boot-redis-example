package com.uuhnaut69.example.api;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.javafaker.Faker;
import com.uuhnaut69.example.api.dto.TransferMoneyDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

  public static final String CUSTOMER_KEYSPACE = "customer:";

  private final RedissonClient redissonClient;

  private final RedisTemplate<String, String> redisTemplate;

  private final RedisScript<Boolean> moneyTransferScript;

  @PostMapping("/init")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Init dummy data")
  public void initCustomers() {
    var faker = new Faker();
    IntStream.range(0, 5)
        .forEach(
            value -> {
              var customer =
                  Map.of(
                      "id",
                      NanoIdUtils.randomNanoId(),
                      "fullName",
                      faker.name().fullName(),
                      "address",
                      faker.address().fullAddress(),
                      "balance",
                      "10000");
              var data =
                  redissonClient.getMap(
                      CUSTOMER_KEYSPACE.concat(customer.get("id")), new StringCodec());
              data.putAll(customer);
              log.info("Inserted {}", customer);
            });
  }

  @PostMapping("/transfer-money")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Transfer money from customer to customer")
  public boolean transferMoney(@RequestBody @Valid TransferMoneyDTO transferMoneyDTO) {
    return Boolean.TRUE.equals(
        redisTemplate.execute(
            moneyTransferScript,
            List.of(transferMoneyDTO.getFrom(), transferMoneyDTO.getTo()),
            transferMoneyDTO.getAmount().toString()));
  }

  @DeleteMapping("/clear")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Clear all data")
  public void clearDatabase() {
    redissonClient.getKeys().flushall();
  }
}
