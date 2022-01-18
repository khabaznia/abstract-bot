package com.khabaznia.bot.meta.mapper;

import com.khabaznia.bot.meta.request.impl.EditMessage;
import com.khabaznia.bot.service.I18nService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RequestMapper {

    @Autowired
    protected I18nService i18nService;
    @Autowired
    protected KeyboardMapper keyboardMapper;

    @Mapping(target = "text", expression = "java(i18nService.getFilledTemplate(source.getKey(), source.getBinding()))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toApiKeyboard(source.getKeyboard()))")
    public abstract SendMessage toApiMethod(com.khabaznia.bot.meta.request.impl.SendMessage source);

    public abstract DeleteMessage toApiMethod(com.khabaznia.bot.meta.request.impl.DeleteMessage source);

    @Mapping(target = "text", expression = "java(i18nService.getFilledTemplate(source.getKey(), source.getBinding()))")
    public abstract EditMessageText toApiMethod(EditMessage source);

    public abstract SendChatAction toApiMethod(com.khabaznia.bot.meta.request.impl.SendChatAction source);
}
