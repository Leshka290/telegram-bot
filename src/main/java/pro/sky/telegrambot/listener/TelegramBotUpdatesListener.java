package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot = new TelegramBot("BOT_TOKEN");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            if (!update.message().text().isEmpty()) {
                String messageText = update.message().text();
                long chatId = update.message().chat().id();

                if (update.message().text().contains("/send")) {
                    var textToSend = messageText.substring(messageText.indexOf(" "));

                    sendTaskMessage(textToSend, update.message().chat().id());
                } else {
                    switch (messageText) {
                        case ("/start"): {
                            registerUser(update.message());

                            startCommandReceived(chatId, update.message().chat().firstName());
                            break;
                        }
                        case ("/help"): {
                            prepareAndSendMessage(chatId, "Введите {/send dd.MM.yyyy HH:mm:ss Task} для добавления нового уведомления");

                        }
                        default: {
                            prepareAndSendMessage(chatId, "Команда не распознана");
                            break;
                        }
                    }
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;

    }

    private void registerUser(Message msg) {

        if (userRepository.findById(msg.from().id()).isEmpty()) {
            var chat = msg.chat();

            User user = new User();
            user.setChatId(chat.id());
            user.setFirstName(chat.firstName());
            user.setLastName(chat.lastName());
            user.setUserName(chat.username());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);

            logger.info("User saved: " + user);
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Привет " + name;

        logger.info("Replied to user " + name);

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage(chatId, textToSend);

        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        telegramBot.execute(message);
    }

    private void prepareAndSendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage(chatId, textToSend);

        executeMessage(message);
    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendTask() {

        var tasks = notificationTaskRepository.findAll();
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        for (NotificationTask task : tasks) {
                if (task.getDateTime().equals(localDateTime)) {
                    prepareAndSendMessage(task.getUserId(), task.getTask());
                }
        }
    }

    private void sendTaskMessage(String message, Long userId) {

        Pattern pattern = Pattern.compile("\\d{2}.\\d{2}.\\d{4}.\\d{2}:\\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String timeDateStr = matcher.group(0);
            NotificationTask notificationTask = new NotificationTask();

            LocalDateTime localDateTime = LocalDateTime
                    .parse(timeDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

            notificationTask.setId(1L);
            notificationTask.setDateTime(localDateTime);
            notificationTask.setTask(message);
            notificationTask.setUserId(userId);

            notificationTaskRepository.save(notificationTask);
        }
    }
}
