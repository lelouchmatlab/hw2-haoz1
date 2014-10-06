package edu.cmu.deiis.model;

import java.util.List;
import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMARuntimeException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import com.aliasi.chunk.Chunker;
import com.aliasi.util.AbstractExternalizable;

import edu.cmu.deiis.types.Annotation;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;


public class CrfAE extends JCasAnnotator_ImplBase {
    
	private AbstractSequenceClassifier<CoreLabel> classifier;
	
	/**
	 * Initialize CRF model. It reads in the model file and initialize AbstractSequenceClassifier Object for later useage.
	 */
	public void initialize(UimaContext aContext){
		
		String model_file = (String) aContext.getConfigParameterValue("Param_ModelFile");
		try {
			classifier = CRFClassifier.getClassifier(model_file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		   throw new UIMARuntimeException(e);
		} 
	}
	
	
	/**
	 * Perform CRF based statistical name entity recognition on input sentences.
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
		List<List<CoreLabel>> out = classifier.classify(sentence_content);
		for (List<CoreLabel> sentence : out) {
			int i=0;
			while(i<sentence.size()){
				CoreLabel word = sentence.get(i);
				String label = word.get(CoreAnnotations.AnswerAnnotation.class);
				if(label.equals("O")){
					i++;
				}
				else if(label.equals("B")){
					int ner_begin = word.beginPosition();
					int ner_end = word.endPosition();
					i++;
					while(i<sentence.size() && sentence.get(i).get(CoreAnnotations.AnswerAnnotation.class).equals("I")){
						ner_end = sentence.get(i).endPosition();
						i++;
					}
					String NER = sentence_content.substring(ner_begin,ner_end);
					int shift = 0;
					for(int j=0;j<sentence_content.length();j++){
						if(sentence_content.charAt(j)==' '){
							shift++;
						}
						if(sentence_content.substring(j,j+NER.length()).equals(NER)){
							break;
						}
					}
					String[] NER_words = NER.split(" ");
					Annotation annotation = new Annotation(aJCas);
					annotation.setBegin(ner_begin-shift);
					annotation.setEnd(ner_end-shift-1-(NER_words.length-1));
					annotation.setNameEntity(NER);
					annotation.setCasProcessorId("Crf");
					annotation.setConfidence(0.8);
					annotation.setSentenceID(sentence_id);
					annotation.addToIndexes();
				}
	
			}
	    }
	}

}
