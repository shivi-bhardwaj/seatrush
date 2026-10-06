package io.github.shivibhardwaj.seatrush;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SeatrushApplicationTests {

	@Test
	void contextLoads() {
	}

}
