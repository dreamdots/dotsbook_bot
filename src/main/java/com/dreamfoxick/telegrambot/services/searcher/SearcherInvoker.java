package com.dreamfoxick.telegrambot.services.searcher;

import com.dreamfoxick.telegrambot.services.enums.URLConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static com.dreamfoxick.telegrambot.services.enums.URLConstant.*;
import static java.lang.String.format;

@Slf4j
@Service
public class SearcherInvoker {
    private final Searcher authorPage;
    private final Searcher bookPage;
    private final Searcher authorUL;
    private final Searcher bookUL;

    public SearcherInvoker(@Qualifier("authorSearcher") Searcher authorPage,
                           @Qualifier("bookSearcher") Searcher bookPage,
                           @Qualifier("authorsSearcher") Searcher authorUL,
                           @Qualifier("booksSearcher") Searcher bookUL) {
        this.authorPage = authorPage;
        this.bookPage = bookPage;
        this.authorUL = authorUL;
        this.bookUL = bookUL;
    }

    private static void log(String param,
                            String methodName) {
        log.info(format("Called method: '%s' with param: %s", methodName, param));
    }

    public Map<String, String[]> searchBook(String link) throws IOException, IllegalArgumentException {
        log(link, Thread.currentThread().getStackTrace()[1].getMethodName());
        return bookPage.search(link, URLConstant.SITE_URL);
    }

    public Map<String, String[]> searchAuthor(String link) throws IOException, IllegalArgumentException {
        log(link, Thread.currentThread().getStackTrace()[1].getMethodName());
        return authorPage.search(link, URLConstant.SITE_URL);
    }

    public Map<String, String[]> searchBooks(String bookName) throws IOException, IllegalArgumentException {
        log(bookName, Thread.currentThread().getStackTrace()[1].getMethodName());
        return bookUL.search(bookName, FIND_BOOKS_URL);
    }

    public Map<String, String[]> searchAuthors(String authorName) throws IOException, IllegalArgumentException {
        log(authorName, Thread.currentThread().getStackTrace()[1].getMethodName());
        return authorUL.search(authorName, FIND_AUTHORS_URL);
    }
}
