package com.dreamfoxick.telegrambot.services.message.processor.impl.ul;

import com.dreamfoxick.telegrambot.services.message.processor.impl.AbstractCommandProcessor;
import com.dreamfoxick.telegrambot.services.register.QueryRegister;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.dreamfoxick.telegrambot.services.message.creator.SendMessageCreator.createSendMessageWithReplyKeyboard;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

@Slf4j
public abstract class AbstractULCommandProcessor extends AbstractCommandProcessor {
    private final static int PROFILES_ON_ONE_MESSAGE = 6;

    public AbstractULCommandProcessor(QueryRegister queryRegister) {
        super(queryRegister);
    }

    private static List<String> buildResponse(List<String> links,
                                              List<String> profiles) {
        return Arrays.asList(
                range(0, links.size())
                        .mapToObj(index -> index != 0 && index % PROFILES_ON_ONE_MESSAGE == 0 ?
                                format("%s\n%s\n|", profiles.get(index), links.get(index)) :
                                format("%s\n%s\n", profiles.get(index), links.get(index)))
                        .collect(joining("\n"))
                        .split("[|]"));
    }

    @Override
    public void process(long chatId,
                        String link,
                        TelegramLongPollingBot bot) throws TelegramApiException, IOException, IllegalArgumentException {
        // отправляю сообщение заголовок
        this.sendHeading(chatId, bot);
        log.debug(format("Sending message heading, chatId: %d", chatId));

        // получаю Map<String, String[]> с информацией
        val result = this.getResult(link);

        // достаю ссылки на книги/авторов из мапы
        val links = this.getLinks(result);
        log.debug(format("Links array constructed: \n%s\n", String.join("\n", links)));

        // достаю информацию о книгах/авторах из мапы
        val profiles = this.getProfiles(result);
        log.debug(format("Profile array constructed: \n%s\n", String.join("\n", profiles)));

        // создаю ответ
        val response = buildResponse(links, profiles);
        log.debug("Response array built");

        // обновляю состояние в соответсвии с запросом
        this.updateState(chatId);

        this.sendResult(chatId, response, bot);
    }

    protected abstract String headingMessage();

    protected abstract ReplyKeyboardMarkup headingKeyboard();

    protected abstract List<String> getLinks(Map<String, String[]> result);

    protected abstract List<String> getProfiles(Map<String, String[]> result);

    private void sendHeading(long chatId,
                             TelegramLongPollingBot bot) throws TelegramApiException {
        bot.execute(
                createSendMessageWithReplyKeyboard(
                        chatId,
                        this.headingMessage(),
                        this.headingKeyboard()));
    }
}
