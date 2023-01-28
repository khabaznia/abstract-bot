package com.khabaznia.bots.core.meta.container

import com.khabaznia.bots.core.meta.request.BaseRequest
import com.khabaznia.bots.core.service.ChatService
import com.khabaznia.bots.core.service.I18nService
import com.khabaznia.bots.core.trait.BaseRequests
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import static com.khabaznia.bots.core.util.BotSession.currentChat

@Slf4j
abstract class RequestsContainer implements BaseRequests {

    @Autowired
    protected I18nService i18nService
    @Autowired
    protected ChatService chatService

    protected List<BaseRequest> baseRequests = []
    protected Map<String, Map.Entry<List<String>, Map<String, String>>> lines = [:]
    protected Map<String, String> binding = [:]


    RequestsContainer leftShift(BaseRequest request) {
        if (request)
            baseRequests.add(request)
        this
    }

    RequestsContainer leftShift(List<BaseRequest> requests) {
        if (requests)
            baseRequests.addAll(requests)
        this
    }

    RequestsContainer newLine(String chatCode = currentChat.code) {
        addLineInternal('', chatCode, [:])
        this
    }

    RequestsContainer line(String line, String chatCode = currentChat.code, Map<String, String> binding = [:]) {
        if (line) addLineInternal(line, chatCode, binding)
        this
    }

    RequestsContainer lines(List<String> lines, String chatCode = currentChat.code, Map<String, String> binding = [:]) {
        if (lines) lines.findAll().each { line(it, chatCode, binding) }
        this
    }

    private void addLineInternal(String line, String chatCode, Map<String, String> binding) {
        if (chatCode) {
            boolean isExistingChat = lines.containsKey(chatCode)
            def oldLines = isExistingChat ? lines.get(chatCode).key : []
            def oldBinding = isExistingChat ? lines.get(chatCode).value : [:]
            oldLines.add(line)
            oldBinding.putAll(binding)
            lines.put(chatCode, new MapEntry(oldLines, oldBinding))
        }
    }

    RequestsContainer leftShift(RequestsContainer anotherRequestsContainer) {
        baseRequests.addAll(anotherRequestsContainer.baseRequests)
        anotherRequestsContainer.lines.each { chatCodeEntry ->
            chatCodeEntry.value.key.each {
                line(it, chatCodeEntry.key, chatCodeEntry.value.value)
            }
        }
        binding.putAll(anotherRequestsContainer.binding)
        this
    }

    List<BaseRequest> convertedRequests() {
        List<BaseRequest> aggregateMessages = lines.isEmpty() ? [] :
                lines.collect {
                    getAggregateMessageForEachChat(it.key, it.value.key, it.value.value)
                }
        (baseRequests + aggregateMessages).findAll()
    }

    void clean() {
        baseRequests = []
        lines = [:]
        binding = [:]
    }

    Map<String, Map.Entry<List<String>, Map<String, String>>> getLines() {
        lines
    }

    Map<String, String> getBinding() {
        binding
    }

    List<BaseRequest> getBaseRequests() {
        baseRequests
    }

    protected BaseRequest getAggregateMessageForEachChat(String chatCode, List<String> lines, Map binding) {
        sendMessage
                .text(getLocalizedLines(lines, binding, chatCode).join('\n'))
                .chatId(chatCode)
    }

    protected List<String> getLocalizedLines(List<String> lines, Map<String, String> binding, String chatCode) {
        lines.collect {
            i18nService.getFilledTemplate(it, binding, chatService.getChatLang(chatCode))
        }
    }
}
