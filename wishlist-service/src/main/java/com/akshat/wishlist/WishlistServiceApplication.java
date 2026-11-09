package com.akshat.wishlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		excludeName = "org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration"
)
public class WishlistServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WishlistServiceApplication.class, args);
	}

}
