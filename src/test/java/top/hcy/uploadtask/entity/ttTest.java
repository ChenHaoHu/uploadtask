package top.hcy.uploadtask.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.hcy.uploadtask.maper.TTMapper;

import java.sql.Timestamp;


class ttTest {



    @Test
    void insertTime(){
        Integer a = null;
        int b = (int)a;
        System.out.println(a);
        System.out.println(b);

        if (a!=null){
            System.out.println(a.intValue());
        }
    }

}