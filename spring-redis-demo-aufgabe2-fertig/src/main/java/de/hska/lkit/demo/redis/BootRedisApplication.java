package de.hska.lkit.demo.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootRedisApplication {

	public static void main(String[] args) {
		System.out.println("***Klasse:BootRedisApplication, Methode: main wurde aufgerufen.***");
		SpringApplication.run(BootRedisApplication.class, args);
	}
}
