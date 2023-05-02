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
        Element element = document.createElement(elementName);
        if (elementStack.empty()) {
            document.appendChild(element);
        } else {
            elementStack.peek().appendChild(element);
        }
        elementStack.push(element);
    }

    private void setAttribute(String key, String value) {
        elementStack.peek().setAttribute(key, value);
    }
}