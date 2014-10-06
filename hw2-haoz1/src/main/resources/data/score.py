import sys

def main(ref,hyp):
  
  truth_file = open(ref,"r");
  truth = truth_file.readlines();
  num_truth = len(truth)
  
  NER_file = open(hyp,"r");
  NER = NER_file.readlines();
  num_NER = len(NER)
  
  truth_dict = {}
  for line in truth:
    array = line.split('|')
    key = array[0]+array[1]
    value = array[2];
    truth_dict[key]=value
    #print key, value
  
  counter = 0
  
  for line in NER:
    array = line.split('|')
    key = array[0]+array[1]
    value = array[2];
    if(truth_dict.has_key(key) and truth_dict[key]==value):
      counter = counter +1
  print counter    
  prescision = (counter+0.0)/num_NER
  recall = (counter+0.0)/num_truth
  #print prescision, recall
  f_score = 2*(prescision*recall)/(prescision+recall)
  
  print prescision, recall, f_score
  
    
    
    
if __name__=="__main__":
  ref = sys.argv[1]
  hyp = sys.argv[2]
  main(ref,hyp)
    
