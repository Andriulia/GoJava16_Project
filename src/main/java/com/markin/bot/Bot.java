package com.markin.bot;

//import lombok.extern.slf4j.Slf4j;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

//@Slf4j
public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            incomeMessageHandler(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            incomeCallbackHandler(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getData());
        }
    }

    private void incomeMessageHandler(Message message) {
        String messageText = message.getText();
        switch (messageText) {
            case "/start":
                sendMessage(message, /*null*/ EmojiParser.parseToUnicode(":wave:"), mainKeys());
                sendMessage(message, "Пора выбирать " + EmojiParser.parseToUnicode(":blush: :point_down:"),
                        inLineKeyboard("src\\main\\resources\\languages.json"));
                break;
            case "/Назад":
                sendMessage(message, EmojiParser.parseToUnicode(":leftwards_arrow_with_hook:"), null);
                break;
            case "/STOP":
                sendMessage(message, "\uD83D\uDED1", null);
//                BotSession session = ApiContext.getInstance(BotSession.class);
//                session.setToken(getBotToken());
//                session.setOptions(getOptions());
//                session.stop();
                break;
        }
    }

    private void incomeCallbackHandler(Message incomeMessage, String incomeCallback) {
        String[] callback = incomeCallback.split("-");
        switch (callback[1]) {
            case "languages":
                getLanguageCategories(incomeMessage, callback[2]);
                break;
                case "categories":
                    getCategoryQuestion(incomeMessage, incomeCallback);
                    break;
        }
    }

    private void getLanguageCategories(Message incomeMessage, String language) {
//        List<LanguagesJson> languagesJson = JsonHandler.getLanguages();
//        for (LanguagesJson data : languagesJson) {
//            if (data.getCallback().contains(language))
                try {
                    updateMessage(incomeMessage, "Категории:"/* + data.getName() + ":"*/, inLineKeyboard(JsonHandler.categories(language)));
                } catch (RuntimeException rte) {
                    updateMessage(incomeMessage, "⛔", null);
                }
//        }
    }

    private void getCategoryQuestion(Message incomeMessage, String incomeCallback) {
        String[] callback = incomeCallback.split("-");
        try {
            updateMessage(incomeMessage, JsonHandler.getQuestionsForCategory(callback[2], callback[3]).get(0).getQuestion(), null);
        }
        catch (RuntimeException rte) {
            updateMessage(incomeMessage, "⛔", null);
        }
    }

    private void sendMessage(Message message, String text, ReplyKeyboard keyboard) {

        SendMessage sendMessage = new SendMessage()
                .enableMarkdown(true)
                .setChatId(message.getChatId().toString())
                .setText(text)
                .setReplyMarkup(keyboard);
//        sendMessage.setReplyToMessageId(message.getMessageId());

        try {

            execute(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void updateMessage(Message message, String text, InlineKeyboardMarkup keyboard) {

        EditMessageText newMessage = new EditMessageText()
                .setChatId(message.getChatId())
                .setMessageId(Math.toIntExact(message.getMessageId()))
                .setText(text)
                .setReplyMarkup(keyboard);
        try {
            execute(newMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup mainKeys() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        keyboardFirstRow.add("/Назад");
        keyboardSecondRow.add("/STOP");

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return replyKeyboardMarkup;
    }

    InlineKeyboardMarkup inLineKeyboard(String pathToJson) {

        List<List<InlineKeyboardButton>> inLineKeyboard = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();

        String[] jsonPath = pathToJson.split("\\\\");
        String jsonType = jsonPath[jsonPath.length - 1];
        if (pathToJson.isEmpty()) {
            return null;
        } else if (jsonType.contains("languages")) {
            List<LanguagesJson> jsonData = JsonHandler.getLanguages();
            for (LanguagesJson data : jsonData) {
                InlineKeyboardButton button = new InlineKeyboardButton().setText(data.getName()).setCallbackData(data.getId() + "-" + data.getCallback());
                if (Integer.parseInt(data.getId()) <= jsonData.size() / 2) {
                    firstRow.add(button);
                } else secondRow.add(button);
            }
        } else if (jsonType.contains("Categories")) {
            List<LanguagesJson> jsonData = JsonHandler.getCategories(jsonType.substring(0, jsonType.length() - 15));
            for (LanguagesJson data : jsonData) {
                InlineKeyboardButton button = new InlineKeyboardButton().setText(data.getName()).setCallbackData(data.getId() + "-" + data.getCallback());
                if (Integer.parseInt(data.getId()) <= jsonData.size() / 2) {
                    firstRow.add(button);
                } else secondRow.add(button);
            }
        }
        /*} else if (jsonType.equals("Questions")) {
            for (QuestionsJson data : jsonData) {
                InlineKeyboardButton button = new InlineKeyboardButton().setText(data.getCategory()).setCallbackData(data.getId() + "-" + data.getCategory());
                if (Integer.parseInt(data.getId()) <= jsonData.size() / 2) {
                    firstRow.add(button);
                } else secondRow.add(button);
            }
        }*/

        inLineKeyboard.add(firstRow);
        inLineKeyboard.add(secondRow);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(inLineKeyboard);

        return markupKeyboard;
    }

    @Override
    public String getBotUsername() {
        return "GoJava_ProjectBot";
    }

    @Override
    public String getBotToken() {
        return "1270098389:AAEjEuidZ3_kyHzdLi7lEjf11fPY_vbTMuk";
    }
}