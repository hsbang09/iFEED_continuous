/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ifeed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 *
 * @author Bang
 */
public class Scheme {
    
    private double accuracy = 0.001;
    private String expression;
    // two options
    // check whether two values are exactly the same
    // check whether the value is within the min and max range
    
    public int compare(Architecture a){

        String[] exp_split = expression.split(" and ");
        int flag = 1;
        
        for(int i=0;i<exp_split.length;i++){
            String thisExp = exp_split[i];
            
//  two options
//  index for nsat, nplane, alt, inc, RAAN, or FOV + "-" + "exact:" + value
//  index for nsat, nplane, alt, inc, RAAN, or FOV + "-" + "min:" + value + "-" + "max:" + value
// connected with &
            
            String type = thisExp.split("-")[0];  
            int type_int=-1;
            if(type.equals("NSAT")){
                type_int=0;
            } else if(type.equals("NPLANE")){
                type_int=1;
            }else if(type.equals("ALT")){
                type_int=2;
            }else if(type.equals("INC")){
                type_int=3;
            }else if(type.equals("RAAN")){
                type_int=4;
            }else if(type.equals("FOV")){
                type_int=5;
            }
            
            double exact=-9999, min=-9999, max=-9999;
            for(int j=0;j<thisExp.split("-").length-1;j++){
                
                String compare = thisExp.split("-")[j+1].split(":")[0];
                Double value = Double.parseDouble(thisExp.split("-")[j+1].split(":")[1]);

                if(compare.equals("exact")){
                    exact=value;
                }else if(compare.equals("min")){
                    min=value;
                }else if(compare.equals("max")){
                    max=value;
                }
            }
            
            if(!compare_single(a,type_int,exact,min,max)){
                flag=0;
            }
        }
        return flag;
    }

    
//    private int inputType;
//    private double exact;
//    private double min;
//    private double max;
    
    public boolean compare_single(Architecture a, int type, double exact, double min, double max) {
        
        if(type<0){System.out.println("Error in the feature type");}
        
        double actual = Double.parseDouble(a.getInputs().get(type));
        
        if(exact!=-9999){
            if(Math.abs(actual-exact)<=accuracy){
                return true;
            }
            return false;
        }else{
            
            boolean passed = true;
            
            if(min!=-9999){
                if(actual>=min){
                    // do nothing
                }else{
                    passed=false;
                }
            }
            if(max!=-9999){
                if(actual<=max){
                    // do nothing
                }else{
                    passed=false;
                }
            }
            return passed;
        }
    }
    
    
    
    public void setExpression(String expression){
        this.expression=expression;
    }
    
//    public void setInputType(int inputType){
//        this.inputType=inputType;
//    }
//    public void setExact(double exact){
//        this.exact=exact;
//    }
//    public void setMin(double min){
//        this.min=min;
//    }
//    public void setMax(double max){
//        this.max=max;
//    }
    
    
    
    
    
    
//
//    public boolean userDefFilter_eval(String filterExpression,int[][] data){
//        
//        String e = filterExpression;
//        while(e.contains("{")){
//            int level=0;
//            int maxLevel=0;
//
//            for(int i=0;i<e.length();i++){
//                String thisChar = e.substring(i,i+1);
//                if(thisChar.equalsIgnoreCase("{")){
//                    level++;
//                    if(level > maxLevel){
//                        maxLevel = level;
//                    }
//                } else if(thisChar.equalsIgnoreCase("}")){
//                    level--;
//                }
//            }
//            level=0;
//            int maxLevelLoc = 0;
//            for(int i=0;i<e.length();i++){
//                String thisChar = e.substring(i,i+1);
//                if(thisChar.equalsIgnoreCase("{")){
//                    level++;
//                    if(level==maxLevel){
//                        maxLevelLoc = i;
//                        break;
//                    }
//                } else if(thisChar.equalsIgnoreCase("}")){
//                    level--;
//                }
//            }
//            
//            String innermostExpression = e.substring(maxLevelLoc+1);
//            innermostExpression = innermostExpression.substring(0, innermostExpression.indexOf("}"));
//            boolean bool = userDefFilter_evalWithoutParen(innermostExpression,data);
//            String e1,e2;
//            if(maxLevelLoc==0){e1 = "";}
//            else {e1 = e.substring(0,maxLevelLoc);}
//            if(maxLevelLoc + 1 + innermostExpression.length() + 1 == e.length()){e2 = "";}
//            else {e2 = e.substring(maxLevelLoc + innermostExpression.length() + 2);}
////            if(tmpcnt==0){System.out.println(e);}
//            if(bool){
//                e = e1 + "true()" + e2;
//            } else{
//                e = e1 + "false()" + e2;
//            }
//        }
//        return userDefFilter_evalWithoutParen(e,data); 
//    }
//    
//    public boolean userDefFilter_evalWithoutParen(String filterExpression,int[][] data){
//        
//        String e = filterExpression;
//        String logic = "and";
//        boolean output = true;
//        
//        while(true){
////            if(tmpcnt==0){System.out.println(e);}
//            int andLoc = e.indexOf("&&");
//            int orLoc = e.indexOf("||");
//            
//            if(andLoc==-1 && orLoc==-1){ //single preset feature
//                output = userDefFilter_evalSingle(e,output,logic,data);
//                break;
//            } else{
//                int firstLogicLoc;
//                String nextLogic;
//                if(andLoc==-1){
//                    firstLogicLoc = orLoc;
//                    nextLogic = "or";
//                } else if(orLoc==-1){
//                    firstLogicLoc = andLoc;
//                    nextLogic="and";
//                } else if(andLoc < orLoc){
//                    firstLogicLoc = andLoc;
//                    nextLogic = "and";
//                } else{
//                    firstLogicLoc = orLoc;
//                    nextLogic = "or";
//                }
//                
//                output = userDefFilter_evalSingle(e.substring(0,firstLogicLoc),output,logic,data);
//                e = e.substring(firstLogicLoc+2);
//                logic = nextLogic;
//            }
//        }
////        tmpcnt++;
//        return output;
//    }
//    
    
//    
//            
//    public boolean userDefFilter_evalSingle(String filterExpression,boolean prev,String logic,int[][] data){
//
//        String e = filterExpression;
//        int paren1 = e.indexOf("(");
//        int paren2 = e.indexOf(")");
//        if(paren1==-1){System.out.println(e);}
//        String filterName = e.substring(0,paren1);
//        String params = e.substring(paren1+1,paren2);
//        ArrayList<String> param = new ArrayList<>();
//        boolean output=true;
//        
//        if(!params.contains(";")){
//            param.add(params);
//        } else{
//            while(params.contains(";")){
//                int semicolon = params.indexOf(";");
//                param.add(params.substring(0,semicolon));
//                params = params.substring(semicolon+1);
//            }
//            param.add(params);
//        }
//        
//        if(filterName.equalsIgnoreCase("true")){
//            output= true;
//        } else if(filterName.equalsIgnoreCase("false")){
//            output = false;
//        } else {
//            output = presetFilter(filterName,data,param);
//        }
//        
//        if(prev==output){
//            return prev;
//        } else{
//            return logic.equalsIgnoreCase("or");
//        }
//    }
    
    
    
    
    
    
}