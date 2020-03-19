package com.dreamfoxick.telegrambot.services.searcher.impl.ul;

import com.dreamfoxick.telegrambot.services.downloader.html.HtmlDownloader;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.dreamfoxick.telegrambot.services.enums.URLConstant.FIND_BOOKS_URL;
import static java.lang.Math.min;
import static java.lang.String.format;

@Slf4j
@Component
public class BooksSearcher extends AbstractULSearcher {
    @Autowired
    public BooksSearcher(HtmlDownloader htmlDownloader) {
        super(htmlDownloader);
    }

    @Override
    protected Map<String, String[]> getResult(Elements elements,
                                              String bookName) throws IllegalArgumentException {
        if (elements.isEmpty()) throw new IllegalArgumentException(format("Книги по запросу: %s не найдены", bookName));
        val authorNames = new ArrayList<String>();
        val bookLinks = new ArrayList<String>();
        val bookNames = new ArrayList<String>();

        for (Element e : elements) {
            if (isBookLink(e)) {
                var y = e.attr("href");
                bookLinks.add(y);
                log.trace(format("Book link: %s", y));
                y = e.text();
                bookNames.add(y);
                log.trace(format("Book name: %s", y));
            } else {
                val y = e.text();
                authorNames.add(y);
                log.trace(format("Author name: %s", y));
            }
        }

        val result = new ConcurrentHashMap<String, String[]>();
        for (int i = 0; i < min(bookLinks.size(), authorNames.size()); i++) {
            result.put(
                    bookLinks.get(i),
                    new String[]{bookNames.get(i), authorNames.get(i)});
        }

        return result;
    }

    @Override
    protected Elements selectHTMLSection(Document htmlDocument) {
        return htmlDocument
                .select(MAIN_CONTAINER_SELECTOR)
                .select(BOOK_HREF_SELECTOR);
    }

    @Override
    protected String buildRequestUrl(String bookName) {
        return FIND_BOOKS_URL.getURL().replace(
                "PARAM",
                URLEncoder.encode(bookName, Charset.defaultCharset()));
    }
}
