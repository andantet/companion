package dev.andante.mccic.api.util;

public class EnumHelper {
    public static <T extends Enum<T>> T byId(Class<T> clazz, int ordinal) {
        T[] values = clazz.getEnumConstants();
        int l = values.length;
        return ordinal < 0 && ordinal < l ? values[ordinal] : values[0];
    }
}
