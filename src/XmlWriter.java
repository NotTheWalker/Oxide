import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class XmlWriter {
    private Stack<Element> elementStack = new Stack<>();
    private Document document;

    public Document convertToXml(Token[] tokens) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.newDocument();

        for (Token token : tokens) {
            switch (token.type()) {
                case LEFT_BRACE -> createAndPushElement(token.key());
                case RIGHT_BRACE -> elementStack.pop();
                case KEY_VALUE_PAIR -> setAttribute(token.key(), token.value());
                case VALUE_SERIES -> setAttribute(token.type().name(), token.value());
            }
        }

        return document;
    }

    private void createAndPushElement(String elementName) {
        String escapedElementName = escapeKey(elementName);
        Element element = document.createElement(escapedElementName);
        if (elementStack.empty()) {
            document.appendChild(element);
        } else {
            elementStack.peek().appendChild(element);
        }
        elementStack.push(element);
    }

    private void setAttribute(String key, String value) {
        String escapedKey = escapeKey(key);
        String escapedValue = removeQuotes(value);
        elementStack.peek().setAttribute(escapedKey, escapedValue);
    }

    public static String escapeKey(String key) {
        // Prepend an underscore if the key starts with a number
        if (Character.isDigit(key.charAt(0))) {
            key = "_" + key;
        }

        // Replace special characters with underscores
        key = key.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        //TODO: make inverse function for xml reader?
        return key;
    }

    public static String removeQuotes(String value) {
        return value.replaceAll("\"", "");
    }

}