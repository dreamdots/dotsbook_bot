package com.dreamfoxick.telegrambot.services.message.processor.impl.ul;

import com.dreamfoxick.telegrambot.services.enums.Pattern;
import com.dreamfoxick.telegrambot.services.message.creator.KeyboardCreator;
import com.dreamfoxick.telegrambot.services.register.QueryRegister;
import com.dreamfoxick.telegrambot.services.searcher.SearcherInvoker;
import com.dreamfoxick.telegrambot.services.statecontroller.State;
import com.dreamfoxick.telegrambot.services.statecontroller.StateController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.dreamfoxick.telegrambot.services.enums.RegEx.BOOK_LINK_SECOND_SLASH;
import static com.dreamfoxick.telegrambot.services.statecontroller.State.FIND_BOOK_STATE;
import static java.lang.String.format;
import static java.util.stream.Collectors.toUnmodifiableList;

@Component
public class BooksCommandProcessor extends AbstractULCommandProcessor {
    private final static String HEADING_MESSAGE = "*Если в списке нет нужной вам книги, попробуйте ввести полное название*";

    private final StateController<Long, State> stateController;
    private final SearcherInvoker searcherInvoker;

    @Autowired
    public BooksCommandProcessor(StateController<Long, State> stateController,
                                 QueryRegister queryRegister,
                                 SearcherInvoker searcherInvoker) {
        super(queryRegister);
        this.stateController = stateController;
        this.searcherInvoker = searcherInvoker;
    }

    @Override
    protected Map<String, String[]> getResult(String bookName) throws IOException {
        return searcherInvoker.searchBooks(bookName);
    }

    @Override
    protected void updateState(long chatId) {
        stateController.updateIfPresent(chatId, FIND_BOOK_STATE);
    }

    @Override
    protected String headingMessage() {
        return HEADING_MESSAGE;
    }

    @Override
    protected ReplyKeyboardMarkup headingKeyboard() {
        return KeyboardCreator.backKeyboard();
    }

    @Override
    protected List<String> getLinks(Map<String, String[]> result) {
        return result
                .keySet()
                .parallelStream()
                .map(link -> format(Pattern.RESPONSE_LINK.getPattern(), link.replaceAll(BOOK_LINK_SECOND_SLASH.getRegEx(), "")))
                .collect(toUnmodifiableList());
    }

    /**
     * profileArray[0] - название книги
     * profileArray[1] - автор
     */
    @Override
    protected List<String> getProfiles(Map<String, String[]> result) {
        return result
                .values()
                .parallelStream()
                .map(profileArray -> format(Pattern.RESPONSE_BOOK_NAME_AND_AUTHOR_FULL_NAME.getPattern(), profileArray[0], profileArray[1]))
                .collect(toUnmodifiableList());
    }
}
