package pro.sky.telegrambot.repository;

import org.springframework.data.repository.CrudRepository;
import pro.sky.telegrambot.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
