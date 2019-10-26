package top.hcy.uploadtask.maper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.hcy.uploadtask.entity.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest

class TaskMapperTest {


    @Autowired
    TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {

        List<Task> all = taskMapper.findAll();
        all.forEach(task -> {
            System.out.println(task);
        });
    }


    @Test
    void t1(){
        Task s = new Task();
        s.setTaskName("task2");
        s.setAuthor("hcy2");
        s.setTaskUrl("/uu");
        taskMapper.save(s);
    }
}