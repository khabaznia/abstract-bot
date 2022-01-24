package com.khabaznia.bot.meta.mapper;

import com.khabaznia.bot.meta.request.impl.EditMessage;
import com.khabaznia.bot.meta.request.impl.EditMessageKeyboard;
import com.khabaznia.bot.meta.request.impl.PinMessage;
import com.khabaznia.bot.service.I18nService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    @Mapping(target = "text", expression = "java(i18nService.getFilledTemplate(source.getKey(), source.getBinding(), source.getEmoji()))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toApiKeyboard(source.getKeyboard()))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract SendMessage toApiMethod(com.khabaznia.bot.meta.request.impl.SendMessage source);

    public abstract DeleteMessage toApiMethod(com.khabaznia.bot.meta.request.impl.DeleteMessage source);

    public abstract GetMe toApiMethod(com.khabaznia.bot.meta.request.impl.GetMe source);

    @Mapping(target = "text", expression = "java(i18nService.getFilledTemplate(source.getKey(), source.getBinding()))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toInlineApiKeyboard(source.getKeyboard()))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract EditMessageText toApiMethod(EditMessage source);

    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toInlineApiKeyboard(source.getKeyboard()))")
    public abstract EditMessageReplyMarkup toApiMethod(EditMessageKeyboard source);

    public abstract SendChatAction toApiMethod(com.khabaznia.bot.meta.request.impl.SendChatAction source);

    public abstract PinChatMessage toApiMethod(PinMessage source);


}
