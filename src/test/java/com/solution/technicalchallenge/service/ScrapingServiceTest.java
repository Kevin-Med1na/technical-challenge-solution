package com.solution.technicalchallenge.service;

import com.solution.technicalchallenge.dto.extraction.ScrapedProductData;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScrapingServiceTest {

    @Mock
    private ScrapingDocumentClient scrapingDocumentClient;

    @InjectMocks
    private ScrapingService scrapingService;

    @Test
    void scrapeParsesProductInformationFromMockedHtml() throws IOException {
        when(scrapingDocumentClient.fetchProductDocument(1)).thenReturn(Jsoup.parse("""
                <html>
                    <body>
                        <div class="product-information">
                            <h2>Blue Top</h2>
                            <span><span>Rs. 500</span></span>
                            <p><b>Category:</b> Women &gt; Tops</p>
                            <p><b>Availability:</b> In Stock</p>
                            <p><b>Condition:</b> New</p>
                            <p><b>Brand:</b> Polo</p>
                        </div>
                    </body>
                </html>
                """));

        ScrapedProductData result = scrapingService.scrape(1);

        assertAll(
                () -> assertEquals("Blue Top", result.getName()),
                () -> assertEquals("Rs. 500", result.getPrice()),
                () -> assertEquals("Women > Tops", result.getCategory()),
                () -> assertEquals("In Stock", result.getAvailability()),
                () -> assertEquals("New", result.getCondition()),
                () -> assertEquals("Polo", result.getBrand())
        );
        verify(scrapingDocumentClient).fetchProductDocument(1);
    }

    @Test
    void scrapeReturnsNullWhenOptionalFieldsAreMissing() throws IOException {
        when(scrapingDocumentClient.fetchProductDocument(2)).thenReturn(Jsoup.parse("""
                <html>
                    <body>
                        <div class="product-information">
                            <h2>Minimal Product</h2>
                        </div>
                    </body>
                </html>
                """));

        ScrapedProductData result = scrapingService.scrape(2);

        assertAll(
                () -> assertEquals("Minimal Product", result.getName()),
                () -> assertNull(result.getPrice()),
                () -> assertNull(result.getCategory()),
                () -> assertNull(result.getAvailability()),
                () -> assertNull(result.getCondition()),
                () -> assertNull(result.getBrand())
        );
    }

    @Test
    void scrapeThrowsWhenProductHasNoContent() throws IOException {
        when(scrapingDocumentClient.fetchProductDocument(99)).thenReturn(Jsoup.parse("""
                <html>
                    <body>
                        <div class="product-information">
                            <p><b>Category:</b> Unknown</p>
                        </div>
                    </body>
                </html>
                """));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> scrapingService.scrape(99));

        assertEquals("Product 99 has no content available", exception.getMessage());
    }
}
