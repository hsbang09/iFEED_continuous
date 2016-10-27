/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webinterfaces;

import ifeed.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.util.ArrayList;
import jess.*;
import java.util.Stack;

/**
 *
 * @author Bang
 */
@WebServlet(name = "DataAnalysisServlet", urlPatterns = {"/DataAnalysisServlet"})
public class DataAnalysisServlet extends HttpServlet {

    
    private Gson gson = new Gson();
    Scheme scheme;
    boolean init = false;
    String displayOutput;
    DrivingFeaturesGenerator dfsGen;
    ArrayList<DrivingFeature> DFs;
    ArrayList<DrivingFeature> sortedDFs;
    
    ArrayList<String> candidates;
    ArrayList<String> candidates_names;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DataAnalysisServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DataAnalysisServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);
//
        if (init==false){
            init=true;
            dfsGen = DrivingFeaturesGenerator.getInstance();
        }
        
        String outputString="";
        String requestID = request.getParameter("ID");
        try {
            

//       "[{"inputs":["1","1","500","51","6","25"],"objectives":["121.344","117.97","66.646","66.685","1000","39.072","125.173","123.704","70.096","70.096","168","44.647"]},"
//       + "{"inputs":["1","1","500","51","22.5","25"],"objectives":["111.311","107.032","45.023","45.801","1000","44.589","119.654","114.799","66.415","67.727","168","62.426"]},"
//       + "{"inputs":["1","1","500","51","13.5","25"],"objectives":["116.933","110.228","66.05","66.193","1000","143.498","123.477","120.659","70.096","70.096","168","143.937"]}]"
            
        if(requestID.equalsIgnoreCase("setCandidateFeatures")){
            
            candidates = new ArrayList<>();
            candidates_names = new ArrayList<>();
            
            String candidate_raw = request.getParameter("candidateDrivingFeatures"); 
            String candidate_names_raw = request.getParameter("candidateDrivingFeatures_names");
            
//              ["NSAT[exact:5]","NSAT[exact:4]","NSAT[min:1/max:3]"]
            
            candidate_raw = candidate_raw.substring(1,candidate_raw.length()-1);
            candidate_names_raw = candidate_names_raw.substring(1,candidate_names_raw.length()-1);
            String[] candidate_split = candidate_raw.split(",");
            String[] candidate_names_split = candidate_names_raw.split(",");

            for(int i=0;i<candidate_split.length;i++){
                String cand_tmp = candidate_split[i];
                cand_tmp = cand_tmp.substring(1,cand_tmp.length()-1);
                candidates.add(cand_tmp);
                String cand_name_tmp = candidate_names_split[i];
                cand_name_tmp = cand_name_tmp.substring(1,cand_name_tmp.length()-1);
                candidates_names.add(cand_name_tmp);
//                System.out.println(cand_tmp);
            }
        } 
        
        else if(requestID.equalsIgnoreCase("getDrivingFeatures")){
            
            double support_threshold = Double.parseDouble(request.getParameter("supp"));
            double confidence_threshold = Double.parseDouble(request.getParameter("conf"));
            double lift_threshold = Double.parseDouble(request.getParameter("lift")); 
             
            ArrayList<Architecture> unselected = new ArrayList<>();
            ArrayList<Architecture> selected = new ArrayList<>();
            
            String unselected_raw = request.getParameter("unselected");
            String selected_raw = request.getParameter("selected");
            
            unselected_raw = unselected_raw.substring(2,unselected_raw.length()-2);
            selected_raw = selected_raw.substring(2,selected_raw.length()-2);
//       {"inputs":["1","1","500","51","6","25"],"objectives":["121.344","117.97","66.646","66.685","1000","39.072","125.173","123.704","70.096","70.096","168","44.647"]},
//       {"inputs":["1","1","500","51","22.5","25"],"objectives":["111.311","107.032","45.023","45.801","1000","44.589","119.654","114.799","66.415","67.727","168","62.426"]},
//       {"inputs":["1","1","500","51","13.5","25"],"objectives":["116.933","110.228","66.05","66.193","1000","143.498","123.477","120.659","70.096","70.096","168","143.937"]}
            
            
            ArrayList<String> inputs;
            ArrayList<String> outputs;
            ArrayList<String> inputNames = new ArrayList<>();
            ArrayList<String> outputNames = new ArrayList<>();

            
            String inputNames_raw = request.getParameter("inputNames");
            int indStart = inputNames_raw.indexOf("[");
            int indEnd = inputNames_raw.indexOf("]");
            String inside_name = inputNames_raw.substring(indStart+1,indEnd);
            String[] inside_split_name = inside_name.split(",");
            for(int i=0;i<inside_split_name.length;i++){
                String name = inside_split_name[i];
                name = name.substring(1,name.length()-1);
                inputNames.add(name);
            }
            String outputNames_raw = request.getParameter("outputNames");
            indStart = outputNames_raw.indexOf("[");
            indEnd = outputNames_raw.indexOf("]");
            inside_name = outputNames_raw.substring(indStart+1,indEnd);
            inside_split_name = inside_name.split(",");
            for(int i=0;i<inside_split_name.length;i++){
                String name = inside_split_name[i];
                name = name.substring(1,name.length()-1);
                outputNames.add(name);
            }

            
            while(true){
                if(!unselected_raw.contains("[")){
                    break;
                }
                inputs = new ArrayList<>();
                outputs = new ArrayList<>();

                for (int j=0;j<2;j++){
                    int start = unselected_raw.indexOf("[");
                    int end = unselected_raw.indexOf("]");
                    String inside = unselected_raw.substring(start+1,end);
                    String[] inside_split = inside.split(",");
                    for(int i=0;i<inside_split.length;i++){
                        String num = inside_split[i];
                        num = num.substring(1,num.length()-1);
                        if(j==0){
                            inputs.add(num);
                        }else if(j==1){
                            outputs.add(num);
                        }
                    }
                    unselected_raw = unselected_raw.substring(end+1);
                }

                unselected.add(new Architecture(inputs, outputs,inputNames,outputNames));
            }

            while(true){
                if(!selected_raw.contains("[")){
                    break;
                }
                inputs = new ArrayList<>();
                outputs = new ArrayList<>();
                
                for (int j=0;j<2;j++){
                    int start = selected_raw.indexOf("[");
                    int end = selected_raw.indexOf("]");
                    String inside = selected_raw.substring(start+1,end);
                    String[] inside_split = inside.split(",");
                    for(int i=0;i<inside_split.length;i++){
                        String num = inside_split[i];
                        num = num.substring(1,num.length()-1);
                        if(j==0){
                            inputs.add(num);
                        }else if(j==1){
                            outputs.add(num);
                        }
                    }
                    selected_raw = selected_raw.substring(end+1);
                }
                
                selected.add(new Architecture(inputs, outputs,inputNames,outputNames));
            }
 
            System.out.println("selectedLength: " + selected.size());
            System.out.println("unselectedLength: " + unselected.size());
            
            dfsGen.initialize(selected, unselected, support_threshold,confidence_threshold,lift_threshold);
            dfsGen.setCandidateFeatures(candidates,candidates_names);
            
            DFs = dfsGen.getDrivingFeatures();
         
//            String sortingCriteria = request.getParameter("criteria");
            String sortingCriteria = "lift";
            
            sortedDFs = new ArrayList<>();
            for (DrivingFeature df:DFs){
            
                double value = 0.0;
                double maxVal = 1000000.0;
                double minVal = -1.0;
                
                if (sortedDFs.isEmpty()){
                    sortedDFs.add(df);
                    continue;
                } 
                
                if(sortingCriteria.equalsIgnoreCase("lift")){
                    value = df.getMetrics()[1];
                    maxVal = sortedDFs.get(0).getMetrics()[1];
                    minVal = sortedDFs.get(sortedDFs.size()-1).getMetrics()[1];
                } else if(sortingCriteria.equalsIgnoreCase("supp")){
                    value = df.getMetrics()[0];
                    maxVal = sortedDFs.get(0).getMetrics()[0];
                    minVal = sortedDFs.get(sortedDFs.size()-1).getMetrics()[0];
                } else if(sortingCriteria.equalsIgnoreCase("confave")){
                    value = (double) (df.getMetrics()[2] + df.getMetrics()[3])/2;
                    maxVal = (double) (sortedDFs.get(0).getMetrics()[2] + sortedDFs.get(0).getMetrics()[3])/2;
                    minVal = (double) (sortedDFs.get(sortedDFs.size()-1).getMetrics()[2] + sortedDFs.get(sortedDFs.size()-1).getMetrics()[3])/2;
                } else if(sortingCriteria.equalsIgnoreCase("conf1")){
                    value = df.getMetrics()[2];
                    maxVal = sortedDFs.get(0).getMetrics()[2];
                    minVal = sortedDFs.get(sortedDFs.size()-1).getMetrics()[2];
                } else if(sortingCriteria.equalsIgnoreCase("conf2")){
                    value = df.getMetrics()[3];
                    maxVal = sortedDFs.get(0).getMetrics()[3];
                    minVal = sortedDFs.get(sortedDFs.size()-1).getMetrics()[3];
                }
                
                if (value >= maxVal){
                    sortedDFs.add(0,df);
                } else if(value <= minVal){
                    sortedDFs.add(df);
                } else {
                        for (int j=0;j<sortedDFs.size();j++){
                            
                            double refval = 0.0;
                            double refval2 = 0.0;
                            if(sortingCriteria.equalsIgnoreCase("lift")){
                                refval = sortedDFs.get(j).getMetrics()[1];
                                refval2 = sortedDFs.get(j+1).getMetrics()[1];
                            } else if(sortingCriteria.equalsIgnoreCase("supp")){
                                refval = sortedDFs.get(j).getMetrics()[0];
                                refval2 = sortedDFs.get(j+1).getMetrics()[0];
                            } else if(sortingCriteria.equalsIgnoreCase("confave")){
                                refval = (double) (sortedDFs.get(j).getMetrics()[2] + sortedDFs.get(j).getMetrics()[3])/2;
                                refval2 = (double) (sortedDFs.get(j+1).getMetrics()[2] + sortedDFs.get(j+1).getMetrics()[3])/2;
                            } else if(sortingCriteria.equalsIgnoreCase("conf1")){
                                refval = sortedDFs.get(j).getMetrics()[2];
                                refval2 = sortedDFs.get(j+1).getMetrics()[3];
                            } else if(sortingCriteria.equalsIgnoreCase("conf2")){
                                refval = sortedDFs.get(j).getMetrics()[2];
                                refval2 = sortedDFs.get(j+1).getMetrics()[3];
                            }
                            
                            if(value <= refval && value > refval2){
                                sortedDFs.add(j+1,df);
                                break;
                            }
                        } 
                }
            }
            String jsonObj = gson.toJson(sortedDFs);
            outputString = jsonObj;
        } 
        
        
        
        
        
        
        
        else if(requestID.equalsIgnoreCase("buildClassificationTree")){
            
            double support_threshold = Double.parseDouble(request.getParameter("supp"));
            double confidence_threshold = Double.parseDouble(request.getParameter("conf"));
            double lift_threshold = Double.parseDouble(request.getParameter("lift")); 
             
            ArrayList<Architecture> all = new ArrayList<>();
            ArrayList<Architecture> selected = new ArrayList<>();
            
            String unselected_raw = request.getParameter("unselected");
            String selected_raw = request.getParameter("selected");
            
            unselected_raw = unselected_raw.substring(2,unselected_raw.length()-2);
            selected_raw = selected_raw.substring(2,selected_raw.length()-2);
//       {"inputs":["1","1","500","51","6","25"],"objectives":["121.344","117.97","66.646","66.685","1000","39.072","125.173","123.704","70.096","70.096","168","44.647"]},
//       {"inputs":["1","1","500","51","22.5","25"],"objectives":["111.311","107.032","45.023","45.801","1000","44.589","119.654","114.799","66.415","67.727","168","62.426"]},
//       {"inputs":["1","1","500","51","13.5","25"],"objectives":["116.933","110.228","66.05","66.193","1000","143.498","123.477","120.659","70.096","70.096","168","143.937"]}
            
            
            ArrayList<String> inputs;
            ArrayList<String> outputs;
            ArrayList<String> inputNames;
            ArrayList<String> outputNames;

            while(true){
                if(!unselected_raw.contains("[")){
                    break;
                }
                inputs = new ArrayList<>();
                outputs = new ArrayList<>();
                inputNames = new ArrayList<>();
                outputNames = new ArrayList<>();
                
                for (int j=0;j<4;j++){
                    int start = unselected_raw.indexOf("[");
                    int end = unselected_raw.indexOf("]");
                    String inside = unselected_raw.substring(start+1,end);
                    String[] inside_split = inside.split(",");
                    for(int i=0;i<inside_split.length;i++){
                        String num = inside_split[i];
                        num = num.substring(1,num.length()-1);
                        if(j==0){
                            inputs.add(num);
                        }else if(j==1){
                            outputs.add(num);
                        }else if(j==2){
                            inputNames.add(num);
                        }else if(j==3){
                            outputNames.add(num);
                        }
                    }
                    unselected_raw = unselected_raw.substring(end+1);
                }

                all.add(new Architecture(inputs, outputs,inputNames,outputNames));
            }

            while(true){
                if(!selected_raw.contains("[")){
                    break;
                }
                inputs = new ArrayList<>();
                outputs = new ArrayList<>();
                inputNames = new ArrayList<>();
                outputNames = new ArrayList<>();
                
                for (int j=0;j<4;j++){
                    int start = selected_raw.indexOf("[");
                    int end = selected_raw.indexOf("]");
                    String inside = selected_raw.substring(start+1,end);
                    String[] inside_split = inside.split(",");
                    for(int i=0;i<inside_split.length;i++){
                        String num = inside_split[i];
                        num = num.substring(1,num.length()-1);
                        if(j==0){
                            inputs.add(num);
                        }else if(j==1){
                            outputs.add(num);
                        }else if(j==2){
                            inputNames.add(num);
                        }else if(j==3){
                            outputNames.add(num);
                        }
                    }
                    selected_raw = selected_raw.substring(end+1);
                }
                
                selected.add(new Architecture(inputs, outputs,inputNames,outputNames));
                all.add(new Architecture(inputs,outputs,inputNames,outputNames));
            }
            
            
            String candidate_raw = request.getParameter("candidateDrivingFeatures"); 
            String candidate_names_raw = request.getParameter("candidateDrivingFeatures_names");
            
//              ["NSAT-exact:5","NSAT-exact:4","NSAT-min:1-max:3"]
            
            candidate_raw = candidate_raw.substring(1,candidate_raw.length()-1);
            candidate_names_raw = candidate_names_raw.substring(1,candidate_names_raw.length()-1);
            String[] candidate_split = candidate_raw.split(",");
            String[] candidate_names_split = candidate_names_raw.split(",");
            
            ArrayList<String> candidates = new ArrayList<>();
            ArrayList<String> candidates_names = new ArrayList<>();
            
            for(int i=0;i<candidate_split.length;i++){
                String cand_tmp = candidate_split[i];
                cand_tmp = cand_tmp.substring(1,cand_tmp.length()-1);
                candidates.add(cand_tmp);
                
                String cand_name_tmp = candidate_names_split[i];
                cand_name_tmp = cand_name_tmp.substring(1,cand_name_tmp.length()-1);
                candidates_names.add(cand_name_tmp);
                

            }
            
            System.out.println("selectedLength: " + selected.size());
            System.out.println("allLength: " + all.size());
            
            dfsGen.initialize(selected, all, support_threshold,confidence_threshold,lift_threshold);
            dfsGen.setCandidateFeatures(candidates,candidates_names);

            String graph = dfsGen.buildTree();
            TreeNode root = parse_decisionTree(graph);
            outputString = gson.toJson(root);
//            System.out.println(outputString);
        } 
        
        }
        catch(Exception e){ e.printStackTrace();}
        
        response.flushBuffer();
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write(outputString);
        
    }

    public TreeNode parse_decisionTree(String graph){
        
//        String g = graph.substring(graph.indexOf("{")+2 , graph.indexOf("}")-1);
        String g = graph;
        ArrayList<String> existingNodes = new ArrayList<>();
//        ArrayList<ArrayList<String>> branches;
        TreeNode root = new TreeNode();
        while(g.length()!=0){
            if(!g.contains("\n")){
                break;
            }
            String line = g.substring(0, g.indexOf("\n"));
            String rest = g.substring(g.indexOf("\n")+1);
            if(!line.contains("[")){
                g = rest;
                continue;
            }
            String id;
            if(!line.contains("->")){ // node
                id = line.substring(line.indexOf("N")+1,line.indexOf("[")-1);
                if(id.equalsIgnoreCase("0")){
                    root = new TreeNode("0");
//                    String name = line.substring(line.indexOf("[")+1,line.indexOf("]",line.length()-1));
                    String tmp;
                    String name;
                    if(line.contains("'")){
                        tmp = line.substring(line.indexOf("\"")+2);
                        name = tmp.substring(0,tmp.indexOf("\"")-1);
                    } else{
                        tmp = line.substring(line.indexOf("\"")+1);
                        name = tmp.substring(0,tmp.indexOf("\""));
                    }
                    root.setName(name);
                    existingNodes.add(id);
                }else{
                    if(existingNodes.contains(id)){
                        TreeNode thisNode = root.findDescendent(root,id);
                        String tmp;
                        String name;
                        if(line.contains("'")){
                            tmp = line.substring(line.indexOf("\"")+2);
                            name = tmp.substring(0,tmp.indexOf("\"")-1);
                        } else{
                            tmp = line.substring(line.indexOf("\"")+1);
                            name = tmp.substring(0,tmp.indexOf("\""));
                        }
                        
                        if(name.contains("selected")){
//                            System.out.println(name);
                            String insideParen = name.substring(name.indexOf("(")+1,name.indexOf(")"));
                            double weight;
                            double incorrect=0.0;
                            if(insideParen.indexOf("/")==-1){
                                weight = Double.parseDouble(insideParen);
                            } else{
                                weight = Double.parseDouble(insideParen.substring(0,insideParen.indexOf("/")));
                                incorrect = Double.parseDouble(insideParen.substring(insideParen.indexOf("/")+1));
                            }
                            name = name.substring(0,name.indexOf("(")-1);
                            thisNode.setWeight((int) weight);
                            if(!name.contains("not")){ // selected
                                thisNode.setClassifiedAsSelected((int) (weight-incorrect));
                            } else{    // not selected
                                thisNode.setClassifiedAsSelected((int) incorrect);
                            }
                        }
                        thisNode.setName(name);
                    } else{
                        System.out.println("sth went wrong");
                    }
                }
            }
            else{ // link
                String ids = line.substring(0,line.indexOf("[")-1);
                id = ids.substring(ids.indexOf("N")+1,ids.indexOf("->"));
                String newid = ids.substring(ids.indexOf("->")+3);
                TreeNode thisNode = root.findDescendent(root, id);
                TreeNode newNode = new TreeNode(newid);
                if(line.contains("true")){
                    newNode.setCond(true);
                }else if(line.contains("false")){
                    newNode.setCond(false);
                }else {
                    System.out.println("link without condition");
                }
                thisNode.addChild(newNode);
                existingNodes.add(newid);
            }
            g = rest;
        }
        root.updateAllClassifiedAsSelected(root);
        root.updateAllWeights(root);
        return root;
    }
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
