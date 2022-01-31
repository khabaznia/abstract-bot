package com.khabaznia.bot.security.authentication.filter

import org.apache.commons.io.IOUtils

import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class MultiReadHttpServletRequest extends HttpServletRequestWrapper {

    protected ByteArrayOutputStream cachedBytes

    MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request)
    }

    @Override
    ServletInputStream getInputStream() throws IOException {
        if (cachedBytes == null)
            cacheInputStream()
        new CachedServletInputStream()
    }

    @Override
    BufferedReader getReader() throws IOException {
        new BufferedReader(new InputStreamReader(getInputStream()))
    }

    private void cacheInputStream() throws IOException {
        cachedBytes = new ByteArrayOutputStream()
        IOUtils.copy(super.inputStream, cachedBytes)
    }

    class CachedServletInputStream extends ServletInputStream {
        private ByteArrayInputStream input

        CachedServletInputStream() {
            input = new ByteArrayInputStream(cachedBytes.toByteArray())
        }

        @Override
        boolean isFinished() {
            input.available() == 0
        }

        @Override
        boolean isReady() {
            true
        }

        @Override
        void setReadListener(ReadListener readListener) {
            throw new RuntimeException("Not implemented")
        }

        @Override
        int read() throws IOException {
            input.read()
        }
    }
}

