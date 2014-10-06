import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.StringReader;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;


public class Dataprepare {
	
	public static void main(String[] args) throws Exception{
		String InputText = "src/main/resources/data/mysample.in";
		String InputLabel = "src/main/resources/data/mysample.out";
		String OutputTrain = "src/main/resources/data/NER_train.tsv";
		BufferedReader Text_in = new BufferedReader(new FileReader(new File(InputText)));
		BufferedReader Label_in = new BufferedReader(new FileReader(new File(InputLabel)));
		BufferedWriter Train_out = new BufferedWriter(new FileWriter(OutputTrain));;
		HashMap<String,String> ID2content = new HashMap<String,String>();
		HashMap<String,List<String>> ID2label = new HashMap<String,List<String>>();
		
		int countL = 0;
		while(Label_in.ready()){
			String docLabel = Label_in.readLine();
			countL++;
			String[] temp = docLabel.split("\\|");
			System.out.println(temp[2]);
			if(ID2label.containsKey(temp[0])){
				ID2label.get(temp[0]).add(temp[2]);
			}
			else{
				ID2label.put(temp[0], new ArrayList<String>());
				ID2label.get(temp[0]).add(temp[2]);
			}
		}
		int countB = 0;
		
		while(Text_in.ready()){
			String docText = Text_in.readLine();
			int start = 0;
			while(docText.charAt(start)!=' '){
				start++;
			}
			String sentence_id = docText.substring(0,start);
			String sentence_content = docText.substring(start+1);
			PTBTokenizer<Word> content_tokenizer=PTBTokenizer.newPTBTokenizer(new BufferedReader(new StringReader(sentence_content)));	
			List<String> content_buf = new ArrayList<String>();
	    	while (content_tokenizer.hasNext()) {
	    	    Word nextToken=content_tokenizer.next();
	    	    content_buf.add(nextToken.toString());
	    	}
	    	for(String s : content_buf){
	    		System.out.print(s+" * ");
	    	}
	    	
			List<String> NER = ID2label.get(sentence_id);
			
			if(NER==null){
				System.out.println("EMPTY");
			    int i=0;
			    while(i<content_buf.size()){
			    	Train_out.write(content_buf.get(i)+"\tO\n");
			    	i++;
			    }
			}
			else{
			    List<List<String>> NER_tokens = new ArrayList<List<String>>();
			    //countB = countB + (NER==null? 0 :NER.size());
			    for(String ner : NER){
			    	PTBTokenizer<Word> ner_tokenizer=PTBTokenizer.newPTBTokenizer(new BufferedReader(new StringReader(ner)));
			    	List<String> buf = new ArrayList<String>();
			    	while (ner_tokenizer.hasNext()) {
			    	    Word nextToken=ner_tokenizer.next();
			    	    buf.add(nextToken.toString());
			    	}
			    	NER_tokens.add(buf);	    	
			    }
			    int i=0;
			    while(i<content_buf.size()){
			    	if(!NER_tokens.isEmpty()){
			    		List<String> fist_ner = NER_tokens.get(0);
			    		while(i<content_buf.size() && !content_buf.get(i).startsWith(fist_ner.get(0))){
			    			Train_out.write(content_buf.get(i)+"\tO\n");
			    			i++;
			    		}
			    		int j= 0;
			    		
			    		Train_out.write(fist_ner.get(j)+"\tB\n");
			    		i++;
			    		for(j=1;j<fist_ner.size();j++){
			    			Train_out.write(fist_ner.get(j)+"\tI\n");
			    			i++;
			    		}
			    		NER_tokens.remove(0);
			    		countB++;
			    	}
			    	else{
			    		Train_out.write(content_buf.get(i)+"\tO\n");
			    		i++;
			    	}
			    }
			    if(!NER_tokens.isEmpty()){
			    	System.out.println(sentence_id);
			    	//throw (new Exception());
			    }
			}
		}
		
		Train_out.flush();
		System.out.println(countB);
	}

}
