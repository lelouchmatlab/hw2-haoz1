package edu.cmu.deiis.model;

import java.io.File;
import java.io.IOException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.util.AbstractExternalizable;

import edu.cmu.deiis.types.Annotation;

public class LingpipeAE extends JCasAnnotator_ImplBase {
    
	
	public Chunker chunker = null;
	public File modelFile = null;
	
	public void initialize(UimaContext aContext){
		
		String model_file = (String) aContext.getConfigParameterValue("Param_ModelFile");
		modelFile = new File(model_file);
		try {
			chunker = (Chunker) AbstractExternalizable.readObject(modelFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	
	
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
			String[] NER_words = NER.split(" ");
			String NER_begin = NER_words[0];
			boolean begin_offset = true;
			String NER_end = NER_words[NER_words.length-1];
			boolean end_offset = true;
			for(int i=0;i<words.length;i++){
				if(begin_offset && words[i].equals(NER_begin)){
					begin = begin - i;
					begin_offset = false;
				}
				if(end_offset && words[i].equals(NER_end)){
					end = end - i -1;
					end_offset = false;
					break;
				}
			}
			annotation.setBegin(begin);
			annotation.setEnd(end);
			annotation.setNameEntity(NER);
			annotation.setCasProcessorId("LingPipe");
			annotation.setConfidence(0.8);
			annotation.setSentenceID(sentence_id);
			annotation.addToIndexes();
        }
	}

}
