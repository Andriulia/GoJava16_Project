package com.markin.bot;

import lombok.Data;

@Data
public class QuestionsJson {

    private String id;
    private String callback;
    private String question;
    private String answer;

}