package com.github.romualdrousseau.shuju.json;

import java.util.Optional;

public interface JSONObject {

    Iterable<String> keys();

    <T> Optional<T> get(String k);

    <T> JSONObject set(String k, T o);

    JSONObject remove(String k);

    String toString(final boolean pretty);

    String toString();
}
