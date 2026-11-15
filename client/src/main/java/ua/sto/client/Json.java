package ua.sto.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Мінімальний розбір і формування JSON (щоб клієнт не потребував сторонніх бібліотек).
 * Розбір повертає: Map (об'єкт), List (масив), String, Double, Boolean або null.
 */
public final class Json {

    private final String s;
    private int pos;

    private Json(String s) {
        this.s = s;
    }

    public static Object parse(String text) {
        Json p = new Json(text);
        p.skipWs();
        Object v = p.value();
        p.skipWs();
        if (p.pos != p.s.length()) {
            throw new IllegalArgumentException("Зайві символи в JSON на позиції " + p.pos);
        }
        return v;
    }

    private Object value() {
        skipWs();
        if (pos >= s.length()) {
            throw new IllegalArgumentException("Несподіваний кінець JSON");
        }
        char c = s.charAt(pos);
        switch (c) {
            case '{':
                return object();
            case '[':
                return array();
            case '"':
                return string();
            case 't':
                expect("true");
                return Boolean.TRUE;
            case 'f':
                expect("false");
                return Boolean.FALSE;
            case 'n':
                expect("null");
                return null;
            default:
                return number();
        }
    }

    private Map<String, Object> object() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++; // {
        skipWs();
        if (peek() == '}') {
            pos++;
            return map;
        }
        while (true) {
            skipWs();
            String key = string();
            skipWs();
            if (s.charAt(pos++) != ':') {
                throw new IllegalArgumentException("Очікувалась ':' на позиції " + (pos - 1));
            }
            map.put(key, value());
            skipWs();
            char c = s.charAt(pos++);
            if (c == '}') {
                return map;
            }
            if (c != ',') {
                throw new IllegalArgumentException("Очікувалась ',' або '}' на позиції " + (pos - 1));
            }
        }
    }

    private List<Object> array() {
        List<Object> list = new ArrayList<>();
        pos++; // [
        skipWs();
        if (peek() == ']') {
            pos++;
            return list;
        }
        while (true) {
            list.add(value());
            skipWs();
            char c = s.charAt(pos++);
            if (c == ']') {
                return list;
            }
            if (c != ',') {
                throw new IllegalArgumentException("Очікувалась ',' або ']' на позиції " + (pos - 1));
            }
        }
    }

    private String string() {
        if (s.charAt(pos) != '"') {
            throw new IllegalArgumentException("Очікувався рядок на позиції " + pos);
        }
        pos++;
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = s.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                char e = s.charAt(pos++);
                switch (e) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'u' -> {
                        sb.append((char) Integer.parseInt(s.substring(pos, pos + 4), 16));
                        pos += 4;
                    }
                    default -> sb.append(e); // \" \\ \/
                }
            } else {
                sb.append(c);
            }
        }
    }

    private Double number() {
        int start = pos;
        while (pos < s.length() && "+-0123456789.eE".indexOf(s.charAt(pos)) >= 0) {
            pos++;
        }
        if (start == pos) {
            throw new IllegalArgumentException("Невірний JSON на позиції " + pos);
        }
        return Double.valueOf(s.substring(start, pos));
    }

    private void expect(String word) {
        if (!s.startsWith(word, pos)) {
            throw new IllegalArgumentException("Невірний JSON на позиції " + pos);
        }
        pos += word.length();
    }

    private char peek() {
        return s.charAt(pos);
    }

    private void skipWs() {
        while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
            pos++;
        }
    }

    // ---------- формування JSON ----------

    public static String quote(String text) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append('"').toString();
    }
}
