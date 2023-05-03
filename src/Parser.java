import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
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
            logger.info(
                "\n" + "=".repeat(50) +
                "\n \t\t filename: " + filename +
                "\n" + "=".repeat(50)
            );
        }
        this.outputFileString = filename;
        String data = fileToText(inputFileString);
        data = cleanText(data);
        List<Token> tokens = tokenize(data);
        XmlWriter xmlWriter = new XmlWriter();
        try {
            this.parsedData = xmlWriter.convertToXml(tokens.toArray(new Token[0]));
            this.dataString = parsedData.toString();
            saveDocumentToXML(parsedData, dirName, outputFileString);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
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
//            logger.info(parts[i]);
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

    public void saveDocumentToXML(Document parsedData, String outputDirectory, String outputFileString) throws TransformerException {
        // Create the output file object
        File outputFile = new File(outputDirectory, outputFileString);
        if(!outputFile.exists()) {
            try {
                outputFile.mkdirs();
            } catch (SecurityException e) {
                logger.warning("could not write " + outputFileString + " due to security exception!");
            }
        }

        // Configure the transformer to output the XML in a readable format
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        // Create the result object to write the XML to the output file
        StreamResult result = new StreamResult(outputFile);

        // Transform the parsedData document to XML and write it to the output file
        transformer.transform(new DOMSource(parsedData), result);
    }

}
