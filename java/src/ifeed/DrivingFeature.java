/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ifeed;

import java.util.ArrayList;

/**
 *
 * @author Bang
 */
 
public class DrivingFeature{
        
        private String name; 
        private String expression;
        private double lift;
        private double supp;
        private double conf;  //{feature} -> {good design}
        private double conf2; // {good design} -> {feature}
        

        public DrivingFeature(String name){
            this.name = name;

        }
        public DrivingFeature(String name,String expression, double lift, double supp, double conf, double conf2){
            this.name = name;
            this.expression=expression;
            this.lift = lift;
            this.supp = supp;
            this.conf = conf;       
            this.conf2 = conf2;
        }

        public String getName(){return name;}
        public String getExpression(){return expression;}
        public double getLift(){return lift;}
        public double getSupport(){return supp;}
        public double getConfidence(){return conf;}
        public double getConfidence2(){return conf2;}

        
    }
