import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class Parser {
    //this class will parse text files and build objects which consist of <key> = <value> entries
    //splitting on "{"'s and closing on "}"'s, it should be able to parse every hoi4 file
    static Logger logger = Logger.getLogger(Parser.class.getName());
    private final String filestring;
    private final Map<String, ValueInstance> parsedData;

    public Parser(String filestring) {
        this.filestring = filestring;
        this.parsedData = parseToDict();
    }

    public String getFilestring() {
        return filestring;
    }

    public Map<String, ValueInstance> getParsedData() {
        return parsedData;
    }

    private Map<String, ValueInstance> parseToDict() {
        String data = fileToText(filestring);
        data = cleanText(data);
        List<Token> tokens = tokenize(data);
        return parseTokens(tokens);
    }

    private static List<Token> tokenize(String text) {
        List<Token> tokens = new ArrayList<>();
        String[] parts = text.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            String partKey = parts[i];
            if (partKey.equals("{")) {
                tokens.add(new Token(Token.Type.LEFT_BRACE, null, null));
                logger.warning("left brace found at start of tokenization, no key found");
            } else if (partKey.equals("}")) {
                tokens.add(new Token(Token.Type.RIGHT_BRACE, null, null));
            } else if (i < parts.length - 3 && parts[i + 1].equals("=")) {
                if(parts[i + 2].equals("{")) {
                    tokens.add(new Token(Token.Type.LEFT_BRACE, partKey, null));
                } else {
                    tokens.add(new Token(Token.Type.KEY_VALUE_PAIR, partKey, parts[i + 2]));
                }
                i += 2;
            }
        }
        return tokens;
    }

    private static Map<String, ValueInstance> parseTokens(List<Token> tokens) {
        Map<String, ValueInstance> result = new LinkedHashMap<>();
        Stack<Map<String, ValueInstance>> mapStack = new Stack<>();
        mapStack.push(result);
        Stack<Token> tokenStack = new Stack<>();
        for (Token token : tokens) {
            switch (token.type()) {
                case LEFT_BRACE -> {
                    tokenStack.push(token);
                    Map<String, ValueInstance> newMap = new LinkedHashMap<>();
                    mapStack.push(newMap);
                }
                case RIGHT_BRACE -> {
                    Map<String, ValueInstance> catchMap = mapStack.pop();
                    ValueInstance catchValue = new ValueInstance(catchMap);
                    Token catchToken = tokenStack.pop();
                    mapStack.peek().put(catchToken.key(), catchValue);
                }
                case KEY_VALUE_PAIR -> mapStack.peek().put(token.key(), token.toValueInstance());
            }
        }
        return result;
    }


    private static String cleanText(String text) {
        text = text.replace("\t", " ");
        text = text.replace("}", " } ");
        text = text.replace("{", " { ");
        text = text.replace("=", " = ");
        return text.trim().replaceAll("\\s+", " ");
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

    private static String removeComments(String text) {
        return text.replaceAll("#.*", "");
    }
}
