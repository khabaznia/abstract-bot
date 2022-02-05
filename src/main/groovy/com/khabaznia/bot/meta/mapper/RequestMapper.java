package com.khabaznia.bot.meta.mapper;

import com.khabaznia.bot.meta.request.impl.EditMessage;
import com.khabaznia.bot.meta.request.impl.PinMessage;
import com.khabaznia.bot.service.I18nService;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RequestMapper {

    @Autowired
    protected I18nService i18nService;
    @Autowired
    protected KeyboardMapper keyboardMapper;

    @Mapping(target = "text", expression = "java(i18nService.getFilledTemplate(source.getText(), source.getBinding(), source.getEmoji()))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toApiKeyboard(source.getKeyboard()))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract SendMessage toApiMethod(com.khabaznia.bot.meta.request.impl.SendMessage source);

    public abstract DeleteMessage toApiMethod(com.khabaznia.bot.meta.request.impl.DeleteMessage source);

    public abstract GetMe toApiMethod(com.khabaznia.bot.meta.request.impl.GetMe source);

    @Mapping(target = "text", expression = "java(i18nService.getFilledTemplate(source.getText(), source.getBinding(), source.getEmoji()))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toInlineApiKeyboard(source.getKeyboard()))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract EditMessageText toApiEditMessageText(EditMessage source);

    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toInlineApiKeyboard(source.getKeyboard()))")
    public abstract EditMessageReplyMarkup toApiEditReplyKeyboard(EditMessage source);

    public BotApiMethod toApiMethod(EditMessage source) {
        String messageText = i18nService.getFilledTemplate(source.getText(), source.getBinding(), source.getEmoji());
        return messageText == null || "".equals(messageText)
                ? toApiEditReplyKeyboard(source)
                : toApiEditMessageText(source);
    }

    public abstract SendChatAction toApiMethod(com.khabaznia.bot.meta.request.impl.SendChatAction source);

    public abstract PinChatMessage toApiMethod(PinMessage source);

    @Mapping(target = "audio", expression = "java(new org.telegram.telegrambots.meta.api.objects.InputFile(source.getAudio()))")
    @Mapping(target = "caption", expression = "java(i18nService.getFilledTemplate(source.getText(), source.getBinding(), source.getEmoji()))")
    public abstract SendAudio toApiMethod(com.khabaznia.bot.meta.request.impl.SendAudio source);

    @Mapping(target = "video", expression = "java(new org.telegram.telegrambots.meta.api.objects.InputFile(source.getVideo()))")
    @Mapping(target = "caption", expression = "java(i18nService.getFilledTemplate(source.getText(), source.getBinding(), source.getEmoji()))")
    public abstract SendVideo toApiMethod(com.khabaznia.bot.meta.request.impl.SendVideo source);

    @Mapping(target = "photo", expression = "java(new org.telegram.telegrambots.meta.api.objects.InputFile(source.getPhoto()))")
    @Mapping(target = "caption", expression = "java(i18nService.getFilledTemplate(source.getText(), source.getBinding(), source.getEmoji()))")
    public abstract SendPhoto toApiMethod(com.khabaznia.bot.meta.request.impl.SendPhoto source);
}
