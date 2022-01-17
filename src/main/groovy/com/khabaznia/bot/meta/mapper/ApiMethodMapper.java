package com.khabaznia.bot.meta.mapper;

import com.khabaznia.bot.enums.MessageType;
import com.khabaznia.bot.meta.request.impl.EditMessage;
import com.khabaznia.bot.meta.response.impl.BooleanResponse;
import com.khabaznia.bot.meta.response.impl.MessageResponse;
import com.khabaznia.bot.service.I18nService;
import com.khabaznia.bot.util.SessionUtil;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ApiMethodMapper {

    @Autowired
    protected I18nService i18nService;

    @Mapping(target = "text", expression = "java(i18nService.getFilledTemplate(source.getKey(), source.getBinding()))")
    public abstract SendMessage toApiMethod(com.khabaznia.bot.meta.request.impl.SendMessage source);

    public abstract DeleteMessage toApiMethod(com.khabaznia.bot.meta.request.impl.DeleteMessage source);

    @Mapping(target = "text", expression = "java(i18nService.getFilledTemplate(source.getKey(), source.getBinding()))")
    public abstract EditMessageText toApiMethod(EditMessage source);

    public abstract SendChatAction toApiMethod(com.khabaznia.bot.meta.request.impl.SendChatAction source);

    public BooleanResponse toResponse(Boolean result, MessageType type) {
        BooleanResponse response = new BooleanResponse();
        response.setResult(result);
        return response;
    }

    public MessageResponse toResponse(Message result, MessageType type){
        MessageResponse response = new MessageResponse();
        com.khabaznia.bot.model.Message mappedMessage = mapMessage(result);
        mappedMessage.setType(type);
        response.setResult(mappedMessage);
        return response;
    }

    public  com.khabaznia.bot.model.Message mapMessage(Message source){
        com.khabaznia.bot.model.Message target = new com.khabaznia.bot.model.Message();
        target.setMessageId(source.getMessageId());
        target.setChat(SessionUtil.getCurrentChat());
        target.setText(source.getText());
        return target;
    }




}
