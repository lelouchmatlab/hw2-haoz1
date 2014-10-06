import sys
from subprocess import call
def main(ref,hyp):
  dict_file = 'temp'
  truth_file = open(ref,"r");
  truth = truth_file.readlines();
  num_truth = len(truth)
  
  NER_file = open(hyp,"r");
  NER = NER_file.readlines();
  num_NER = len(NER)
  
  Dict_file = open(dict_file,"w");
  truth_dict = {}
  for line in truth:
    array = line.split('|')
    key = array[2];
    truth_dict[key]=""
    #print key, value
  
  counter = 0
  NER_dict = {}
  for line in NER:
    array = line.split('|')
    key = array[2];
    NER_dict[key]=""
  
  for line in truth:
    array = line.split('|')
    key = array[2];
    if NER_dict.has_key(key):
       counter = counter+1;
    else:
       Dict_file.write(key)

    
    
if __name__=="__main__":
  ref = sys.argv[1]
  hyp = sys.argv[2]
  main(ref,hyp)
  call(["sort","-u", "temp"])
    
