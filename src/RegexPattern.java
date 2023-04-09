import java.util.regex.Pattern;

public record RegexPattern(String regex) {

    public Pattern getPattern() {
        return Pattern.compile(regex);
    }
}
