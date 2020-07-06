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
            Message message = update.getMessage();
            if (update.getMessage().getText().equals("/start")) {
                sendMessage(message, /*null*/ EmojiParser.parseToUnicode(":wave:"), mainKeys());
                sendMessage(message, "Пора выбирать " + EmojiParser.parseToUnicode(":blush: :point_down:"),
                        inLineKeyboard("languages"));
            } else if (update.getMessage().getText().equals("/Назад")) {
                sendMessage(message, EmojiParser.parseToUnicode(":leftwards_arrow_with_hook:"), null);
            } else if (update.getMessage().getText().equals("/STOP")) {
                sendMessage(message, "\uD83D\uDED1", null);
//                BotSession session = ApiContext.getInstance(BotSession.class);
//                session.setToken(getBotToken());
//                session.setOptions(getOptions());
//                session.stop();
            }

        } else if (update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            if (update.getCallbackQuery().getData().equals("1")) {
                updateMessage(message, "Категории Java:", inLineKeyboard("themes"));
            } else {
                updateMessage(message, "А только Java.  \uD83D\uDE42",
                        inLineKeyboard("languages"));
            }
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

    private void updateMessage(Message message, String text, ReplyKeyboard keyboard) {

        EditMessageText newMessage = new EditMessageText()
                .setChatId(message.getChatId())
                .setMessageId(Math.toIntExact(message.getMessageId()))
                .setText(text)
                .setReplyMarkup((InlineKeyboardMarkup) keyboard);
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

    private InlineKeyboardMarkup inLineKeyboard(String type) {
        List<List<InlineKeyboardButton>> inLineKeyboard = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        String pathToJson = null;
        if (type.equals("languages")) {
            pathToJson = "src\\main\\resources\\languages.json";
        } else if (type.equals("themes")) {
            pathToJson = "src\\main\\resources\\themes.json";
        }
            List<JsonParser> jsonData = DataParser.readFromJson(pathToJson);

        for (JsonParser data : jsonData) {
            InlineKeyboardButton button = new InlineKeyboardButton().setText(data.getName()).setCallbackData(data.getId());
            if (Integer.parseInt(data.getId()) <= jsonData.size()/2){
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