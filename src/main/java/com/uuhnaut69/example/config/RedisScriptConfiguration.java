package com.uuhnaut69.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisScriptConfiguration {

  @Bean
  public RedisScript<Boolean> moneyTransferScript() {
    var moneyTransferScriptSrc =
        new ResourceScriptSource(new ClassPathResource("scripts/customer_money_transfer.lua"));
    return RedisScript.of(moneyTransferScriptSrc.getResource(), Boolean.class);
  }
}
