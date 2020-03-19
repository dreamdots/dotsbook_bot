package com.dreamfoxick.telegrambot.services.searcher.impl.page;

import com.dreamfoxick.telegrambot.services.downloader.html.HtmlDownloader;
import com.dreamfoxick.telegrambot.services.enums.RegEx;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.dreamfoxick.telegrambot.services.enums.RegEx.BOOK_AND_ANY_FORMAT_LINK;
import static java.lang.String.format;

@Slf4j
@Component
public class BookSearcher extends AbstractPageSearcher {
    private final static String DJVI_TEXT = "(скачать djvu)";
    private final static String PDF_TEXT = "(скачать pdf)";
    private final static String DOC_TEXT = "(скачать doc)";
    private final static String RTF_TEXT = "(скачать rtf)";

    @Autowired
    public BookSearcher(HtmlDownloader htmlDownloader) {
        super(htmlDownloader);
    }

    private static boolean isDownloadLink(Element e) {
        return e.attr("href").matches(BOOK_AND_ANY_FORMAT_LINK.getRegEx());
    }

    private void putFormats(String[] result,
                            Element e) {
        val format = e.attr("href");
        log.trace(format("Download format: %s", format));
        if (format.matches(RegEx.FB2_FORMAT_LINK.getRegEx()))
            result[0] = format;
        else if (format.matches((RegEx.EPUB_FORMAT_LINK.getRegEx())))
            result[1] = format;
        else if (format.matches((RegEx.MOBI_FORMAT_LINK.getRegEx())))
            result[2] = format;
        else if (format.matches(RegEx.PDF_DJVI_DOC_FORMAT_LINK.getRegEx())) {
            val text = e.text();
            switch (text) {
                case (PDF_TEXT): {
                    result[3] = format;
                    break;
                }
                case (DJVI_TEXT): {
                    result[4] = format;
                    break;
                }
                case (DOC_TEXT): {
                    result[5] = format;
                    break;
                }
                case (RTF_TEXT): {
                    result[6] = format;
                    break;
                }
            }
        }
    }

    @Override
    protected Map<String, String[]> getResult(Elements elements,
                                              String bookLink) throws IllegalArgumentException {
        if (elements.isEmpty()) throw new IllegalArgumentException(format("По ссылке: %s нет результата",
                bookLink.replaceAll(RegEx.BOOK_LINK_SECOND_SLASH.getRegEx(), "")));
        val result = new HashMap<String, String[]>();
        result.put(bookLink, new String[7]);
        log.trace(format("Book link: %s", bookLink));

        elements.stream()
                .filter(BookSearcher::isDownloadLink)
                .forEach(e -> this.putFormats(result.get(bookLink), e));

        return result;
    }
}
