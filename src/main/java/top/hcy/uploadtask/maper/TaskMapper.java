package top.hcy.uploadtask.maper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.hcy.uploadtask.entity.Task;

/**
 * @ClassName: TaskMapper
 * @Author: hcy
 * @Description:
 * @Date: 2019-10-26 01:05
 * @Version: 1.0
 **/
@Repository
public interface TaskMapper  extends JpaRepository<Task,Integer> {
}
