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

import java.io.File;
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
        List<JsonParser> jsonData = DataParser.readFromJson("src\\main\\resources\\languages.json");
        String[] callback = incomeCallback.split("-");
        String categories = "src\\main\\resources\\ThemesCategories\\" + callback[2] + "Categories.json";
        File f = new File(categories);
        for (JsonParser data : jsonData) {
//            if (callback[0].equals(data.getId())) {
                switch (callback[1]) {
                    case "languages":
                        if (f.exists() && !f.isDirectory()) {
                            updateMessage(incomeMessage, "Категории " + data.getName() + ":", inLineKeyboard(categories));
                        } else {
                            updateMessage(incomeMessage, "⛔", null);
                        }
                }
//            }
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

    private InlineKeyboardMarkup inLineKeyboard(String pathToJson) {
        List<List<InlineKeyboardButton>> inLineKeyboard = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        List<JsonParser> jsonData = DataParser.readFromJson(pathToJson);
        if (pathToJson.isEmpty()) {
            return null;
        }

        for (JsonParser data : jsonData) {
            InlineKeyboardButton button = new InlineKeyboardButton().setText(data.getName()).setCallbackData(data.getId() + "-" + data.getCallback());
            if (Integer.parseInt(data.getId()) <= jsonData.size() / 2) {
                firstRow.add(button);
            } else secondRow.add(button);
        }
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