package com.dreamfoxick.telegrambot.services.searcher.impl.ul;

import com.dreamfoxick.telegrambot.services.downloader.HtmlDownloader;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

import static com.dreamfoxick.telegrambot.services.enums.URLConstant.FIND_AUTHORS_URL;
import static java.lang.String.format;
import static java.util.stream.Collectors.toConcurrentMap;

@Slf4j
@Component
public class AuthorsSearcher extends AbstractULSearcher {

    @Autowired
    public AuthorsSearcher(HtmlDownloader htmlDownloader) {
        super(htmlDownloader);
    }

    private static void log(Pair<String, String[]> p) {
        log.trace(String.format("Profile: %s - %s", p.getLeft(), Arrays.toString(p.getRight())));
    }

    private static Pair<String, String[]> loadAuthorProfile(Element e) {
        val authorLink = e.attr("href");
        val authorName = e.text();
        return new ImmutablePair<>(authorLink, new String[]{authorName});
    }

    @Override
    protected Map<String, String[]> getResult(Elements elements,
                                              String authorName) throws IllegalArgumentException {
        if (elements.isEmpty())
            throw new IllegalArgumentException(format("Авторы по запросу: %s не найдены", authorName));
        return elements
                .parallelStream()
                .map(AuthorsSearcher::loadAuthorProfile)
                .filter(AbstractULSearcher::pairIsNotNull)
                .peek(AuthorsSearcher::log)
                .collect(toConcurrentMap(Pair::getLeft, Pair::getRight));
    }

    @Override
    protected Elements selectHTMLSection(Document htmlDocument) {
        return htmlDocument
                .select(MAIN_CONTAINER_SELECTOR)
                .select(AUTHOR_HREF_SELECTOR);
    }

    @Override
    protected String buildRequestUrl(String authorName) {
        return FIND_AUTHORS_URL.getURL().replace(
                "PARAM",
                URLEncoder.encode(authorName, Charset.defaultCharset()));
    }
}
