package com.celsius.domain.prototype;

/** Prototype: un objeto que sabe crear una copia independiente de sí mismo. */
public interface Prototype<T extends Prototype<T>> {
    T copy();
}
