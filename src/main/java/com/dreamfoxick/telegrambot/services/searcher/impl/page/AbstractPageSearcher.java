package com.dreamfoxick.telegrambot.services.searcher.impl.page;

import com.dreamfoxick.telegrambot.services.downloader.HtmlDownloader;
import com.dreamfoxick.telegrambot.services.searcher.impl.AbstractSearcher;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import static com.dreamfoxick.telegrambot.services.enums.URLConstant.BASE_SITE_URL;
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
        return format("%s%s", BASE_SITE_URL.getURL(), link);
    }
}
