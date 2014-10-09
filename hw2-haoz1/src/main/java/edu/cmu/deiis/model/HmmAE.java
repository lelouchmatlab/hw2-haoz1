package edu.cmu.deiis.model;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.examples.cpe.SimpleRunCPE;
import org.apache.uima.jcas.JCas;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;

import edu.cmu.deiis.types.Annotation;

public class HmmAE extends JCasAnnotator_ImplBase {
    
	
	public Chunker chunker = null;
	public File modelFile = null;
	/**
	 * Initialize HMM model. It reads in the model file and initialize Chunker Object for later useage.
	 */
	public void initialize(UimaContext aContext){
		
		try {
		    chunker = (Chunker) AbstractExternalizable.readResourceObject(HmmAE.class, (String) aContext.getConfigParameterValue("Param_ModelFile"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	
	/**
	 * Perform HMM based statistical name entity recognition on input sentences.
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
		String[] words = sentence_content.split(" ");
		Chunking chunking = chunker.chunk(sentence_content);
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
			annotation.setCasProcessorId("Hmm");			
			annotation.setConfidence(ner.score());
			annotation.setSentenceID(sentence_id);
			annotation.addToIndexes();
        }
	}

}
