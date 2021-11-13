package com.uuhnaut69.example.api;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.javafaker.Faker;
import com.uuhnaut69.example.api.dto.TransferMoneyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  private final RedisScript<Boolean> moneyTransferScript;

  private final RedisTemplate<String, String> redisTemplate;

  @PostMapping("/init")
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
              redisTemplate
                  .opsForHash()
                  .putAll(CUSTOMER_KEYSPACE.concat(customer.get("id")), customer);
              log.info("Inserted {}", customer);
            });
  }

  @PostMapping("/transfer-money")
  public boolean transferMoney(@RequestBody @Valid TransferMoneyDTO transferMoneyDTO) {
    return Boolean.TRUE.equals(
        redisTemplate.execute(
            moneyTransferScript,
            List.of(transferMoneyDTO.getFrom(), transferMoneyDTO.getTo()),
            transferMoneyDTO.getAmount().toString()));
  }
}
