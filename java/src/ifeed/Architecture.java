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
public class Architecture {
    
//    input: nsat, nplane, alt, inc, RAAN, FOV     // RAAN is double, and the rest are int
//    output: avg_global, max_global, avg_NH, max_NH, avg_SH, max_SH, avg_polar, max_polar, avg_US, max_US   

    ArrayList<String> inputs;
    ArrayList<String> outputs;
    ArrayList<String> inputNames;
    ArrayList<String> outputNames;
    
    public Architecture(ArrayList<String> inputs, ArrayList<String> outputs){
        this.inputs=inputs;
        this.outputs=outputs;
    }
    public Architecture(ArrayList<String> inputs, ArrayList<String> outputs, ArrayList<String> inputNames, ArrayList<String> outputNames){
        this.inputs=inputs;
        this.outputs=outputs;
        this.inputNames=inputNames;
        this.outputNames=outputNames;
    }
    
    
    public ArrayList<String> getInputs(){
        return inputs;
    }
    public ArrayList<String> getOutputs(){
        return outputs;
    }
    
}
