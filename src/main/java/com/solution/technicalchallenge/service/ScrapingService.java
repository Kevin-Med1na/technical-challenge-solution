package com.solution.technicalchallenge.service;

import com.solution.technicalchallenge.dto.extraction.ScrapedProductData;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;

@Service
public class ScrapingService {

    private static final String BASE_URL = "https://automationexercise.com/product_details/";

    public ScrapedProductData scrape(Integer externalId) throws IOException {
        Document doc = Jsoup.connect(BASE_URL + externalId)
                .userAgent("Mozilla/5.0")
                .timeout(10_000)
                .get();

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
