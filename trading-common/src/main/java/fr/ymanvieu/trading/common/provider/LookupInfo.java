package fr.ymanvieu.trading.common.provider;

import java.util.Objects;

public record LookupInfo(String code, String name, String exchange, String type, String providerCode) {

    public LookupInfo {
        Objects.requireNonNull(code);
        Objects.requireNonNull(name);
        Objects.requireNonNull(exchange);
        Objects.requireNonNull(type);
        Objects.requireNonNull(providerCode);
    }
}
