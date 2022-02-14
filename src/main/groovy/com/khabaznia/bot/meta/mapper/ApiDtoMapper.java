package com.khabaznia.bot.meta.mapper;

import com.khabaznia.bot.integration.dto.SendMessageDto;
import com.khabaznia.bot.meta.request.impl.SendMessage;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ApiDtoMapper {

    @Autowired
    private ApplicationContext context;

    @Mapping(target = "metaClass", ignore = true)
    public abstract void mapToBotRequest(@MappingTarget SendMessage target, SendMessageDto source);

    public SendMessage toBotRequest(SendMessageDto source) {
        SendMessage target = (SendMessage) context.getBean("sendMessage");
        mapToBotRequest(target, source);
        target.inlineKeyboard(source.getButtons());
        return target;
    }
}
