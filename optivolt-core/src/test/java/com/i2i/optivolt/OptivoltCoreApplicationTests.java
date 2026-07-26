package com.i2i.optivolt;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Veritabanı ve Kafka altyapısı olmadan CI/CD'de patlamaması için devredışı bırakıldı.")
@SpringBootTest
class OptivoltCoreApplicationTests {

	@Test
	void contextLoads() {
	}

}
