package com.dreamfoxick.telegrambot.services.searcher.impl;

import com.dreamfoxick.telegrambot.services.downloader.html.HtmlDownloader;
import com.dreamfoxick.telegrambot.services.enums.URLConstant;
import com.dreamfoxick.telegrambot.services.searcher.Searcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static com.dreamfoxick.telegrambot.services.enums.RegEx.BOOK_LINK;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public abstract class AbstractSearcher implements Searcher {
    protected final String BOOK_HREF_SELECTOR = "[href~=[/b/|/a/][\\d]+]";
    protected final String AUTHOR_HREF_SELECTOR = "[href~=/a/[\\d]+]";

    protected final String MAIN_CONTAINER_SELECTOR = "div#main-wrapper";

    private final HtmlDownloader htmlDownloader;

    protected static boolean isBookLink(Element e) {
        return e.attr("href").matches(BOOK_LINK.getRegEx());
    }

    @Override
    public Map<String, String[]> search(String param,
                                        URLConstant URLType) throws IOException, IllegalArgumentException {
        // создаю url для запроса
        val requestURL = this.buildRequestUrl(param);
        log.debug(format("Created request url: %s", requestURL));

        // скачиваю html документ
        val htmlDocument = this.getHTMLDocumentFromUrl(requestURL);
        log.debug(format("Document downloaded: %s", requestURL));

        // выбираю нужную часть страницы для парсинга
        val htmlElements = this.selectHTMLSection(htmlDocument);
        log.debug(format("Necessary part pulled out: \n%s\n",
                htmlElements.stream()
                        .map(Element::toString)
                        .collect(joining("\n"))));

        // парсинг и подготовка результата
        val result = this.getResult(htmlElements, param);
        log.debug(format("Result constructed: \n%s\n",
                result.entrySet()
                        .stream()
                        .map(e -> e.getKey() + " : " + Arrays.toString(e.getValue()))
                        .collect(joining("\n"))));

        return result;
    }

    protected abstract String buildRequestUrl(String param);

    protected abstract Elements selectHTMLSection(Document htmlDocument);

    protected abstract Map<String, String[]> getResult(Elements elements,
                                                       String param) throws IllegalArgumentException;

    private Document getHTMLDocumentFromUrl(String URL) throws IOException {
        val http = htmlDownloader.download(URL);
        return Jsoup.parse(http);
    }
}
