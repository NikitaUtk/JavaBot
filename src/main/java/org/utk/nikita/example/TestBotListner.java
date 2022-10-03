package org.utk.nikita.example;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.utk.nikita.example.entity.Currency;
import org.utk.nikita.example.service.CurrencyModeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class TestBotListner extends TelegramLongPollingBot{

    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();

    @Override
    public String getBotUsername() {
        return "@NuJavaBot";
    }

    @Override
    public String getBotToken() {
        return "5445278331:AAHJ7LPyUQdqFwJgX3cVv2Vi8cL_q19f8qY";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasCallbackQuery()){
            handleCallback(update.getCallbackQuery());
        }else if(update.hasMessage()){
            handleMessage(update.getMessage());
            Message message = update.getMessage();
        }
    }
    @SneakyThrows
    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        Currency newCurrency = Currency.valueOf(param[1]);
        switch (action) {
            case "ORIGINAL":
                currencyModeService.setOriginalCurrency(message.getChatId(), newCurrency);
                break;
            case "TARGET":
                currencyModeService.setTargetCurrency(message.getChatId(), newCurrency);
                break;
        }
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
        for (Currency currency : Currency.values()) {
            buttons.add(
                    Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(originalCurrency, currency))
                                    .callbackData("ORIGINAL:" + currency)
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(targetCurrency, currency))
                                    .callbackData("TARGET:" + currency)
                                    .build()));
        }
        execute(
                EditMessageReplyMarkup.builder()
                        .chatId(message.getChatId().toString())
                        .messageId(message.getMessageId())
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                        .build());
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if(message.hasText() && message.hasEntities()){
           Optional<MessageEntity> commandEntity = message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();

           if(commandEntity.isPresent()){
               String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
               switch (command){
                   case "/set_currency":
                       List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                       Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
                       Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());

                       for (Currency currency : Currency.values()) {
                           buttons.add(
                                   Arrays.asList(
                                           InlineKeyboardButton.builder()
                                                   .text(getCurrencyButton(originalCurrency, currency))
                                                   .callbackData("ORIGINAL:" + currency)
                                                   .build(),
                                           InlineKeyboardButton.builder()
                                                   .text(getCurrencyButton(targetCurrency, currency))
                                                   .callbackData("TARGET:" + currency)
                                                   .build()));
                       }
                       execute(SendMessage.builder()
                               .text("Please chose Original and Target currencies")
                               .chatId(message.getChatId().toString())
                               .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                               .build());
                       return;
               }
           }
        }
    }

    private String getCurrencyButton(Currency saved, Currency current){
        return saved == current ? current + "✅" : current.name();
    }

    @SneakyThrows
    public static void main(String[] args) {
        TestBotListner testBotListner = new TestBotListner();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(testBotListner);
    }
}
