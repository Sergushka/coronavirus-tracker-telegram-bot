package org.telegram.bots;

import lombok.extern.slf4j.Slf4j;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.telegram.client.HttpRequest;
import org.telegram.configs.BotConfig;
import org.telegram.configs.Commands;
import org.telegram.configs.Emoji;
import org.telegram.dto.LocationData;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

@Slf4j
public class CoronaBot extends TelegramLongPollingBot {
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasText() || message.hasLocation()) {
                    log.debug(message.getText() + " " + message.getFrom().getUserName());
                    handleIncomingMessage(message);
                }
            }
        } catch (Exception e) {
            log.error("Can't handle message", e);
        }
    }

    public String getBotUsername() {
        return BotConfig.BOT_USERNAME;
    }

    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    private void handleIncomingMessage(Message message) throws TelegramApiException, IOException {
        if (message.hasText()) {
            if (message.getText().startsWith("Te") || message.getText().startsWith("te") || message.getText().startsWith("Те")) {
                execute(sendBasicMonospacedTextMessage(message.getChatId(), "Ты лошара " + Emoji.WAVING_HAND_SIGN.toString()));
                return;
            }
            if (isCommand(message.getText())) {
                execute(sendBasicMonospacedTextMessage(message.getChatId(), "Enter country name or type all"));
                return;
            } else if (message.getText().startsWith(Commands.STOPCOMMAND)) {
                sendHideKeyboard(message.getFrom().getId(), message.getChatId(), message.getMessageId());
                return;
            }
        }
        String responseMessageText;

        try {
            responseMessageText = generateHTMLTable(getCoronaVirusCases(message.getText()));
        } catch (NotFound notFound) {
            responseMessageText = message.getText() + " is not found";
        }
        SendMessage sendMessage = sendBasicMonospacedTextMessage(message.getChatId(), responseMessageText);

        execute(sendMessage);
    }

    private void sendHideKeyboard(Integer userId, Long chatId, Integer messageId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setText(Emoji.WAVING_HAND_SIGN.toString());

        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setSelective(true);
        sendMessage.setReplyMarkup(replyKeyboardRemove);

        execute(sendMessage);
    }

    private static boolean isCommand(String text) {
        return text.startsWith("/");
    }

    private static SendMessage sendChooseOptionMessage(Long chatId, Integer messageId, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setText("chooseOption");

        return sendMessage;
    }

    private SendMessage sendBasicMonospacedTextMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setParseMode("MARKDOWN");
        sendMessage.setText(String.format("`%s`", text));

        return sendMessage;
    }

    private List<LocationData> getCoronaVirusCases(String country) throws IOException, NotFound {
        HttpRequest httpRequest = new HttpRequest();
        return httpRequest.getLocationsData(country);
    }

    private String generateHTMLTable(List<LocationData> locations) {
        StringBuilder builder = new StringBuilder();
        builder.append("Country                Total   Today\n");

        for (LocationData location : locations) {
            String country = addSpacesUntilSpecifiedLength(location.getCountry(), 23);
            builder.append(country);
            String totalCases = addSpacesUntilSpecifiedLength(String.valueOf(location.getTotalCases()), 8);
            builder.append(totalCases);
            builder.append(location.getDifferenceWithYesterday());
            builder.append("\n");
        }
        return builder.toString();
    }

    private String addSpacesUntilSpecifiedLength(String requestText, int size) {
        StringBuilder requestTextBuilder = new StringBuilder(requestText);
        while (requestTextBuilder.length() < size) {
            requestTextBuilder.append(" ");
        }
        return requestTextBuilder.toString();
    }
}
