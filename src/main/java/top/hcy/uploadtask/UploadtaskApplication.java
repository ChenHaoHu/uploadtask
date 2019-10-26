package top.hcy.uploadtask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class UploadtaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadtaskApplication.class, args);
    }

}
