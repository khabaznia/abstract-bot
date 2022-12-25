package com.khabaznia.bot.meta.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MetaObjectsMapper {

    public abstract ChatPermissions toApiObject(com.khabaznia.bot.meta.object.ChatPermissions source);

    public abstract List<BotCommand> toApiObjects(List<com.khabaznia.bot.meta.object.BotCommand> source);
}
