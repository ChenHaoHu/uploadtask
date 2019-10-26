package top.hcy.uploadtask;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class UploadtaskApplicationTests {

    @Test
    void contextLoads() {
        String str = "tring qq_pic_merged_1559347865805.jpg";

        String[] split = str.split("\\.");

        Arrays.stream(split).forEach(s -> {
            System.out.println(s);
        });

    }

}
