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
        private double[] metrics;

        public DrivingFeature(String name){
            this.name = name;
        }
        public DrivingFeature(String name,String expression,double[] metrics){
            this.name = name;
            this.expression=expression;
            this.metrics = metrics;
        }

        public String getName(){return name;}
        public String getExpression(){return expression;}
        public double[] getMetrics(){return metrics;}


        
    }
