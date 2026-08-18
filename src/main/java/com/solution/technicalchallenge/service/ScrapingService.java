package com.solution.technicalchallenge.service;

import com.solution.technicalchallenge.dto.extraction.ScrapedProductData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ScrapingService {

    private final ScrapingDocumentClient scrapingDocumentClient;

    public ScrapedProductData scrape(Integer externalId) throws IOException {
        Document doc = scrapingDocumentClient.fetchProductDocument(externalId);

        String name = doc.select(".product-information h2").text();

        if (name == null || name.isBlank()) {
            throw new RuntimeException("Product " + externalId + " has no content available");
        }

        Element priceElement = doc.select(".product-information span span").first();
        String price = priceElement != null ? priceElement.text() : null;

        String category    = extractField(doc, "Category:");
        String availability = extractField(doc, "Availability:");
        String condition   = extractField(doc, "Condition:");
        String brand       = extractField(doc, "Brand:");

        return new ScrapedProductData(name, price, category, availability, condition, brand);
    }
    //metodo privado que se encarga de extraer la informacion especificamente de los campos que estan en etiquetas <p>
    //mas que todo para abstraccion
    private String extractField(Document doc, String label) {
        return doc.select(".product-information p").stream()
                .filter(p -> p.text().contains(label))
                .map(p -> p.text().replace(label, "").trim())
                .findFirst()
                .orElse(null);
    }
}
