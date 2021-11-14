package com.uuhnaut69.example.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.javafaker.Faker;
import com.uuhnaut69.example.api.dto.TransferMoneyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

  public static final String CUSTOMER_KEYSPACE = "customer:";

  private final RedissonClient redissonClient;

  private final RedisTemplate<String, String> redisTemplate;

  private final RedisScript<Boolean> moneyTransferScript;

  public void dummyData() {
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

  public boolean transferMoney(TransferMoneyDTO transferMoneyDTO) {
    return Boolean.TRUE.equals(
        redisTemplate.execute(
            moneyTransferScript,
            List.of(transferMoneyDTO.getFrom(), transferMoneyDTO.getTo()),
            transferMoneyDTO.getAmount().toString()));
  }

  public void clearDatabase() {
    redissonClient.getKeys().flushall();
  }
}
