package pro.sky.telegrambot.repository;

import org.springframework.data.repository.CrudRepository;
import pro.sky.telegrambot.model.NotificationTask;

public interface NotificationTaskRepository extends CrudRepository<NotificationTask, Long> {
}
