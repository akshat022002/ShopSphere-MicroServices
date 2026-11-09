package com.akshat.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		excludeName = "org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration"
)
public class CouponServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouponServiceApplication.class, args);
	}

}
