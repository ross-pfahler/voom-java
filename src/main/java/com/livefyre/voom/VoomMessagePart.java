package com.livefyre.voom;

import java.util.HashMap;
import java.util.Map;

public abstract class VoomMessagePart<T> extends AbstractMessage  {
    protected T body;
    protected VoomHeaders headers;

    public VoomMessagePart(VoomHeaders headers) {
        this.headers = headers;
    }

    public VoomMessagePart(Map<String, String> headers) {
        this(new VoomHeaders(headers));
    }

    public VoomMessagePart(VoomHeaders headers, T body) {
        this.headers = headers;
        this.body = body;
    }

    public VoomMessagePart(Map<String, String> headers, T body) {
        this(new VoomHeaders(headers), body);
    }

    public VoomMessagePart(T body) {
        this(new HashMap<String, String>(), body);
    }
    @Override
    protected abstract VoomMessagePart<T> clone();

    public T getBody() {
        return body;
    }
}
