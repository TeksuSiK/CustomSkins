package pl.teksusik.customskins.util;

import org.bukkit.Bukkit;

import javax.management.ReflectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ReflectionHelper {
    private static final Map<Class<?>, Class<?>> builtInMap = new HashMap<>();
    public static String serverVersion = null;

    static {
        builtInMap.put(Integer.class, Integer.TYPE);
        builtInMap.put(Long.class, Long.TYPE);
        builtInMap.put(Double.class, Double.TYPE);
        builtInMap.put(Float.class, Float.TYPE);
        builtInMap.put(Boolean.class, Boolean.TYPE);
        builtInMap.put(Character.class, Character.TYPE);
        builtInMap.put(Byte.class, Byte.TYPE);
        builtInMap.put(Short.class, Short.TYPE);

        try {
            Class.forName("org.bukkit.Bukkit");
            serverVersion = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);
        } catch (Exception ignored) {
        }
    }

    private ReflectionHelper() {
    }

    public static Class<?> getBukkitClass(String clazz) throws Exception {
        return Class.forName("org.bukkit.craftbukkit." + serverVersion + "." + clazz);
    }

    public static Class<?> getBungeeClass(String path, String clazz) throws Exception {
        return Class.forName("net.md_5.bungee." + path + "." + clazz);
    }

    private static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) throws Exception {
        Constructor<?> c = clazz.getConstructor(args);
        c.setAccessible(true);

        return c;
    }

    public static Enum<?> getEnum(Class<?> clazz, String constant) throws Exception {
        Class<?> c = Class.forName(clazz.getName());
        Enum<?>[] econstants = (Enum<?>[]) c.getEnumConstants();
        for (Enum<?> e : econstants)
            if (e.name().equalsIgnoreCase(constant))
                return e;
        throw new Exception("Enum constant not found " + constant);
    }

    public static Enum<?> getEnum(Class<?> clazz, String enumname, String constant) throws Exception {
        Class<?> c = Class.forName(clazz.getName() + "$" + enumname);
        Enum<?>[] econstants = (Enum<?>[]) c.getEnumConstants();
        for (Enum<?> e : econstants)
            if (e.name().equalsIgnoreCase(constant))
                return e;
        throw new Exception("Enum constant not found " + constant);
    }

    private static Method getMethod(Class<?> clazz, String mname) {
        Method m;
        try {
            m = clazz.getDeclaredMethod(mname);
        } catch (Exception e) {
            try {
                m = clazz.getMethod(mname);
            } catch (Exception ex) {
                return null;
            }
        }

        m.setAccessible(true);
        return m;
    }

    public static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return field;
            }
        }

        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }

    private static Method getMethod(Class<?> clazz, String mname, Class<?>... args) {
        Method m;
        try {
            m = clazz.getDeclaredMethod(mname, args);
        } catch (Exception e) {
            try {
                m = clazz.getMethod(mname, args);
            } catch (Exception ex) {
                return null;
            }
        }

        m.setAccessible(true);
        return m;
    }

    public static Class<?> getNMSClass(String clazz, String fullClassName) throws ReflectionException {
        try {
            return forNameWithFallback(clazz, fullClassName);
        } catch (ClassNotFoundException e) {
            throw new ReflectionException(e);
        }
    }

    private static Class<?> forNameWithFallback(String clazz, String fullClassName) throws ClassNotFoundException {
        try {
            return Class.forName("net.minecraft.server." + serverVersion + "." + clazz);
        } catch (ClassNotFoundException ignored) {
            return Class.forName(fullClassName);
        }
    }

    public static Object invokeConstructor(Class<?> clazz, Class<?>[] args, Object... initargs) throws ReflectionException {
        try {
            return getConstructor(clazz, args).newInstance(initargs);
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    public static Object invokeConstructor(Class<?> clazz, Object... initargs) throws ReflectionException {
        try {
            return getConstructor(clazz, toClassArray(initargs)).newInstance(initargs);
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    private static Class<?>[] toClassArray(Object[] args) {
        return Stream.of(args).map(Object::getClass).map(ReflectionHelper::convertToPrimitive).toArray(Class<?>[]::new);
    }

    private static Class<?> convertToPrimitive(Class<?> clazz) {
        return builtInMap.getOrDefault(clazz, clazz);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method) throws ReflectionException {
        try {
            return Objects.requireNonNull(getMethod(clazz, method)).invoke(obj);
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method, Class<?>[] args, Object... initargs) throws ReflectionException {
        try {
            return Objects.requireNonNull(getMethod(clazz, method, args)).invoke(obj, initargs);
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method, Object... initargs) throws ReflectionException {
        try {
            return Objects.requireNonNull(getMethod(clazz, method)).invoke(obj, initargs);
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    public static Object invokeMethod(Object obj, String method) throws ReflectionException {
        try {
            return Objects.requireNonNull(getMethod(obj.getClass(), method)).invoke(obj);
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    public static Object invokeMethod(Object obj, String method, Object[] initargs) throws ReflectionException {
        try {
            return Objects.requireNonNull(getMethod(obj.getClass(), method)).invoke(obj, initargs);
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }
}
