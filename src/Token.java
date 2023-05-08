import java.util.Map;

public record Token(Token.Type type, String key, String value) {
    public enum Type {
        KEY_VALUE_PAIR,
        LEFT_BRACE,
        RIGHT_BRACE,
        VALUE_SERIES
    }

    public Map<String, String> toMap() {
        return Map.of(key, value);
    }

    @Override
    public String toString() {
        if(this.type==Type.RIGHT_BRACE) {
            return "Token: [" +
                    "type= " + type +
                    ']';
        }
        if(this.type==Type.LEFT_BRACE) {
            if(this.key==null) {
                return "Token: [" +
                        "type= ! " + type +
                        " ! ]";
            }
            return "Token: [" +
                    "type= " + type +
                    ", key= " + key +
                    ']';
        }
        if(this.type==Type.VALUE_SERIES) {
            return "Token: [" +
                    "type= " + type +
                    ", value= " + value +
                    ']';
        }
        return "Token: [" +
                "type= " + type +
                ", key= " + key +
                ", value= " + value +
                ']';
    }
}