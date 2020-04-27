package me.oriharel.machinery.inventory;

/**
 * Anything that stores data that is passed on navigation should implement this
 * @param <T> type of data stored
 */
public interface NavigableData<T> {
    T getStoredData();
    NavigableData<T> setStoredData(T data);
}
