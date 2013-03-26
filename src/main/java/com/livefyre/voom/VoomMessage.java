package com.livefyre.voom;

import java.util.ArrayList;
import java.util.Map;

public class VoomMessage<T> extends AbstractMessage {
    private ArrayList<VoomMessagePart<T>> messages;

    public VoomMessage() {
        super();
        messages = new ArrayList<VoomMessagePart<T>>();
    }

    public VoomMessage(VoomHeaders headers) {
        this();
        this.headers = headers;
    }

    public VoomMessage(Map<String, String> headers) {
        this(new VoomHeaders(headers));
    }

    public VoomMessage(VoomHeaders headers, ArrayList<VoomMessagePart<T>> messages) {
        this(headers);
        this.messages.addAll(messages);
    }

    public VoomMessage(Map<String, String> headers, ArrayList<VoomMessagePart<T>> messages) {
        this(new VoomHeaders(headers), messages);
    }


    public VoomMessage(VoomMessage<T> other) {
        this();
        headers.putAll(other.headers.toMap());
        for (VoomMessagePart<T> part:other.getMessages()) {
            messages.add(part.clone());
        }
    }

    public void addMessage(VoomMessagePart<T> msg) {
        messages.add(msg);
    }

    public ArrayList<VoomMessagePart<T>> getMessages() {
        return messages;
    }
}
