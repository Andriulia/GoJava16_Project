package com.markin.bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataParser {

    public static List<JsonParser> readFromJson(String jsonPathname) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(jsonPathname), new TypeReference<List<JsonParser>>() {});
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}