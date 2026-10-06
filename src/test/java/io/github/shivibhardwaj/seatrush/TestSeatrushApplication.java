package io.github.shivibhardwaj.seatrush;

import org.springframework.boot.SpringApplication;

public class TestSeatrushApplication {

	public static void main(String[] args) {
		SpringApplication.from(SeatrushApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
