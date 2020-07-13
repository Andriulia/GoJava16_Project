package com.markin.bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonHandler {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private final static String LANGUAGES = "src\\main\\resources\\languages.json";

    public static String categories(String language) {
        return "src\\main\\resources\\ThemesCategories\\" + language + "Categories.json";
    }

    public static String questions(String language) {
        return "src\\main\\resources\\Questions\\" + language + "Questions.json";
    }

    public static List<TypicalJson> getLanguages() {
        try {
            return MAPPER.readValue(new File(LANGUAGES), new TypeReference<List<TypicalJson>>() {});
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<TypicalJson> getCategories(String language) {
        try {
            return MAPPER.readValue(new File(categories(language)), new TypeReference<List<TypicalJson>>() {});
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String getAnswers(String language, String id) {
        try {
            return MAPPER.readValue(new File(questions(language)), new TypeReference<List<QuestionsJson>>() {}).get(Integer.parseInt(id)).getAnswer();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<QuestionsJson> getQuestionsForCategory(String language, String category) {
        try {
            List<QuestionsJson> questions = MAPPER.readValue(new File(questions(language)), new TypeReference<List<QuestionsJson>>() {});
            questions.removeIf(q -> !q.getCategory().equals(category));
            return questions;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}