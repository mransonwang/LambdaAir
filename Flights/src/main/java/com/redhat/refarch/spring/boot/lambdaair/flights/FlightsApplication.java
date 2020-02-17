package com.redhat.refarch.spring.boot.lambdaair.flights;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FlightsApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run( FlightsApplication.class, args );
	}
}