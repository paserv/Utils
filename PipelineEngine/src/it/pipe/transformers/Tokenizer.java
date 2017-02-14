package it.pipe.transformers;

import it.pipe.core.Transformer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class Tokenizer extends Transformer {

	public Tokenizer() {
		super();
	}

	public Tokenizer(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public ArrayList<String> getOutput(ArrayList<String> input) {

		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
		ArrayList<String> result = new ArrayList<String>();

		Iterator<String> it = input.iterator();
		while (it.hasNext()){
			String currInput = it.next();
			//System.out.println(currInput);
			StringReader reader = new StringReader(currInput);
			

			try {
				TokenStream stream = analyzer.tokenStream(null, reader);
				//				CharArraySet stopSet = new CharArraySet(Version.LUCENE_41, this.stopwords, true);
				//				StopFilter stopFil = new StopFilter(Version.LUCENE_41, stream, stopSet);
				//				stopFil.reset();
				stream.reset();
				while(stream.incrementToken()) {
					//System.out.println(stream.getAttribute(CharTermAttribute.class).toString());
					result.add(stream.getAttribute(CharTermAttribute.class).toString());
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}


		}

		return result;
	}

}
