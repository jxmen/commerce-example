package dev.jxmen.commerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class CommerceApplicationTests {

	@Container
	@ServiceConnection // datasource 설정을 자동으로 주입
	static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

	@Test
	void contextLoads() {
	}

}
