package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleHandler implements RequestHandler<LambdaRequest, LambdaResponse> {

    @Override
    public LambdaResponse handleRequest(LambdaRequest r, Context context) {
        HttpClient client = HttpClient.newHttpClient();
        String[] users = {"{\"username\": \"amilesh\"}","{\"username\": \"tim\"}" ,"{\"username\": \"randy\"}"};
        String[] products = {
                "{\"url\": \"http://example.com/p1\", \"category\": \"ELECTRONICS\"}",
                "{\"url\": \"http://example.com/p2\", \"category\": \"BOOKS\"}",
                "{\"url\": \"http://example.com/p3\", \"category\": \"TOYS\"}"
        };
//        StringBuilder r1 = new StringBuilder();
//        r1.append("{")
//                .append("\"productId\": 1,")
//                .append("\"rating\": 2,")
//                .append("\"text\": \"Meh!\"")
//                .append("}");
//        StringBuilder r2 = new StringBuilder();
//        r2.append("{")
//                .append("\"productId\": 1,")
//                .append("\"rating\": 3,")
//                .append("\"text\": \"Super\"")
//                .append("}");
//        StringBuilder[] reviews = {r1,r2};

        for(int i = 0; i < 3; i++) {
            System.out.println("Processing adding user" + (i + 1));

            String jsonInput = users[i];
            HttpRequest request = null;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI("https://slayscale.azurewebsites.net/api/users"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        for(int i = 0; i < 3; i++) {
            System.out.println("Processing adding product" + (i + 1));

            String jsonInput = products[i];
            HttpRequest request = null;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI("https://slayscale.azurewebsites.net/api/products"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
//        for(int i = 0; i < 2; i++) {
//            System.out.println("Processing adding review" + (i + 1));
//
//            String jsonInput = reviews[i].toString();
//            context.getLogger().log(jsonInput);
//            HttpRequest request = null;
//            try {
//                request = HttpRequest.newBuilder()
//                        .uri(new URI("https://slayscale.azurewebsites.net/api/users/"+(i+1)+"/review"))
//                        .header("Content-Type", "application/json")
//                        .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
//                        .build();
//                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//                System.out.println(response.body());
//            } catch (URISyntaxException e) {
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }


        return new LambdaResponse("Completed");
    }

    private double getSimilarity(User user1, User user2) {
        Set<Product> thisProducts = user1.getReviews().stream()
                .map(Review::getProduct)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Product> otherProducts = user2.getReviews().stream()
                .map(Review::getProduct)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (thisProducts.isEmpty() && otherProducts.isEmpty()) {
            return 0.0d;
        }

        Set<Product> intersection = new HashSet<>(thisProducts);
        intersection.retainAll(otherProducts);

        Set<Product> union = new HashSet<>(thisProducts);
        union.addAll(otherProducts);

        return (double) intersection.size() / union.size();
    }
}
