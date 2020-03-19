package com.dreamfoxick.telegrambot.services.searcher.impl.page;

import com.dreamfoxick.telegrambot.services.downloader.html.HtmlDownloader;
import com.dreamfoxick.telegrambot.services.enums.RegEx;
import com.dreamfoxick.telegrambot.services.searcher.impl.AbstractSearcher;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Component
public class AuthorSearcher extends AbstractPageSearcher {

    @Autowired
    public AuthorSearcher(HtmlDownloader htmlDownloader) {
        super(htmlDownloader);
    }

    private static String[] getBookName(Element e) {
        val bookName = e.text();
        log.trace(format("Book name: %s", bookName));
        val infoArray = new String[1];
        infoArray[0] = bookName;
        return infoArray;
    }

    private static String getBookLink(Element e) {
        val link = e.attr("href");
        log.trace(format("Book link: %s", link));
        return link;
    }

    private static String[] mergeF(String[] v1,
                                   String[] v2) {
        return v1;
    }

    @Override
    protected Map<String, String[]> getResult(Elements elements,
                                              String authorLink) throws IllegalArgumentException {
        if (elements.isEmpty()) throw new IllegalArgumentException(format("По ссылке: %s нет результата",
                authorLink.replaceAll(RegEx.AUTHOR_LINK_SECOND_SLASH.getRegEx(), "")));
        return elements
                .stream()
                .filter(AbstractSearcher::isBookLink)
                .collect(Collectors.toMap(
                        AuthorSearcher::getBookLink,
                        AuthorSearcher::getBookName,
                        AuthorSearcher::mergeF));

    }
}
