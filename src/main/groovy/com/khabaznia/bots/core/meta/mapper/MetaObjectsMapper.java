package com.khabaznia.bots.core.meta.mapper;

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

    public abstract ChatPermissions toApiObject(com.khabaznia.bots.core.meta.object.ChatPermissions source);

    public abstract List<BotCommand> toApiObjects(List<com.khabaznia.bots.core.meta.object.BotCommand> source);
}
