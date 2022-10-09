package org.utk.nikita.example;

import lombok.SneakyThrows;
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
import org.utk.nikita.example.entity.CurrencyEnum;
import org.utk.nikita.example.service.CurrencyConversionService;
import org.utk.nikita.example.service.CurrencyModeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class ConverterBot extends TelegramLongPollingBot{

    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();
    private final CurrencyConversionService currencyConversionService = CurrencyConversionService.getInstance();

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
        CurrencyEnum newCurrencyEnum = CurrencyEnum.valueOf(param[1]);
        switch (action) {
            case "ORIGINAL":
                currencyModeService.setOriginalCurrency(message.getChatId(), newCurrencyEnum);
                break;
            case "TARGET":
                currencyModeService.setTargetCurrency(message.getChatId(), newCurrencyEnum);
                break;
        }
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        CurrencyEnum originalCurrencyEnum = currencyModeService.getOriginalCurrency(message.getChatId());
        CurrencyEnum targetCurrencyEnum = currencyModeService.getTargetCurrency(message.getChatId());
        for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
            buttons.add(
                    Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(originalCurrencyEnum, currencyEnum))
                                    .callbackData("ORIGINAL:" + currencyEnum)
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(targetCurrencyEnum, currencyEnum))
                                    .callbackData("TARGET:" + currencyEnum)
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
                       CurrencyEnum originalCurrencyEnum = currencyModeService.getOriginalCurrency(message.getChatId());
                       CurrencyEnum targetCurrencyEnum = currencyModeService.getTargetCurrency(message.getChatId());

                       for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
                           buttons.add(
                                   Arrays.asList(
                                           InlineKeyboardButton.builder()
                                                   .text(getCurrencyButton(originalCurrencyEnum, currencyEnum))
                                                   .callbackData("ORIGINAL:" + currencyEnum)
                                                   .build(),
                                           InlineKeyboardButton.builder()
                                                   .text(getCurrencyButton(targetCurrencyEnum, currencyEnum))
                                                   .callbackData("TARGET:" + currencyEnum)
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
        if(message.hasText()){
            String messageText = message.getText();
            Optional<Double> value = parseDouble(messageText);
            CurrencyEnum originalCurrencyEnum = currencyModeService.getOriginalCurrency(message.getChatId());
            CurrencyEnum targetCurrencyEnum = currencyModeService.getTargetCurrency(message.getChatId());
            double ratio = currencyConversionService.getConversionRatio(originalCurrencyEnum, targetCurrencyEnum);
            if(value.isPresent()){
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(String.format("%4.2f %s is %4.2f %s", value.get(), originalCurrencyEnum, (value.get()*ratio), targetCurrencyEnum))
                        .build());
                return;
            }
        }
    }

    private Optional<Double> parseDouble(String messageText) {
        try{
            return Optional.of(Double.parseDouble(messageText));
        } catch (Exception e){
            return Optional.empty();
        }
    }


    private String getCurrencyButton(CurrencyEnum saved, CurrencyEnum current){
        return saved == current ? current + "✅" : current.name();
    }

    @SneakyThrows
    public static void main(String[] args) {
        ConverterBot converterBot = new ConverterBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(converterBot);
    }
}
