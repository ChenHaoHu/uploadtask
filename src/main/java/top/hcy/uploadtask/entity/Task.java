package top.hcy.uploadtask.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName: Task
 * @Author: hcy
 * @Description:
 * @Date: 2019-10-26 00:50
 * @Version: 1.0
 **/


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer taskId;

    String taskName;

    String taskUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @CreationTimestamp
    Date createTime;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    Date modifeTime;

    String author;
}
