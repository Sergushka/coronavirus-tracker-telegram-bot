package org.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.bots.CoronaBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@Slf4j
public class Main {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {
        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = createLongPollingTelegramBotsApi();
            // Register long polling bots. They work regardless type of TelegramBotsApi we are creating
            telegramBotsApi.registerBot(new CoronaBot());
            log.debug("STARTED");
        } catch (Exception e) {
            log.error(LOGTAG, e);
        }
    }

    /**
     * @return TelegramBotsApi to register the bots.
     * @brief Creates a Telegram Bots Api to use Long Polling (getUpdates) bots.
     */
    private static TelegramBotsApi createLongPollingTelegramBotsApi() {
        return new TelegramBotsApi();
    }
}
