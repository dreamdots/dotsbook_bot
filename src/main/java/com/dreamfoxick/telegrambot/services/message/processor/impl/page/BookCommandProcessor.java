package com.dreamfoxick.telegrambot.services.message.processor.impl.page;

import com.dreamfoxick.telegrambot.services.message.processor.impl.AbstractCommandProcessor;
import com.dreamfoxick.telegrambot.services.register.QueryRegister;
import com.dreamfoxick.telegrambot.services.searcher.SearcherInvoker;
import com.dreamfoxick.telegrambot.services.statecontroller.State;
import com.dreamfoxick.telegrambot.services.statecontroller.StateController;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dreamfoxick.telegrambot.services.statecontroller.State.FIND_BOOK_STATE;
import static com.dreamfoxick.telegrambot.utils.StringUtil.insertSlash;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@Component
public class BookCommandProcessor extends AbstractCommandProcessor {
    private final StateController<Long, State> stateController;
    private final SearcherInvoker searcherInvoker;
    private final QueryRegister queryRegister;

    public BookCommandProcessor(StateController<Long, State> stateController,
                                QueryRegister queryRegister,
                                SearcherInvoker searcherInvoker) {
        super(queryRegister);
        this.stateController = stateController;
        this.queryRegister = queryRegister;
        this.searcherInvoker = searcherInvoker;
    }

    @Override
    public void process(long chatId,
                        String link,
                        TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        // восстанавливаю ссылку
        val bookId = insertSlash(link);
        log.debug(format("Book link restored: %s", bookId));

        // получаю Map<String, String[]> с информацией
        val result = this.getResult(bookId);
        log.debug("Result map getting");

        // создаю ответ
        val response = this.buildResponse(result, bookId);
        log.debug(format("Response array constructed: \n%s\n", join("\n", response)));

        // обновляю состояние в соответсвии с запросом
        this.updateState(chatId);

        this.sendResult(chatId, link, response, bot);
    }

    @Override
    protected Map<String, String[]> getResult(String link) throws IOException {
        return searcherInvoker.searchBook(link);
    }

    @Override
    protected void updateState(long chatId) {
        stateController.updateIfPresent(chatId, FIND_BOOK_STATE);
    }

    /**
     * string[0] - fb2
     * string[1] - epub
     * string[2] - mobi
     * string[3] - pdf
     * string[4] - djvi
     * string[5] - doc
     */
    private List<String> buildResponse(Map<String, String[]> result,
                                       String link) {
        return Arrays.stream(result.get(link))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private void sendResult(long chatId,
                            String bookId,
                            List<String> response,
                            TelegramLongPollingBot bot) throws TelegramApiException {
        queryRegister.registerDownloadQueries(chatId, bookId, response, bot);
    }
}
