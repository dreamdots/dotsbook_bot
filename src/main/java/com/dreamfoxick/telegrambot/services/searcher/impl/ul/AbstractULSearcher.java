package com.dreamfoxick.telegrambot.services.searcher.impl.ul;

import com.dreamfoxick.telegrambot.services.downloader.html.HtmlDownloader;
import com.dreamfoxick.telegrambot.services.searcher.impl.AbstractSearcher;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

public abstract class AbstractULSearcher extends AbstractSearcher {

    public AbstractULSearcher(HtmlDownloader htmlDownloader) {
        super(htmlDownloader);
    }

    protected static boolean pairIsNotNull(Pair<String, String[]> profile) {
        val link = profile.getLeft();
        val info = profile.getRight();
        return link != null && !link.isEmpty()
                && info != null && Arrays.stream(info).allMatch(v -> v != null && !v.isEmpty());
    }
}
