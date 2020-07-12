package com.markin.bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonHandler {

    static ObjectMapper mapper = new ObjectMapper();

    static String languages = "src\\main\\resources\\languages.json";

    public static String categories(String language) {
        return "src\\main\\resources\\ThemesCategories\\" + language + "Categories.json";
    }

    public static String questions(String language) {
        return "src\\main\\resources\\Questions\\" + language + "Questions.json";
    }

    public static List<LanguagesJson> getLanguages() {
        try {
            return mapper.readValue(new File(languages), new TypeReference<List<LanguagesJson>>() {});
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<LanguagesJson> getCategories(String language) {
        try {
            return mapper.readValue(new File(categories(language)), new TypeReference<List<LanguagesJson>>() {});
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<QuestionsJson> getQuestionsForCategory(String language, String category) {
        try {
            List<QuestionsJson> questions = mapper.readValue(new File(questions(language)), new TypeReference<List<QuestionsJson>>() {});
            questions.removeIf(q -> !q.getCategory().equals(category));
            return questions;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}