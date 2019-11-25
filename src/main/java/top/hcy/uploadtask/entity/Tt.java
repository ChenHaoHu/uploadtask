package top.hcy.uploadtask.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "tt")
public class Tt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer taskId;

    Timestamp time1;

    Date time2;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP")
    Date time3;


    @Temporal(TemporalType.DATE)
    @Column(columnDefinition = "Date")
    Date time4;
}
