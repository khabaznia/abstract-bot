package com.khabaznia.bot.strategy

interface MediaFileRetrievingStrategy {

    InputStream getMediaForCode(String fileCode)
}
