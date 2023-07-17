package pro.sky.telegrambot.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "notification_task_table")
@Getter
@Setter
public class NotificationTask {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime dateTime;

    private String task;
}
