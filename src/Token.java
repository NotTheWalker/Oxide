import java.util.Map;

public record Token(Token.Type type, String key, String value) {
    public enum Type {
        KEY_VALUE_PAIR,
        LEFT_BRACE,
        RIGHT_BRACE
    }

    public Map<String, String> toMap() {
        return Map.of(key, value);
    }

    public ValueInstance toValueInstance() {
        return new ValueInstance(value);
    }

}