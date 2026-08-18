package com.solution.technicalchallenge.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ScrapingDocumentClient {

    private static final String BASE_URL = "https://automationexercise.com/product_details/";

    public Document fetchProductDocument(Integer externalId) throws IOException {
        return Jsoup.connect(BASE_URL + externalId)
                .userAgent("Mozilla/5.0")
                .timeout(10_000)
                .get();
    }
}
