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
	
	
	/**
	 * Initialize ColletionConsumer. 
	 * <p>
	 * In this method, it simply open the output file and prepare to write result
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
	 * Process the function of collection consumer for each CAS.
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
	    
	    
	    while(it.hasNext()){
	    	Annotation cur = (Annotation) it.next();
	    	
	    	String ID = cur.getSentenceID();
	    	String NER = cur.getNameEntity();
	    	Integer start = cur.getBegin();
	    	Integer end = cur.getEnd();   
	    	try {
				out.write(ID+"|"+start+" "+end+"|"+NER+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	    }
	    
	    try {
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * The function to be called when it finish the work. It closes the output file stream. 
	 */
	public void collectionProcessComplete(){	
	    try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
