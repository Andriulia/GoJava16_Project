package com.markin.bot;

import lombok.Data;

@Data
public class QuestionsJson extends DefaultJson {

    String category;
    String question;

}