package dinner.learn;

import dinner.model.Decision;
import dinner.model.Example;
import dinner.model.Attribute;
import org.w3c.dom.Attr;

import java.util.*;

public class DecisionTree {

    public DecisionTree(List<Example> examples, List<Attribute> attributes) {
        createDecisionTree(examples, attributes);
    }

    public static void createDecisionTree(List<Example> examples, List<Attribute> attributes) {
        /*
        first call getMostImportantAttribute to figure out which attribute we should be looking at.

        then call categorizeExamplesByAttributeValue() to get each of the different values for the attribute with their
        examples

        go thru each value returned, check the examples,
            1. if the examples have the same decision tree, stop, this branch is finished
            2. if the examples don't have the same decision, remove the current best attribute, call getMostImportantAttribute
                and make a recursive call based on that?
            3. not quite sure what to do in this case
            4. if no attributes left, but both positive and negative outcomes-> you have noise and have to do something
                with it, not sure what yet tho :D
         */
        Attribute mostImportant = getMostImportantAttribute(examples, attributes);
        Map<Object, List<Example>> values = categorizeExamplesByAttributeValue(examples, mostImportant);
        System.out.println("Node: " + mostImportant);
        System.out.println();
        for(Object branch: values.keySet()) {
            createDecisionTreeHelper(values.get(branch), attributes, mostImportant.toString(), branch.toString());
        }
    }

    private static void createDecisionTreeHelper(List<Example> examples, List<Attribute> attributes, String parent, String incomingBranchName) {
        Attribute mostImportant = getMostImportantAttribute(examples, attributes);
        Map<Object, List<Example>> values = categorizeExamplesByAttributeValue(examples, mostImportant);
        Map<Object, Integer> decs = new HashMap<>();
        System.out.println("Parent: " + parent);
        int i = 0;
        for(Example examp: examples){
            i++;
        }
        for(Map.Entry<Object, List<Example>> entry: values.entrySet()){
            for(Example entr: entry.getValue()){
                Decision decision = entr.getDecision();
                decs.put(decision, entry.getValue().size());
            }
        }
        if(decs.size() > 1){
            System.out.println("Branch: " + incomingBranchName);
            createDecisionTree(examples, attributes);
        }
        else{
            for(Map.Entry<Object, Integer> entry: decs.entrySet()) {
                System.out.println("Branch: " + incomingBranchName);
                System.out.println("Decision (" + i + "): " + entry.getKey());
                System.out.println();
            }
        }

    }

    public static Attribute getMostImportantAttribute(List<Example> examples, List<Attribute> attributes) {
        /* Identifies and returns the attribute that decides the most examples in `examples`
         */
        List<Double> numDecided = new ArrayList<>();
        List<Attribute> attributesDecided = new ArrayList<Attribute>();
        int maxDecisions = 0;
        int i = 0;
        for(Attribute attribute: attributes){
            Map<Object, Map<Decision, Integer>> decisions = generateDecisionMap(examples, attribute);
            double decided = getNumOutcomesDecided(decisions);
            numDecided.add(decided);
            attributesDecided.add(attribute);
            if(numDecided.get(maxDecisions) < numDecided.get(i)){
                maxDecisions = i;
            }
            i++;
        }
        return attributesDecided.get(maxDecisions);
    }

    private static Map<Object, List<Example>> categorizeExamplesByAttributeValue(List<Example> examples, Attribute attribute) {
        Map<Object, List<Example>> attributeExampleMap = new TreeMap<Object, List<Example>>();
        Object attributeValue;
        List<Example> valueExamples;
        for(Example example: examples){
            attributeValue = example.getAttributeValue(attribute);
            if(attributeExampleMap.containsKey(attributeValue)){
                valueExamples = attributeExampleMap.get(attributeValue);
                valueExamples.add(example);
            } else {
                valueExamples = new ArrayList<>();
                valueExamples.add(example);
                attributeExampleMap.put(attributeValue, valueExamples);
            }
        }
        return attributeExampleMap;
    }

    private static Map<Object, Map<Decision, Integer>> generateDecisionMap(List<Example> examples, Attribute attribute) {
        Map<Object, Map<Decision, Integer>> decisionMap = new HashMap<>();
        Map<Decision, Integer> decisionCountMap;
        Object attributeValue;
        Decision decision;

        for(Example example: examples) {
            attributeValue = example.getAttributeValue(attribute);
            decision = example.getDecision();
            if(decisionMap.containsKey(attributeValue)) {
                decisionCountMap = decisionMap.get(attributeValue);
                if(decisionCountMap.containsKey(decision)) {
                    decisionCountMap.put(decision, decisionCountMap.get(decision)+1);
                } else {
                    decisionCountMap.put(decision, 1);
                }
            } else {
                decisionCountMap = new HashMap<>();
                decisionCountMap.put(decision, 1);
                decisionMap.put(attributeValue, decisionCountMap);
            }
        }

        return decisionMap;
    }
    
    private static double getNumOutcomesDecided(Map<Object, Map<Decision, Integer>> decisionMap) {
        int numDecided = 0;
        for (Object attrValue : decisionMap.keySet()) {
            if (decisionMap.get(attrValue).keySet().size() == 1) {
                numDecided++;
            }
        }
        return numDecided;
    }
}
