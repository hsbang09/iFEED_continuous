

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ifeed;

//import be.ac.ulg.montefiore.run.jadti.AttributeSet;
//import be.ac.ulg.montefiore.run.jadti.AttributeValue;
//import be.ac.ulg.montefiore.run.jadti.DecisionTree;
//import be.ac.ulg.montefiore.run.jadti.Item;
//import be.ac.ulg.montefiore.run.jadti.ItemSet;
//import be.ac.ulg.montefiore.run.jadti.KnownSymbolicValue;
//import be.ac.ulg.montefiore.run.jadti.SymbolicAttribute;
//import be.ac.ulg.montefiore.run.jadti.SymbolicValue;
//import be.ac.ulg.montefiore.run.jadti.UnknownSymbolicValue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.Random;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.classifiers.Evaluation;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.CSVSaver;
import java.io.File;
import java.awt.BorderLayout;
import java.util.Arrays;
import javax.swing.JFrame;
/**
 *
 * @author Bang
 */
public class DrivingFeaturesGenerator {
    private static DrivingFeaturesGenerator instance = null;
    
    private ArrayList<Architecture> focus; // behavioral
    private ArrayList<Architecture> random; // non_behavioral
    
    private ArrayList<String> candidateFeatures;
    private ArrayList<String> candidateFeatures_names;

    private int[][] dataFeatureMat;
    private double supp_threshold;
    private double confidence_threshold;
    private double lift_threshold;
    
    private ArrayList<DrivingFeature> drivingFeatures;
    private ArrayList<DrivingFeature> userDef;
    

    public void initialize(ArrayList<Architecture> focus, ArrayList<Architecture> random, double supp, double conf, double lift){

        this.focus = focus;
        this.random = random;
        this.supp_threshold=supp;
        this.confidence_threshold=conf;
        this.lift_threshold=lift;
 
        
        userDef = new ArrayList<>();
        drivingFeatures = new ArrayList<>();
        candidateFeatures = new ArrayList<>();
        candidateFeatures_names = new ArrayList<>();
    }

    
    
    private double computeLift (Scheme scheme) {
        
        int count_focus = 0;
        int count_random = 0;
        for (Architecture a:focus) {
            if (scheme.compare(a) == 1) ++count_focus;
        }
        for (Architecture a:random) {
            if (scheme.compare(a) == 1) ++count_random;
        }
        double lift =0;
        if (count_random != 0) lift = (double) ( (double) count_focus/focus.size())/ ( (double) count_random/random.size());
        
        return lift;
    }
    
    private double computeSupport (Scheme scheme) {
        
        int count_data = 0;
        for (Architecture a:focus) {
            if (scheme.compare(a) == 1) count_data++;
        }
        double support = ((double) count_data)/((double)random.size());
        return support;
    }
    
    private double computeConfidenceGivenSelection (Scheme scheme){
        int count_focus=0;
        int count_random=0;
        int focus_size = focus.size();

        for (Architecture a:focus) {
            if (scheme.compare(a) == 1) count_focus++;
        }
        for (Architecture a:random) {
            if (scheme.compare(a) == 1) count_random++;
        }
        double conf = (double) ((double) count_focus)/((double) focus.size());   // confidence of a rule  {goodDesign} -> {feature}

        return conf;
    }
    private double computeConfidenceGivenFeature (Scheme scheme) {
        
//        randomData
//        focusData;        
        int count_focus=0;
        int count_random=0;
        int focus_size = focus.size();

        for (Architecture a:focus) {
            if (scheme.compare(a) == 1) count_focus++;
        }
        for (Architecture a:random) {
            if (scheme.compare(a) == 1) count_random++;
        }
        double conf = (double) ((double) count_focus)/((double) count_random);   // confidence of a rule  {feature} -> {goodDesign}

        return conf;
    } 

    public void setCandidateFeatures(ArrayList<String> input, ArrayList<String> names){
        candidateFeatures = input;
        candidateFeatures_names = names;
    }

    
    public ArrayList<DrivingFeature> getDrivingFeatures (){
//  two options
//  index for nsat, nplane, alt, inc, RAAN, or FOV + "-" + "exact:" + value
//  index for nsat, nplane, alt, inc, RAAN, or FOV + "-" + "min:" + value + "-" + "max:" + value
        // connected with +
        int i=0;
        for(String cf:candidateFeatures){
            Scheme s = new Scheme();
            String expression = cf;
            s.setExpression(expression);

            double support = computeSupport (s);
            double lift = computeLift(s);
            double conf = computeConfidenceGivenFeature(s);
            double conf2 = computeConfidenceGivenSelection(s);
            if (support > supp_threshold && lift > lift_threshold && conf > confidence_threshold && conf2 > confidence_threshold) {
                String name = candidateFeatures_names.get(i);
                drivingFeatures.add(new DrivingFeature(name, expression,lift, support, conf, conf2));
                
            }
            i = i+1;
        }
        
        

//        for (DrivingFeature userDef1:userDef){
//            
//            scheme.setName(userDef1.getType());
//            double support = computeSupport(scheme);
//            double lift = computeLift(scheme);
//            double conf = computeConfidenceGivenFeature(scheme);
//            double conf2 = computeConfidenceGivenSelection(scheme);
//            if (support > supp_threshold && lift>lift_threshold && conf > confidence_threshold && conf2 > confidence_threshold) {
//                drivingFeatures.add(new DrivingFeature(userDef1.getName(),userDef1.getType(),lift, support, conf, conf2));
//            }
//        }
        
//        getDataFeatureMat();
        
//        System.out.println("----------mRMR-----------");
//        ArrayList<String> mRMR = minRedundancyMaxRelevance(40);
//        for(String mrmr:mRMR){
//            System.out.println(drivingFeatures.get(Integer.parseInt(mrmr)).getName());
//        }

        return drivingFeatures;
    }
    
    
    
    public static DrivingFeaturesGenerator getInstance()
    {
        if( instance == null ) 
        {
            instance = new DrivingFeaturesGenerator();
        }
        return instance;
    }

//    public int[][] getDataFeatureMat(){
//        
//        int numData = random.size();
//        int numFeature = drivingFeatures.size() + 1; // add class label as a last feature
//        int[][] dataMat = new int[numData][numFeature];
//        
//        for(int i=0;i<numData;i++){
//            int[][] d = randomData.get(i);
//            Scheme s = new Scheme();
//
////            presetFilter(String filterName, int[][] data, ArrayList<String> params
//            for(int j=0;j<numFeature-1;j++){
//                DrivingFeature f = drivingFeatures.get(j);
//                String name = f.getName();
//                String type = f.getType();
//                
//                if(f.isPreset()){
//                    String[] param_ = f.getParam();
//                    ArrayList<String> param = new ArrayList<>();
//                    param.addAll(Arrays.asList(param_));
//                    if(s.presetFilter(type, d, param)){
//                        dataMat[i][j]=1;
//                    } else{
//                        dataMat[i][j]=0;
//                    }
//                } else{
//                    if(s.userDefFilter_eval(type, d)){
//                        dataMat[i][j]=1;
//                    } else{
//                        dataMat[i][j]=0;
//                    }
//                }
//            }
//            
//            boolean classLabel = false;
//            for (int[][] compData : focusData) {
//                boolean match = true;
//                for(int k=0;k<d.length;k++){
//                    for(int l=0;l<d[0].length;l++){
//                        if(d[k][l]!=compData[k][l]){
//                            match = false;
//                            break;
//                        }
//                    }
//                    if(match==false) break;
//                }
//                if(match==true){
//                    classLabel = true;
//                    break;
//                }
//            }
//            if(classLabel==true){
//                dataMat[i][numFeature-1]=1;
//            } else{
//                dataMat[i][numFeature-1]=0;
//            }
//        }
//        dataFeatureMat = dataMat;
//        return dataMat;
//    }
//    public ArrayList<String> minRedundancyMaxRelevance(int numSelectedFeatures){
//        
//        int[][] m = dataFeatureMat;
//        int numFeatures = m[0].length;
//        int numData = m.length;
//        ArrayList<String> selected = new ArrayList<>();
//        
//        while(selected.size() < numSelectedFeatures){
//            double phi = -10000;
//            int save=0;
//            for(int i=0;i<numFeatures-1;i++){
//                if(selected.contains(""+i)){
//                    continue;
//                }
//
//                double D = getMutualInformation(i,numFeatures-1);
//                double R = 0;
//
//                for (String selected1 : selected) {
//                    R = R + getMutualInformation(i, Integer.parseInt(selected1));
//                }
//                if(!selected.isEmpty()){
//                   R = (double) R/selected.size();
//                }
//                
////                System.out.println(D-R);
//                
//                if(D-R > phi){
//                    phi = D-R;
//                    save = i;
//                }
//            }
////            System.out.println(save);
//            selected.add(""+save);
//        }
//        return selected;
//    }  
//    public double getMutualInformation(int feature1, int feature2){
//        
//        int[][] m = dataFeatureMat;
//        int numFeatures = m[0].length;
//        int numData = m.length;
//        double I;
//        
//        int x1=0,x2=0;
//        int x1x2=0,nx1x2=0,x1nx2=0,nx1nx2=0;      
//
//        for(int k=0;k<numData;k++){
//            if(m[k][feature1]==1){ // x1==1
//                x1++;
//                if(m[k][feature2]==1){ // x2==1
//                    x2++;
//                    x1x2++;
//                } else{ // x2!=1
//                    x1nx2++;
//                }
//            } else{ // x1!=1
//                if(m[k][feature2]==1){ // x2==1 
//                    x2++;
//                    nx1x2++;
//                }else{ // x2!=1
//                    nx1nx2++;
//                }
//            }
//        }
//        double p_x1 =(double) x1/numData;
//        double p_nx1 = (double) 1-p_x1;
//        double p_x2 = (double) x2/numData;
//        double p_nx2 = (double) 1-p_x2;
//        double p_x1x2 = (double) x1x2/numData;
//        double p_nx1x2 = (double) nx1x2/numData;
//        double p_x1nx2 = (double) x1nx2/numData;
//        double p_nx1nx2 = (double) nx1nx2/numData;
//        
//        if(p_x1==0){p_x1 = 0.0001;}
//        if(p_nx1==0){p_nx1=0.0001;}
//        if(p_x2==0){p_x2=0.0001;}
//        if(p_nx2==0){p_nx2=0.0001;}
//        if(p_x1x2==0){p_x1x2=0.0001;}
//        if(p_nx1x2==0){p_nx1x2=0.0001;}
//        if(p_x1nx2==0){p_x1nx2=0.0001;}
//        if(p_nx1nx2==0){p_nx1nx2=0.0001;}
//        
//        double i1 = p_x1x2*Math.log(p_x1x2/(p_x1*p_x2));
//        double i2 = p_x1nx2*Math.log(p_x1nx2/(p_x1*p_nx2));
//        double i3 = p_nx1x2*Math.log(p_nx1x2/(p_nx1*p_x2));
//        double i4 = p_nx1nx2*Math.log(p_nx1nx2/(p_nx1*p_nx2));
//
//        I = i1 + i2 + i3 + i4;
//        return I;
//    }
    
    
//    public FastVector setDataFormat(){
//        
//            FastVector bool = new FastVector();
//            bool.addElement("false");
//            bool.addElement("true");
//            FastVector attributes = new FastVector();
//
//            for(DrivingFeature df:drivingFeatures){
//                String name = df.getName();
//                attributes.addElement(new Attribute(name,bool));
//            }
//            
//            FastVector bool2 = new FastVector();
//            bool2.addElement("not selected");
//            bool2.addElement("selected ");
//            
//            attributes.addElement(new Attribute("class",bool2));
//            
//            return attributes;
//    }
//    
//    public Instances addData(Instances dataset){
//        
//        for(int i=0;i<randomData.size();i++){
//            double[] values = new double[drivingFeatures.size()+1];
//            for(int j=0;j<drivingFeatures.size()+1;j++){
//                values[j] = (double) dataFeatureMat[i][j];
//            }
//            Instance thisInstance = new Instance(1.0,values);
//            dataset.add(thisInstance);
//        }
//        return dataset;
//    }
//    
//    
//    public String buildTree() {
//        
//        String graph="";
//        long t0 = System.currentTimeMillis();
//        J48 tree = new J48();
//        getDrivingFeatures();
//        getDataFeatureMat();
//        try{
//            
//            FastVector attributes = setDataFormat();
//            Instances dataset = new Instances("Tree_dataset", attributes, 10000);
//            dataset.setClassIndex(dataset.numAttributes()-1);
//            dataset = addData(dataset);
//            dataset.compactify();
//
////            // save as CSV
////            CSVSaver saver = new CSVSaver();
////            saver.setInstances(dataset);
////            saver.setFile(new File(Params.path + "\\tmp_treeData.clp"));
////            saver.writeBatch();
//            
//            System.out.println("numAttributes: " + dataset.numAttributes());
//            System.out.println("num instances: " + dataset.numInstances());
//            
//            String [] options = new String[2];
//            options[0] = "-C";
//            options[1] = "0.05";
//            tree.setOptions(options);
//            
//            Evaluation eval = new Evaluation(dataset);
//            eval.crossValidateModel(tree, dataset, 10, new Random(1));
//            tree.buildClassifier(dataset);
//            
//            System.out.println(eval.toSummaryString("\nResults\n\n", false));
//            System.out.println(eval.toMatrixString());
//            System.out.println(tree.toSummaryString());
//            
//            
//            String summary = tree.toSummaryString();
//            String evalSummary = eval.toSummaryString("\nResults\n\n", false);
//            String confusion = eval.toMatrixString();
//            graph = tree.graph();
//            
//
//            
////Number of leaves: 21
////Size of the tree: 41
////Results
////Correctly Classified Instances        2550               97.3654 %
////Incorrectly Classified Instances        69                2.6346 %
////Kappa statistic                          0.9385
////Mean absolute error                      0.0418
////Root mean squared error                  0.1603
////Relative absolute error                  9.6708 %
////Root relative squared error             34.4579 %
////Total Number of Instances             2619
////=== Confusion Matrix ===
////    a    b   <-- classified as
//// 1771   19 |    a = false
////   50  779 |    b = true
//
//            
//            
////            System.out.println(graph);
//            
////            TreeVisualizer tv = new TreeVisualizer(null, tree.graph(), new PlaceNode2());
////            JFrame jf = new JFrame("Weka Classifier Tree Visualizer: J48");
////            jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
////            jf.setSize(800, 600);
////            jf.getContentPane().setLayout(new BorderLayout());
////            jf.getContentPane().add(tv, BorderLayout.CENTER);
////            jf.setVisible(true);
////            // adjust tree
////            tv.fitToScreen();
//            
//            long t1 = System.currentTimeMillis();
//            System.out.println( "Tree building done in: " + String.valueOf(t1-t0) + " msec");
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//        
//        return graph;
//    }
    

    
//    public void addUserDefFilter(String name, String expression){
//        this.userDef.add(new DrivingFeature(name,expression));
//    }
    
    

}
