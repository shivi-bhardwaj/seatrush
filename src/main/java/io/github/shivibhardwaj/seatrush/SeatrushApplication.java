package io.github.shivibhardwaj.seatrush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SeatrushApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatrushApplication.class, args);
	}

}
