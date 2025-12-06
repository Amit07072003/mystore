
package org.mystore;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "MyStore API",
                version = "1.0",
                description = "API documentation for MyStore application",
                contact = @Contact(
                        name = "Amit Kumar",
                        email = "amit@example.com",
                        url = "https://github.com/amit"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
public class MystoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MystoreApplication.class, args);
    }

}
