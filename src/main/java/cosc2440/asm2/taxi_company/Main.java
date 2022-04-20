package cosc2440.asm2.taxi_company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "cosc2440.asm2.taxi_company", entityManagerFactoryRef = "sessionFactoryBean")
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
