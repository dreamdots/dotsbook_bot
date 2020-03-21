package com.dreamfoxick.telegrambot.services.searcher.impl.page;

import com.dreamfoxick.telegrambot.services.downloader.html.HtmlDownloader;
import com.dreamfoxick.telegrambot.services.enums.RegEx;
import com.dreamfoxick.telegrambot.services.searcher.impl.AbstractSearcher;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import static com.dreamfoxick.telegrambot.services.enums.URLConstant.SITE_URL;
import static java.lang.String.format;

public abstract class AbstractPageSearcher extends AbstractSearcher {

    public AbstractPageSearcher(HtmlDownloader htmlDownloader) {
        super(htmlDownloader);
    }

    @Override
    protected Elements selectHTMLSection(Document htmlDocument) {
        return htmlDocument
                .select("div#main")
                .select(BOOK_HREF_SELECTOR);
    }

    @Override
    protected String buildRequestUrl(String link) {
        if (link.matches(RegEx.BOOK_LINK.getRegEx()) || link.matches(RegEx.AUTHOR_LINK.getRegEx())) {
            return format("%s%s", SITE_URL.getURL(), link);
        }
        throw new IllegalArgumentException("Неверный формат ссылки, попробуйте начать поиск заного");
    }
}
