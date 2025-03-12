package io.github.spigotrce.paradiseclientfabric.chatroom.common.model;

public record ServerModel(int port, boolean useHAProxy, int messageCooldown, int maxMessageCharacters, int connectionThrottle) {
}
