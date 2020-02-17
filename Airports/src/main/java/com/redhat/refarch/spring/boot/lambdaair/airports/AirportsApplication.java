package com.redhat.refarch.spring.boot.lambdaair.airports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AirportsApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run( AirportsApplication.class, args );
	}
}