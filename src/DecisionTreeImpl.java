import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
  private DecTreeNode root;
  //ordered list of class labels
  private List<String> labels; 
  //ordered list of attributes
  private List<String> attributes; 
  //map to ordered discrete values taken by attributes
  private Map<String, List<String>> attributeValues; 
  
  /**
   * Answers static questions about decision trees.
   */
  DecisionTreeImpl() {
    // no code necessary this is void purposefully
  }

  /**
   * Build a decision tree given only a training set.
   * 
   * @param train: the training set
   */
  DecisionTreeImpl(DataSet train) {

    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    
    // Create decisions tree
    this.root = this.DecisionTreeLearning(train.instances, this.attributes, new ArrayList<Instance>(), null);
  }

  @Override
  public String classify(Instance instance) {
	  DecTreeNode current = root;
	  boolean continueSearch = true;
	  
	  // Searches tree until reaches a terminal node
	  while (continueSearch) {
		  int attrIndex = this.getAttributeIndex(current.attribute);
		  String value = instance.attributes.get(attrIndex);
		  
		  // Searches for the value that the current instance has 
		  for (DecTreeNode childNode : current.children) {
			  if (childNode.parentAttributeValue.equals(value)) {
				  current = childNode;
				  break;
			  }
		  }
		  
		  continueSearch = (current.terminal == false);
	  }
	  
	  String label = current.label;
	  return label;
  }

  @Override
  public void rootInfoGain(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    
    double entropy = Info(train.instances);
    
	for (String attr : attributes) {
		double attrImportance = Importance(entropy, train.instances, attr);
		
		System.out.println(String.format("%s %.5f", attr, attrImportance));
	}	
  }
  
  @Override
  public void printAccuracy(DataSet test) {
    int correctNumber = 0;
    
    for (Instance currInstance :  test.instances) {
    	String outputLabel = classify(currInstance);
    	
    	if (outputLabel.equals(currInstance.label)) {
    		correctNumber++;
    	}
    }
    double accuracy = (double) correctNumber / test.instances.size();
    System.out.println(String.format("%.5f", accuracy));
  }
    /**
   * Build a decision tree given a training set then prune it using a tuning set.
   * ONLY for extra credits
   * @param train: the training set
   * @param tune: the tuning set
   */
  DecisionTreeImpl(DataSet train, DataSet tune) {

    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    // TODO: add code here
    // only for extra credits
  }
  
  @Override
  /**
   * Print the decision tree in the specified format
   */
  public void print() {

    printTreeNode(root, null, 0);
  }

  /**
   * Prints the subtree of the node with each line prefixed by 4 * k spaces.
   */
  public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append("    ");
    }
    String value;
    if (parent == null) {
      value = "ROOT";
    } else {
      int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
      value = attributeValues.get(parent.attribute).get(attributeValueIndex);
    }
    sb.append(value);
    if (p.terminal) {
      sb.append(" (" + p.label + ")");
      System.out.println(sb.toString());
    } else {
      sb.append(" {" + p.attribute + "?}");
      System.out.println(sb.toString());
      for (DecTreeNode child : p.children) {
        printTreeNode(child, p, k + 1);
      }
    }
  }

  /**
   * Helper function to get the index of the label in labels list
   */
  private int getLabelIndex(String label) {
    for (int i = 0; i < this.labels.size(); i++) {
      if (label.equals(this.labels.get(i))) {
        return i;
      }
    }
    return -1;
  }
 
  /**
   * Helper function to get the index of the attribute in attributes list
   */
  private int getAttributeIndex(String attr) {
    for (int i = 0; i < this.attributes.size(); i++) {
      if (attr.equals(this.attributes.get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
   */
  private int getAttributeValueIndex(String attr, String value) {
    for (int i = 0; i < attributeValues.get(attr).size(); i++) {
      if (value.equals(attributeValues.get(attr).get(i))) {
        return i;
      }
    }
    return -1;
  }
  
  /**
   * Create a decision tree based on provided dataset 
   * @param examples dataset 
   * @param attributes dataset's attributes
   * @param parentExamples the dataset that the current dataset was created from
   * @param parentAttributeValue the attribute that the current dataset was created by
   * @return
   */
  private DecTreeNode DecisionTreeLearning(List<Instance> examples, List<String> attributes, List<Instance> parentExamples, String parentAttributeValue) {
	DecTreeNode node;
	// Check if we have no instances left 
	if (examples.isEmpty()) {
		node = PluralityLabel(parentExamples, parentAttributeValue);
	}
	// Check if all instances have the same label
	else if (IsAllSameLabel(examples)) {
		String label = examples.get(0).label;
		node = new DecTreeNode(label, null, parentAttributeValue, true);
	}
	// Check if we have attributes 
	else if (attributes.isEmpty()) {
		node = PluralityLabel(examples, parentAttributeValue);
	} else {
		// Calculate dataset entropy 
		double entropy = Info(examples);
		
		String attribute = null;
		double maxImportance = 0;
		
		// Find the most important attribute 
		for (String attr : attributes) {
			double importance = Importance(entropy, examples, attr);
			
			if ((attribute == null) || (importance > maxImportance)) {
				maxImportance = importance;
				attribute = attr;
			}
		}
		
		// Create split node
		DecTreeNode tree = new DecTreeNode(null, attribute, parentAttributeValue, false);
		
		int index = this.getAttributeIndex(attribute);
		
		List<String> newAttributes = new ArrayList<String>(attributes);
		newAttributes.remove(attribute);

		// Create decision tree for each sub-dataset
		for (String value : this.attributeValues.get(attribute)) {
			List<Instance> childExamples = new ArrayList<Instance>();
			
			for (Instance current : examples) {
				String instanceValue = current.attributes.get(index);
				if (instanceValue.equals(value)) {
					childExamples.add(current);
				}
			}
			
			DecTreeNode subTree = this.DecisionTreeLearning(childExamples, newAttributes, examples, value);
			tree.addChild(subTree);
		}
		
		node = tree;
	}
	
	return node;
  }

  /**
   * Calculate Information Gain for attribute
   * @param entropy dataset entropy
   * @param examples dataset
   * @param attr attribute
   * @return
   */
  private double Importance(double entropy, List<Instance> examples, String attr) {
	int attrIndex = this.getAttributeIndex(attr);
	
	HashMap<String, List<Instance>> valueInstances = new HashMap<String, List<Instance>>();
	
	// Create sub-datasets of each attribute value
	for (Instance current : examples) {
		String value = current.attributes.get(attrIndex);
		
		if (!valueInstances.containsKey(value)) {
			valueInstances.put(value, new ArrayList<Instance>());
		}
		
		List<Instance> instances = valueInstances.get(value);
		instances.add(current);
	}
	
	double remainder = 0;
	
	// Calculate the information per value and update the remainder
	for (String value : valueInstances.keySet()) {
		List<Instance> instances = valueInstances.get(value);
		
		double info = Info(instances);
		double percentage = ((double)instances.size() / examples.size()); 
		remainder += percentage * info;
	}
	
	double informationGain = entropy - remainder;
	return informationGain;
  }

  /**
   * Calculate the Info for a dataset
   * @param examples dataset
   * @return
   */
  private double Info(List<Instance> examples) {
	  double info = 0;
	  
	  HashMap<String, Integer> labels = new HashMap<String, Integer>();
	  
	  // Calculate instances per label
	  for (Instance currInstance : examples) { 
		  int count = 0;
		  String label = currInstance.label;
		  
		  if (labels.containsKey(label)) {
			  count = labels.get(label);
		  }
		  
		  labels.put(label, count + 1);
	  }
	  
	  // Calculate information for dataset
	  for (String label : labels.keySet()) {
		  double percentage = ((double) labels.get(label)) / (examples.size());
			
			if (percentage > 0) {
				info += -1 * percentage * (Math.log(percentage) / Math.log(2));
			}
	  }
	  
	  return info;
  }
  
  /**
   * Check if all instances in dataset have the same classification
   * @param examples dataset
   * @return
   */
  private boolean IsAllSameLabel(List<Instance> examples) {
	String label = examples.get(0).label;
	
	// Scan all instances for a different label
	for (Instance current : examples) {
		String currentLabel = current.label;
		if (!currentLabel.equals(label)) {
			return false;
		}
	}
	
	return true;
  }
	
  /**
   * Split dataset by the label with the majority of instances 
   * @param examples dataset
   * @param parentAttributeValue the parent attribute of the dataset
   * @return
   */
  private DecTreeNode PluralityLabel(List<Instance> examples, String parentAttributeValue) {
	HashMap<String, Integer> labels = new HashMap<String, Integer>();
	
	// Counts the number of instances per label
	for (Instance current : examples) {
		String label = current.label;
		int count = 0;
		
		if (labels.containsKey(label)) {
			count = labels.get(label);
		}
		
		labels.put(label, count + 1);
	}
	
	int max = 0;
	String outputLabel = null;
	
	// Finds the label with majority of instances 
	for (String currentLabel : labels.keySet()) {
		int count = labels.get(currentLabel);
		
		if ((outputLabel == null) || (count > max)) {
			max = count;
			outputLabel = currentLabel;
		}
	}
	
	// Creates terminal node with the label of majority instances  
	DecTreeNode node = new DecTreeNode(outputLabel, null, parentAttributeValue, true);
	return node;
  }
}
