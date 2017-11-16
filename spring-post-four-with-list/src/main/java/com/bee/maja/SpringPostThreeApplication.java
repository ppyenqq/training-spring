package com.bee.maja;

import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bee.maja.model.Past;
import com.bee.maja.repo.PastRepository;
import com.bee.maja.repo.impl.PastRepositoryImpl;

@SpringBootApplication
public class SpringPostThreeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringPostThreeApplication.class, args);
		System.out.println("helo");
		
	}
}
