/**
 * CollectionConsumer is the class which is responsible for collecting annotation made 
 * in analysis engine and then writing output files. The methods it includes are the following:
 * <ul>
 * <li> Initialization
 * <li> ProcessCas
 * <li>collectionProcessComplete
 * </ul>
 * 
 * @author  Hao Zhang
 */


package edu.cmu.deiis.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.apache.uima.UIMARuntimeException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceProcessException;

import edu.cmu.deiis.types.Annotation;

public class CollectionConsumer extends CasConsumer_ImplBase {
	public BufferedWriter out;
	public static final String PARAM_OUTPUTFILE = "Param_OutputFile";
	public static int Hmmcounter = 0;
	public static int Dictcounter = 0;
	public static int Crfcounter = 0;
	
	/**
	 * Initialize ColletionConsumer. 
	 * <p>
	 * In this method, it simply open the output file and prepare to write result.
	 */
	public void initialize(){
		try {
			String filename = ((String) getConfigParameterValue(PARAM_OUTPUTFILE)).trim();
			System.out.println(filename);
			File file = new File(filename);
			file.createNewFile();
			out = new BufferedWriter(new FileWriter(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * PricessCas methods combine annotations from HMM based analysis engine and Dictionary based analysis engine
	 * and CRF based analysis engine.
	 * 
	 * @param  aCAS      The CAS to be processed
	 */
	
	@Override
	public void processCas(CAS aCAS) throws ResourceProcessException {
		// TODO Auto-generated method stub
		JCas jcas;
		  try {
		    jcas = aCAS.getJCas();
		  } catch (CASException e) {
		    throw new ResourceProcessException(e);
		 }
	    FSIterator it = jcas.getAnnotationIndex(Annotation.type).iterator();
	    
	    HashMap<String,String> Hmmmap = new HashMap<String,String>();
	    HashMap<String,String> Dictmap = new HashMap<String,String>();
	    HashMap<String,String> Crfmap = new HashMap<String,String>();
	    try {
		    while(it.hasNext()){
		    	Annotation cur = (Annotation) it.next();
		    	
		    	String ID = cur.getSentenceID();
		    	String NER = cur.getNameEntity();
		    	String category = cur.getCasProcessorId();
		    	Integer start = cur.getBegin();
		    	Integer end = cur.getEnd();
		    	StringBuffer key = new StringBuffer();
		    	key.append(ID+"|"+start+" "+end+"|");
		   
		    	if(category.equals("Hmm")){
		    		Hmmmap.put(key.toString(), NER);
		    	}
		    	else if(category.equals("Dict")){
		    		Dictmap.put(key.toString(), NER);
		    	}
		    	else{
		    		Crfmap.put(key.toString(), NER);
		    	}
		    }
		    /*
		    Hmmcounter = Hmmcounter + Hmmmap.size();
		    Dictcounter = Dictcounter + Dictmap.size();
		    Crfcounter = Crfcounter + Crfmap.size();
	        */
		    int Dict_noin_Hmm = 0;
		    
		    for(String dict_key : Dictmap.keySet()){
		    	if(!Hmmmap.containsKey(dict_key)){
		    		Dict_noin_Hmm++;
		    		Hmmmap.put(dict_key, Dictmap.get(dict_key));
		    	}
		    }
		    int Crf_noin_unioHmmDict = 0;
		    for(String crf_key : Crfmap.keySet()){
		    	if(!Hmmmap.containsKey(crf_key)){
		    		Crf_noin_unioHmmDict++;
		   			Hmmmap.put(crf_key, Crfmap.get(crf_key));
		   		}
		    }
		    for(String hmm_key : Hmmmap.keySet()){
				out.write(hmm_key+Hmmmap.get(hmm_key)+"\n");
		    }	
		    
		    out.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new UIMARuntimeException(e);
		}
	}
	/**
	 * The function to be called after AAE finish the work. It closes the output file stream. 
	 */
	public void collectionProcessComplete(){	
	    try {
			out.close();
		} catch (Exception e) {
			throw new UIMARuntimeException(e);
		}
	}
	
}
