package drew.corenlp;

import com.google.common.io.Files;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**Recognizes the true case of tokens in text where this information was lost, e.g.,
all upper case text. This is implemented with a discriminative model implemented using a CRF sequence tagger. 
The true case label, e.g., INIT_UPPER is saved in TrueCaseAnnotation. 
The token text adjusted to match its true case is saved as TrueCaseTextAnnotation. */
public class TruecaseExample {

  public static void main(String[] args) throws IOException {
    // creates a CoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, truecase");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    // read some text from the file..
    File inputFile = new File("test/resources/truecase-content.txt");
    String input = Files.toString(inputFile, Charset.forName("UTF-8"));
    String lcInput = input.toLowerCase(); // downcase everything.

    // create an empty Annotation with the downcased text.
    Annotation document = new Annotation(lcInput);

    // run all Annotators on this text
    pipeline.annotate(document);

    // list all the sentences in this document
    // define CoreMap which uses class objects as keys and has values with custom types
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

    // capture the true cased tokens for evaluation.
    List<String> tcTokens = new ArrayList<String>();

    System.out.println("Starting Outpt");
    for (CoreMap sentence : sentences) {
      // traverse words in the current sentence
      // create a CoreLabel having with additional token-specific methods
      for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
        // this is the text of the token
        String text = token.get(TextAnnotation.class);
        String trueCase = token.get(TrueCaseAnnotation.class);
        String trueCaseText = token.get(TrueCaseTextAnnotation.class);
        System.out.printf("input:%s state:%s output:%s\n", text, trueCase, trueCaseText);
        tcTokens.add(trueCaseText);
      }
    }
    System.out.println("Stopping");


    // create an empty Annotation with just the standard text.
    document = new Annotation(input);

    // run all Annotators on this text
    pipeline.annotate(document);
    sentences = document.get(SentencesAnnotation.class);

    // capture the standard tokens for evaluation
    List<String> stdTokens = new ArrayList<String>();

    for (CoreMap sentence : sentences) {
      // traversing the words in the current sentence
      // create another CoreLabel
      for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
        // get text of the token
        String word = token.get(TextAnnotation.class);
        stdTokens.add(word);
      }
    }

    // Calculate error rate
    int match = 0;
    int sz = tcTokens.size();

    System.out.println("Starting");

    for (int i=0; i < sz; i++) {
      String tcToken = tcTokens.get(i);
      String stdToken = stdTokens.get(i);
      if (tcToken.equals(stdToken)) {
        match++;
      }
      else {
        System.out.printf("Truecase mismatch: input:'%s' output:'%s' @ %d\n", stdToken, tcToken, i);
      }
    }

    float errorRate = ((float) sz - match) / sz;
    System.out.println("Error Rate: " + errorRate);

    System.out.println("Ending");
  }
}
