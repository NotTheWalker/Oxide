import java.util.Map;
import java.util.logging.Logger;

public class ValueInstance {
    Logger logger = Logger.getLogger(ValueInstance.class.getName());

    private String value;
    private Map<String, ValueInstance> children;

    public ValueInstance(String value) {
        this.value = value;
    }

    public ValueInstance(Map<String, ValueInstance> children) {
        this.children = children;
    }

    public void setValue(Object value) {
        if(value instanceof String) {
            this.value = (String) value;
        } else if(value instanceof Map) {
            if(((Map<?, ?>) value).keySet().stream().anyMatch(key -> !(key instanceof String))) {
                throw new IllegalArgumentException("Map keys must be Strings");
            }
            if(((Map<?, ?>) value).values().stream().anyMatch(val -> !(val instanceof ValueInstance))) {
                throw new IllegalArgumentException("Map values must be ValueInstances");
            }
            this.children = (Map<String, ValueInstance>) value;
        } else {
            throw new IllegalArgumentException("Value must be a String or Map<String, ValueInstance>");
        }
    }

    public Object getValue() {
        if (value != null) {
            return value;
        } else {
            return children;
        }
    }
    public String toString() {
        if (value != null) {
            return value+"\n";
        } else if(children != null) {
            StringBuilder childrenString = new StringBuilder();
            for (Map.Entry<String, ValueInstance> entry : children.entrySet()) {
                childrenString.append("\t");
                childrenString.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
            }
            return childrenString.toString();
        } else {
            logger.warning("ValueInstance has no value or children");
            return "";
        }
    }
}
