package pro.sky.telegrambot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "usersDataTable")
@Getter
@Setter
public class User {

    @Id
    private Long chatId;

    private String firstName;
    private String lastName;
    private String userName;

    private Timestamp registeredAt;

}

