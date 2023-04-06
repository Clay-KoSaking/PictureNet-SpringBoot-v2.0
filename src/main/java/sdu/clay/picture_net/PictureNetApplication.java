package sdu.clay.picture_net;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication
public class PictureNetApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureNetApplication.class, args);
    }

}
