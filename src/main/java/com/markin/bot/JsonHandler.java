package com.markin.bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonHandler {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    public final static String LANGUAGES = "src\\main\\resources\\languages.json";

    public static String categories(String language) {
        return "src\\main\\resources\\ThemesCategories\\" + language + "Categories.json";
    }

    public static String questions(String language) {
        return "src\\main\\resources\\Questions\\" + language + "Questions.json";
    }

    public static List<TypicalJson> getLanguages() {
        try {
            return MAPPER.readValue(new File(LANGUAGES), new TypeReference<List<TypicalJson>>() {
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<TypicalJson> getCategories(String language) {
        try {
            return MAPPER.readValue(new File(categories(language)), new TypeReference<List<TypicalJson>>() {
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String getAnswers(String language, String id) {
        try {
            return MAPPER.readValue(new File(questions(language)), new TypeReference<List<QuestionsJson>>() {
            })
                    .get(Integer.parseInt(id) - 1).getAnswer();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<QuestionsJson> getQuestionsForCategory(String incomeCallback) {
        String[] callback = incomeCallback.split("-");
        try {
            List<QuestionsJson> questions = MAPPER.readValue(new File(questions(callback[2])), new TypeReference<List<QuestionsJson>>() {
            });
            questions.removeIf(q -> !q.getCallback().split("-")[2].equals(callback[3]));

            return questions;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}