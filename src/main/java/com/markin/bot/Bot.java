package com.markin.bot;

import lombok.extern.slf4j.Slf4j;

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

import static com.markin.bot.JsonHandler.*;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private int answerPointer;
    private String answerId;

    //    private int menuPointer;

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
//                menuPointer = 0;
                answerPointer = 0;
                sendMessage(message, /*null*/ EmojiParser.parseToUnicode(":wave:"), mainKeys());
                sendMessage(message, "Пора выбирать " + EmojiParser.parseToUnicode(":blush: :point_down:"),
                        inLineKeyboard("00-languages"));
                break;
            case "/Назад":
//                menuPointer--;
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
//                menuPointer++;
                getLanguageCategories(incomeMessage, incomeCallback);
                break;
            case "categories":
//                menuPointer++;
                getCategoryQuestion(incomeMessage, incomeCallback);
                break;
            case "answer":
                getAnswer(incomeMessage, incomeCallback);
                break;
            case "next":
                answerPointer++;
                getCategoryQuestion(incomeMessage, incomeCallback);
                break;
        }
    }

    private void getLanguageCategories(Message incomeMessage, String incomeCallback) {
        String[] callback = incomeCallback.split("-");
        try {
            File currentCategories = new File(categories(callback[2]));
            if (currentCategories.exists()) {
                updateMessage(incomeMessage, "Категории:", inLineKeyboard("00-categories-" + callback[2]));
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException rte) {
            updateMessage(incomeMessage, "⛔", null);
        }
    }

    private void getCategoryQuestion(Message incomeMessage, String incomeCallback) {
        try {
            updateMessage(incomeMessage, getQuestionsForCategory(incomeCallback)
                    .get(answerPointer)
                    .getQuestion(), inLineKeyboard("00-" + getQuestionsForCategory(incomeCallback)
                    .get(answerPointer).getCallback()));
            answerId = getQuestionsForCategory(incomeCallback).get(answerPointer).getId();
        } catch (RuntimeException rte) {
            updateMessage(incomeMessage, "⛔", null);
        }
    }

    private void getAnswer(Message incomeMessage, String incomeCallback) {
        String[] callback = incomeCallback.split("-");
        updateMessage(incomeMessage, getAnswers(callback[2], answerId), null);
    }

    private void sendMessage(Message message, String text, ReplyKeyboard keyboard) {

        SendMessage sendMessage = new SendMessage()
                .enableMarkdown(true)
                .setChatId(message.getChatId().toString())
                .setText(text)
                .setReplyMarkup(keyboard);

        try {

            execute(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void updateMessage(Message message, String text, InlineKeyboardMarkup keyboard) {

        EditMessageText newMessage = new EditMessageText()
                .setChatId(message.getChatId())
                .setMessageId(message.getMessageId())
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

    private InlineKeyboardMarkup inLineKeyboard(String incomeCallback) {

        List<List<InlineKeyboardButton>> inLineKeyboard = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();

        String[] callback = incomeCallback.split("-");

        switch (callback[1]) {
            case "languages":
                List<TypicalJson> languagesJsonData = getLanguages();
                for (TypicalJson data : languagesJsonData) {
                    InlineKeyboardButton button = new InlineKeyboardButton()
                            .setText(data.getName())
                            .setCallbackData(data.getId() + "-" + data.getCallback());
                    if (Integer.parseInt(data.getId()) <= languagesJsonData.size() / 2) {
                        firstRow.add(button);
                    } else secondRow.add(button);
                }
                break;
            case "categories":
                List<TypicalJson> categoriesJsonData = getCategories(callback[2]);
                for (TypicalJson data : categoriesJsonData) {
                    InlineKeyboardButton button = new InlineKeyboardButton()
                            .setText(data.getName())
                            .setCallbackData(data.getId() + "-" + data.getCallback());
                    if (Integer.parseInt(data.getId()) <= categoriesJsonData.size() / 2) {
                        firstRow.add(button);
                    } else secondRow.add(button);
                }
                break;
            case "questions":
                InlineKeyboardButton answerButton = new InlineKeyboardButton()
                        .setText("Ответ")
                        .setCallbackData(answerPointer + "-answer-" + callback[2]);
                firstRow.add(answerButton);
                InlineKeyboardButton nextButton = new InlineKeyboardButton()
                        .setText(EmojiParser.parseToUnicode(":arrow_right:"))
                        .setCallbackData(answerPointer + "-next-" + callback[2] + "-"
                                + callback[3]);
                firstRow.add(nextButton);

//                    InlineKeyboardButton topButton = new InlineKeyboardButton().setText("...")
//                            .setCallbackData(...);
//                    secondRow.add(topButton);
//                    InlineKeyboardButton mightButton = new InlineKeyboardButton().setText("...")
//                            .setCallbackData(...);
//                    secondRow.add(mightButton);
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