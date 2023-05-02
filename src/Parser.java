import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    //this class will parse text files and build objects which consist of <key> = <value> entries
    //splitting on "{"'s and closing on "}"'s, it should be able to parse every hoi4 file
    static Logger logger = Logger.getLogger(Parser.class.getName());
    private final String inputFileString;
    private final String outputFileString;
    protected final String regex = "^(.+)\\/(.+?)\\.[^\\.]+$";
    protected final Pattern pattern = Pattern.compile(regex);
    private final Document parsedData;
    private final String dataString;

    public Parser(File inputFile) {
        this.inputFileString = inputFile.getPath();
        Matcher matcher = pattern.matcher(inputFileString);
        String dirName = "";
        String filename = "";
        while(matcher.find()) {
            if(matcher.groupCount() > 2) {
                logger.warning("Possible error in filename, more than one group found");
            }
            dirName = "src/resources/"+matcher.group(1);
            filename = matcher.group(2)+".xml";
            logger.info("filename: " + filename);
        }
        this.outputFileString = filename;
        this.parsedData = parseToDict();
        this.dataString = parsedData.toString();
        saveDocumentToXML(dirName);
    }

    public String getInputFileString() {
        return inputFileString;
    }

    public Document getParsedData() {
        return parsedData;
    }

    public String getDataString() {
        return dataString;
    }

    private Document parseToDict() {
        String data = fileToText(inputFileString);
        data = cleanText(data);
        List<Token> tokens = tokenize(data);
        return parseTokens(tokens);
    }

    private static String fileToText(String filePath) {
        Path pathToFile = Paths.get(filePath);
        StringBuilder text = new StringBuilder();
        try(BufferedReader br = Files.newBufferedReader(pathToFile)) {
            String line = br.readLine();
            while (line!=null) {
                text.append(removeComments(line));
                line=br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return text.toString();
    }

    private static String cleanText(String text) {
        text = text.replace("\t", " ");
        text = text.replace("}", " } ");
        text = text.replace("{", " { ");
        text = text.replace("=", " = ");
        return text.trim().replaceAll("\\s+", " ");
    }

    private static String removeComments(String text) {
        return text.replaceAll("#.*", "");
    }

    private List<Token> tokenize(String text) {
        List<Token> tokens = new ArrayList<>();
        String[] parts = text.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            logger.info(parts[i]);
            switch (parts[i]) {
                case "{" -> {
                    Token token = new Token(Token.Type.LEFT_BRACE, null, null);
                    tokens.add(token);
                    logger.warning("unexpected left brace found without matching key");
                }
                case "}" -> {
                    Token token = new Token(Token.Type.RIGHT_BRACE, null, null);
                    tokens.add(token);
                }
                case "=" -> {
                    int relIndexToNextLeftBrace = nextPart(parts, "{", i);
                    Token token;
                    if (relIndexToNextLeftBrace == 1) {
                        token = new Token(Token.Type.LEFT_BRACE, parts[i - 1], null);
                    } else {
                        token = new Token(Token.Type.KEY_VALUE_PAIR, parts[i - 1], parts[i + 1]);
                    }
                    tokens.add(token);
                    i++;
                }
                default -> {
                    int relIndexToNextRightBrace = nextPart(parts, "}", i);
                    int relIndexToNextEquals = nextPart(parts, "=", i);
                    int relIndexToPriorLeftBrace = priorPart(parts, "{", i);

                    if (relIndexToPriorLeftBrace == 1 && (relIndexToNextRightBrace < relIndexToNextEquals)) {
                        String[] subsetParts = Arrays.copyOfRange(parts, i, i + relIndexToNextRightBrace);
                        String value = String.join(",", subsetParts);
                        Token token = new Token(Token.Type.VALUE_SERIES, null, value);
                        tokens.add(token);
                        i += relIndexToNextRightBrace-1;
                    }
                }
            }
        }
        return tokens;
    }

    private int nextRightBrace(Token[] tokens, int startIndex) {
        int depth = 0;
        for (int i = startIndex; i < tokens.length; i++) {
            if (tokens[i].type() == Token.Type.LEFT_BRACE) {
                depth++;
            } else if (tokens[i].type() == Token.Type.RIGHT_BRACE) {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        logger.warning("no matching right brace found for left brace at index " + startIndex);
        return -1;
    }

    private int nextPart(String[] parts, String part, int startIndex) {
        int dist = 0;
        for (int i = startIndex; i < parts.length; i++) {
            if (parts[i].equals(part)) {
                return dist;
            }
            dist++;
        }
        logger.warning("no matching next part found for part '" + part + "' at index " + startIndex + " (searched up to " + dist + " parts)");
        return -1;
    }

    private int priorPart(String[] parts, String part, int startIndex) {
        int dist = 0;
        for (int i = startIndex; i >= 0; i--) {
            if (parts[i].equals(part)) {
                return dist;
            }
            dist++;
        }
        logger.warning("no matching prior part found for part '" + part + "' at index " + startIndex);
        return -1;
    }

    private void appendChildrenRecursively(Document document, Element parent, Token[] tokens, int startIndex, int endIndex) {
        //begins with starting index at 0, but can be recursively called with a different starting index
        for (int i = startIndex; i < endIndex; i++) {
            Token token = tokens[i]; //token is the current token being processed
            if (token.type() == Token.Type.KEY_VALUE_PAIR) {
                //if the token is a key value pair, add it as an attribute to the parent element
                parent.setAttribute(token.key(), token.value());
            } else if (token.type() == Token.Type.LEFT_BRACE) {
                int rightBraceIndex = nextRightBrace(tokens, i);
                if (rightBraceIndex == -1) {
                    logger.warning("no matching right brace found for left brace at index " + i);
                    continue;
                }
                //if the token is a left brace...
                if(tokens[i+1].type() == Token.Type.VALUE_SERIES) {
                    // and if the next token is a value series, add it as an attribute to the parent element
                    parent.setAttribute(token.key(), tokens[i+1].value());
                    i++; //increment i to skip the value series token
                    continue;
                }
                logger.info(token.toString());
                Element child = document.createElement(token.key());
                //otherwise, create a new element with the key of the left brace token...
                parent.appendChild(child);
                // and add it as a child to the parent element
                appendChildrenRecursively(document, child, tokens, i + 1, rightBraceIndex);
                //then recursively call this method with the new element as the parent,
                // the next token as the start index,
                // and the right brace index as the end index
                i = rightBraceIndex-1; //set i to the right brace index to skip the children of the new element
            }
            //do nothing if the token is a right brace as xml handles its own closing tags
        }
    }

    private Document parseTokens(List<Token> tokens) {
        Token[] tokenArray = tokens.toArray(new Token[0]);
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            if(tokenArray[0].type()!= Token.Type.LEFT_BRACE) {
                logger.warning("first token is not a left brace");
            }
            String rootTagName = tokenArray[0].key();
            Element root = document.createElement(rootTagName);
            document.appendChild(root);
            int endIndex = nextRightBrace(tokenArray, 0);
            appendChildrenRecursively(document, root, tokenArray, 1, endIndex);
            return document;
        } catch (ParserConfigurationException e) {
            logger.severe("parser configuration exception:\n" + e.getMessage());
            e.printStackTrace();
        }
        logger.warning("returning null document");
        return null;
    }

    private void saveDocumentToXML(String outputDirectoryString) {
        try {
            File outputDirectory = new File(outputDirectoryString);
            if(!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            File outputFile = new File(outputDirectoryString + "/" + outputFileString);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            DOMSource domSource = new DOMSource(parsedData);
            StreamResult streamResult = new StreamResult(outputFile);
            transformer.transform(domSource, streamResult);
        } catch (TransformerConfigurationException e) {
            logger.warning("TransformerConfigurationException: " + e.getMessage());
        } catch (TransformerException e) {
            logger.warning("TransformerException: " + e.getMessage());
        }
    }
}
