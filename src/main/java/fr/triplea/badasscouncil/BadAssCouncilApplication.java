package fr.triplea.badasscouncil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class BadAssCouncilApplication 
{

	public static void main(String[] args) { SpringApplication.run(BadAssCouncilApplication.class, args); }

	@PostConstruct
	public void init() { SecurityContextHolder.clearContext(); }
}
