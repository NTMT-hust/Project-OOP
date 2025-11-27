package com.humanitarian.logistics.test; // <--- THIS IS CRITICAL

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.humanitarian.logistics.model.SocialPost;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReadJson {
    public static void main(String[] args) {
        System.out.println("--- Reading JSON File ---");

        // 1. Setup Gson with LocalDateTime Deserializer
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime
                                .parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .create();

        // 2. Define the list type
        Type listType = new TypeToken<List<SocialPost>>() {
        }.getType();

        // 3. Open and Read the file
        try (FileReader reader = new FileReader("data/youtube_posts.json")) {
            List<SocialPost> data = gson.fromJson(reader, listType);

            if (data != null) {
                System.out.println("✓ Successfully loaded " + data.size() + " posts.");
                if (!data.isEmpty()) {
                    System.out.println("Sample content: " + data.get(0).getContent());
                }
            }
        } catch (IOException e) {
            System.err.println("✗ Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}