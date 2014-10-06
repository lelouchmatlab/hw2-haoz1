/**
 * Dictionary analysis engine performs the exact match on input file and find named entity
 * based on a hard-core dictionary.
 *  
 *@author Hao Zhang
 */


package edu.cmu.deiis.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.uima.UIMARuntimeException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

import edu.cmu.deiis.types.Annotation;

public class DictionaryAE extends JCasAnnotator_ImplBase{
    
	
	private MapDictionary<String> dictionary;
	private ExactDictionaryChunker dictionaryChunkerTT;
	private final double CHUNK_SCORE = 1.0;     //Global score for name entity recognized by Dictionary
	
	/**
	 * Initialize dictionary. It simply reads in each line in dictionary file and initialize
	 * an entry for that then finally put it into the dictionary Object.
	 */
	public void initialize(UimaContext aContext){
		
		String model_file = (String) aContext.getConfigParameterValue("Param_ModelFile");
		BufferedReader model_in ;
		dictionary = new MapDictionary<String>();
		try {
			FileReader file = new FileReader(new File(model_file));
			model_in = new BufferedReader(file);
			while(model_in.ready()){
				String name_entity_word = model_in.readLine();
				dictionary.addEntry(new DictionaryEntry<String>(name_entity_word,"NE",CHUNK_SCORE));
			}
			dictionaryChunkerTT = new ExactDictionaryChunker(dictionary,
                                         IndoEuropeanTokenizerFactory.INSTANCE,
                                         true,true);
			
		} catch (Exception e) {
			throw new UIMARuntimeException(e);
		} 
	}
	
	
	/**
	 * Perform exact match in input sentence based on the dictionary. Annotate matched
	 * substring as name entity
	 * 
	 * @param aJCas    The CAS to be processed.
	 */
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
		String docText = aJCas.getDocumentText();
		int start = 0;
		while(docText.charAt(start)!=' '){
			start++;
		}
		
		String sentence_id = docText.substring(0,start);
		String sentence_content = docText.substring(start+1);
		Chunking chunking = dictionaryChunkerTT.chunk(sentence_content);
		for(Chunk ner : chunking.chunkSet()){
			Annotation annotation = new Annotation(aJCas);
			Integer begin = ner.start();
			Integer end = ner.end();
			String NER = sentence_content.substring(ner.start(),ner.end());
			int shift = 0;
			for(int i=0;i<sentence_content.length();i++){
				if(sentence_content.charAt(i)==' '){
					shift++;
				}
				if(sentence_content.substring(i,i+NER.length()).equals(NER)){
					break;
				}
			}
			String[] NER_words = NER.split(" ");
			annotation.setBegin(begin-shift);
			annotation.setEnd(end-shift-1-(NER_words.length-1));
			annotation.setNameEntity(NER);
			annotation.setCasProcessorId("Dict");
			annotation.setConfidence(CHUNK_SCORE);
			annotation.setSentenceID(sentence_id);
			annotation.addToIndexes();
        }
		
	}
	
	
	 

}
