package com.khabaznia.bot.meta.utils

import com.khabaznia.bot.meta.request.BaseRequest


class BotRequestList<T extends BaseRequest> extends ArrayList<BaseRequest> {

    private Integer requestOrder

    BotRequestList() {
        requestOrder = 1
    }

    @Override
    boolean add(final BaseRequest request) {
        request.order = requestOrder++
        return super.add(request)
    }
}
