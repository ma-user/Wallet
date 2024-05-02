package com.swiggy.Wallet;

import com.swiggy.Wallet.DataAccessLayer.TransactionRepository;
import com.swiggy.Wallet.DataAccessLayer.UserRepository;
import com.swiggy.Wallet.entity.Location;
import com.swiggy.Wallet.entity.Money;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Currency;

@SpringBootApplication
@ComponentScan(basePackages = "com.swiggy.Wallet")
@EnableJpaRepositories("com.swiggy.Wallet.DataAccessLayer")
public class WalletApplication {
	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}

	@Bean
	public CommandLineRunner backfillData(TransactionRepository transactionRepository, UserRepository userRepository) {
		long id = 100L;
		return args -> {
			transactionRepository.findByServiceFeeIsNull().forEach(entity -> {
				Money defaultServiceFee = new Money(BigDecimal.ZERO, Currency.getInstance("INR"));
				entity.setServiceFee(defaultServiceFee);
				transactionRepository.save(entity);
			});
			transactionRepository.findByAmountIsNull().forEach(entity -> {
				Money defaultAmount = new Money(BigDecimal.ZERO, Currency.getInstance("INR"));
				entity.setAmount(defaultAmount);
				transactionRepository.save(entity);
			});
//			transactionRepository.findByTransactionTypeIsNull().forEach(entity -> {
//				entity.setTransactionType(TransactionType.TRANSFER);
//				transactionRepository.save(entity);
//			});
			userRepository.findByLocationIsNull().forEach(entity -> {
				Location defaultLocation = new Location("Indore", "India");
				entity.setLocation(defaultLocation);
				userRepository.save(entity);
			});
		};
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
