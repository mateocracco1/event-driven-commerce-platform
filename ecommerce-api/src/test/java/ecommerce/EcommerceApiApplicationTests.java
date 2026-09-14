package ecommerce;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Integration test: requires a running MySQL instance. Run manually with the DB up.")
class EcommerceApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
