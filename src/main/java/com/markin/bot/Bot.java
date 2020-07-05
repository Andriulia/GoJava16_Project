package com.markin.bot;

//import lombok.extern.slf4j.Slf4j;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

//@Slf4j
public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().equals("/start")) {
                sendMsg(message, EmojiParser.parseToUnicode(":wave:"), mainKeys());
                sendMsg(message, "Пора выбирать " + EmojiParser.parseToUnicode(":blush: :point_down:"),
                        languagesInLine());
            } else if (update.getMessage().getText().equals("/Назад")) {
                sendMsg(message, EmojiParser.parseToUnicode(":leftwards_arrow_with_hook:"), null);
            } else if (update.getMessage().getText().equals("/STOP")) {
                sendMsg(message, "\uD83D\uDED1", null);
//                BotSession session = ApiContext.getInstance(BotSession.class);
//                session.setToken(getBotToken());
//                session.setOptions(getOptions());
//                session.stop();
            }

        } else if (update.hasCallbackQuery()) {
        }

    }

    private void sendMsg(Message message, String text, ReplyKeyboard keyboard) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
//        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

        try {

            sendMessage.setReplyMarkup(keyboard);
            execute(sendMessage);

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

        keyboardFirstRow.add(new KeyboardButton("/Назад"));
        keyboardSecondRow.add(new KeyboardButton("/STOP"));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup languagesInLine() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        List<JsonParser> jsonData = DataParser.readFromJson("src\\main\\resources\\languages.json");

        for (JsonParser data : jsonData) {
            InlineKeyboardButton button = new InlineKeyboardButton().setText(data.getName()).setCallbackData(data.getId());
            if (Integer.parseInt(data.getId()) <= jsonData.size()/2){
                firstRow.add(button);
            } else secondRow.add(button);
        }
        rowsInline.add(firstRow);
        rowsInline.add(secondRow);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(rowsInline);

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