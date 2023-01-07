package com.khabaznia.bots.core.meta.mapper;

import com.khabaznia.bots.core.meta.request.impl.EditMessage;
import com.khabaznia.bots.core.meta.request.impl.PinMessage;
import com.khabaznia.bots.core.meta.request.impl.UnpinAllMessages;
import com.khabaznia.bots.core.meta.request.impl.UnpinMessage;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinAllChatMessages;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RequestMapper {

    @Autowired
    protected KeyboardMapper keyboardMapper;
    @Autowired
    protected MediaMapper mediaMapper;
    @Autowired
    protected MetaObjectsMapper metaObjectsMapper;
    @Autowired
    protected MappingHelper mappingHelper;

    @Mapping(target = "text", expression = "java(mappingHelper.getLocalizedText(source))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toApiKeyboard(source))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract SendMessage toApiMethod(com.khabaznia.bots.core.meta.request.impl.SendMessage source);

    @Mapping(target = "messageId", expression = "java(mappingHelper.getMessageId(source))")
    public abstract DeleteMessage toApiMethod(com.khabaznia.bots.core.meta.request.impl.DeleteMessage source);

    public abstract GetMe toApiMethod(com.khabaznia.bots.core.meta.request.impl.GetMe source);

    @Mapping(target = "text", expression = "java(mappingHelper.getLocalizedText(source))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toInlineApiKeyboard(source))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract EditMessageText toApiEditMessageText(EditMessage source);

    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toInlineApiKeyboard(source))")
    public abstract EditMessageReplyMarkup toApiEditReplyKeyboard(EditMessage source);

    public BotApiMethod toApiMethod(EditMessage source) {
        String messageText = mappingHelper.getLocalizedText(source);
        return messageText == null || "".equals(messageText)
                ? toApiEditReplyKeyboard(source)
                : toApiEditMessageText(source);
    }

    public abstract SendChatAction toApiMethod(com.khabaznia.bots.core.meta.request.impl.SendChatAction source);

    @Mapping(target = "messageId", expression = "java(mappingHelper.getMessageId(source))")
    public abstract PinChatMessage toApiMethod(PinMessage source);

    @Mapping(target = "messageId", expression = "java(mappingHelper.getMessageId(source))")
    public abstract UnpinChatMessage toApiMethod(UnpinMessage source);

    @Mapping(target = "audio", expression = "java(mediaMapper.toApiInputFile(source))")
    @Mapping(target = "caption", expression = "java(mappingHelper.getLocalizedText(source))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toApiKeyboard(source))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract SendAudio toApiMethod(com.khabaznia.bots.core.meta.request.impl.SendAudio source);

    @Mapping(target = "video", expression = "java(mediaMapper.toApiInputFile(source))")
    @Mapping(target = "caption", expression = "java(mappingHelper.getLocalizedText(source))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toApiKeyboard(source))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract SendVideo toApiMethod(com.khabaznia.bots.core.meta.request.impl.SendVideo source);

    @Mapping(target = "photo", expression = "java(mediaMapper.toApiInputFile(source))")
    @Mapping(target = "caption", expression = "java(mappingHelper.getLocalizedText(source))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toApiKeyboard(source))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract SendPhoto toApiMethod(com.khabaznia.bots.core.meta.request.impl.SendPhoto source);

    @Mapping(target = "animation", expression = "java(mediaMapper.toApiInputFile(source))")
    @Mapping(target = "caption", expression = "java(mappingHelper.getLocalizedText(source))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toApiKeyboard(source))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract SendAnimation toApiMethod(com.khabaznia.bots.core.meta.request.impl.SendAnimation source);

    @Mapping(target = "document", expression = "java(mediaMapper.toApiInputFile(source))")
    @Mapping(target = "caption", expression = "java(mappingHelper.getLocalizedText(source))")
    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toApiKeyboard(source))")
    @Mapping(target = "parseMode", constant = "HTML")
    public abstract SendDocument toApiMethod(com.khabaznia.bots.core.meta.request.impl.SendDocument source);

    public abstract GetChat toApiMethod(com.khabaznia.bots.core.meta.request.impl.GetChat source);

    @Mapping(target = "photo", expression = "java(mediaMapper.toApiInputFile(source.getFileIdentifier(), true))")
    public abstract SetChatPhoto toApiMethod(com.khabaznia.bots.core.meta.request.impl.SetChatPhoto source);

    public abstract SetChatTitle toApiMethod(com.khabaznia.bots.core.meta.request.impl.SetChatTitle source);

    public abstract BanChatMember toApiMethod(com.khabaznia.bots.core.meta.request.impl.BanChatMember source);

    public abstract LeaveChat toApiMethod(com.khabaznia.bots.core.meta.request.impl.LeaveChat source);

    public abstract UnpinAllChatMessages toApiMethod(UnpinAllMessages source);

    @Mapping(target = "permissions", expression = "java(metaObjectsMapper.toApiObject(source.getPermissions()))")
    public abstract SetChatPermissions toApiMethod(com.khabaznia.bots.core.meta.request.impl.SetChatPermissions source);

    public abstract GetChatMember toApiMethod(com.khabaznia.bots.core.meta.request.impl.GetChatMember source);

    @Mapping(target = "scope", expression = "java(mappingHelper.mapScope(source.getScope()))")
    @Mapping(target = "commands", expression = "java(metaObjectsMapper.toApiObjects(source.getCommands()))")
    public abstract SetMyCommands toApiMethod(com.khabaznia.bots.core.meta.request.impl.SetMyCommands source);

    @Mapping(target = "replyMarkup", expression = "java(keyboardMapper.toApiKeyboard(source))")
    public abstract SendLocation toApiMethod(com.khabaznia.bots.core.meta.request.impl.SendLocation source);
}
