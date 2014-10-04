/**
 * CollectionReader is responsible for converting input file to CAS for analysis engine to process. In it's initialization 
 * method it open the input file stream. Then int getNext function, it simply reads in a line, set it content to CAS text, return that CAS instanse.  
 *
 *@author Hao Zhang
 */


package edu.cmu.deiis.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

public class CollectionReader extends CollectionReader_ImplBase {
    
	
	public static final String PARAM_INPUTFILE = "Param_InputFile";
	public BufferedReader in;
	public int CurrentIndex;
	
	
	/**
	 * Initialize input file reader object.
	 */
	public void initialize(){
		CurrentIndex = 0;
		String InputFile = ((String) getConfigParameterValue(PARAM_INPUTFILE)).trim();
		FileReader file = null;
		try {
		    file = new FileReader(new File(InputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		in = new BufferedReader(file);
	}
	
	
	/**
	 * Get next CAS instances. It reads next line from the input file then initialize CAS.
	 * 
	 * @param  aCAS    The CAS to be processed.
	 */
	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		// TODO Auto-generated method stub
		CurrentIndex++;
		JCas jcas;
	    try {
	      jcas = aCAS.getJCas();
	    } catch (CASException e) {
	      throw new CollectionException(e);
	    }
	    
	    String text = in.readLine();
	    jcas.setDocumentText(text);
	    
	}

	@Override
	public void close() throws IOException {
		in.close();
		// TODO Auto-generated method stub

	}
    /**
     * return the progress of collection processing. (Not implemented in this task)  
     */
	@Override
	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		//return new Progress[] { new ProgressImpl(CurrentIndex, 1 , Progress.ENTITIES) };
	    return null;
	}
	/**
     * Check if there is more CAS need to be processed.  
     */ 
	@Override
	public boolean hasNext() throws IOException, CollectionException {
		// TODO Auto-generated method stub
		return in.ready();
	}

}
