package pro.sky.telegrambot.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "notification_task_table")
@Getter
@Setter
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private Long userId;

    private LocalDateTime dateTime;

    private String task;
}
