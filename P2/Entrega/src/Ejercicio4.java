import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

public class Ejercicio4 {

    static class CodeLineTokenizer extends Tokenizer {
        private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

        private static final Pattern PATTERN = Pattern.compile("[,;{}()]");

        public CodeLineTokenizer() {
        }

        @Override
        public boolean incrementToken() throws IOException {
            clearAttributes();
            StringBuilder line = new StringBuilder();
            int c;
            while ((c = input.read()) != -1) {
                char ch = (char) c;
                if (ch == '\n') {
                    break;
                }
                line.append(ch);
            }
            if (line.length() > 0) {
                String lineText = line.toString().toLowerCase().trim();
                // Filtra líneas vacías
                if (!lineText.isEmpty()) {
                    // Elimina llaves, comas, paréntesis y punto y coma
                    lineText = PATTERN.matcher(lineText).replaceAll("");
                    if(lineText.length() > 0) {
                        termAtt.append(lineText);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        // Ejemplo de código fuente
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        // Este es un comentario\n" +
                "        System.out.println(\"Hola, mundo!\");\n" +
                "    }\n" +
                "}\n";

        // Crear el analizador personalizado para código fuente
        Analyzer codeAnalyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer source = new CodeLineTokenizer();
                return new TokenStreamComponents(source);
            }
        };

        // Tokenizar y procesar el código fuente
        TokenStream tokenStream = codeAnalyzer.tokenStream("code", new StringReader(code));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.println(charTermAttribute.toString());
        }
        tokenStream.end();
        tokenStream.close();
    }
}
