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
    DataManagement dm;
    
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
            dm = DataManagement.getInstance();
        }
        
        String outputString="";
        String requestID = request.getParameter("ID");
        try {
            

        if(requestID.equalsIgnoreCase("getDrivingFeatures")){
            
            double support_threshold = Double.parseDouble(request.getParameter("supp"));
            double confidence_threshold = Double.parseDouble(request.getParameter("conf"));
            double lift_threshold = Double.parseDouble(request.getParameter("lift")); 
            int nSelected = Integer.parseInt(request.getParameter("nSelected")); 
            
            ArrayList<Architecture> all = new ArrayList<>();
            ArrayList<Architecture> selected = new ArrayList<>();
            
//            String hidden_raw = request.getParameter("hidden");
            String selected_raw = request.getParameter("selected");
            
//            hidden_raw = hidden_raw.substring(1,hidden_raw.length()-1);
            selected_raw = selected_raw.substring(1,selected_raw.length()-1);

            int[] selected_id = new int[nSelected];
            int cnt=0;
            while(true){
                boolean last=false;
                int comma = selected_raw.indexOf(",");
                if(comma==-1){
                    last=true;
                    comma=selected_raw.length()-1;
                }
                String id_string = selected_raw.substring(0,comma);
                int data_id = Integer.parseInt(id_string);
                selected_id[cnt] = data_id;
                if (last){
                    break;
                }else{
                    selected_raw = selected_raw.substring(comma+1);
                }
            }

            all = dm.queryAllArchitecture();
            for(int id:selected_id){
//                selected.add(dm.queryArchitecture(id));
                for(Architecture thisArch:all){
                    if(thisArch.getID()==id){
                        selected.add(thisArch);
                        break;
                    }
                }
            } 
            
            ArrayList<String> inputNames = dm.queryInputNames();
            ArrayList[] candidateFeatures = dm.queryAllCandidateFeatures();
            ArrayList<String> candidateFeatureNames = candidateFeatures[0];
            ArrayList<String> candidateFeatureExpressions = candidateFeatures[1];
            
            System.out.println("selectedLength: " + selected.size());
            System.out.println("allLength: " + all.size());
            dfsGen.initialize(selected, all, support_threshold,confidence_threshold,lift_threshold);
            dfsGen.setInputNames(inputNames);
            dfsGen.setCandidateFeatures(candidateFeatureExpressions,candidateFeatureNames);

            DFs = dfsGen.getDrivingFeatures();
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
                                refval2 = sortedDFs.get(j+1).getMetrics()[2];
                            } else if(sortingCriteria.equalsIgnoreCase("conf2")){
                                refval = sortedDFs.get(j).getMetrics()[3];
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
            
            unselected_raw = unselected_raw.substring(1,unselected_raw.length()-1);
            selected_raw = selected_raw.substring(1,selected_raw.length()-1);

            while(true){ // Iterate over unselected data
                boolean last=false;
                int comma = unselected_raw.indexOf(",");
                if(comma==-1){
                    last=true;
                    comma=unselected_raw.length()-1;
                }
                String id_string = unselected_raw.substring(0,comma);
                int data_id = Integer.parseInt(id_string);
                Architecture arch = dm.queryArchitecture(data_id);
                all.add(arch);

                if (last){
                    break;
                }else{
                    unselected_raw = unselected_raw.substring(comma+1);
                }
            }

            while(true){
                boolean last=false;
                int comma = selected_raw.indexOf(",");
                if(comma==-1){
                    last=true;
                    comma=selected_raw.length()-1;
                }
                String id_string = selected_raw.substring(0,comma);
                int data_id = Integer.parseInt(id_string);
                Architecture arch = dm.queryArchitecture(data_id);
                all.add(arch);
                selected.add(arch);

                if (last){
                    break;
                }else{
                    selected_raw = selected_raw.substring(comma+1);
                }
            }
            
            ArrayList<String> inputNames = dm.queryInputNames();
            ArrayList[] candidateFeatures = dm.queryAllCandidateFeatures();
            ArrayList<String> candidateFeatureNames = candidateFeatures[0];
            ArrayList<String> candidateFeatureExpressions = candidateFeatures[1];
            
            System.out.println("selectedLength: " + selected.size());
            System.out.println("allLength: " + all.size());
            dfsGen.initialize(selected, all, support_threshold,confidence_threshold,lift_threshold);
            dfsGen.setInputNames(inputNames);
            dfsGen.setCandidateFeatures(candidateFeatureExpressions,candidateFeatureNames);

            String graph = dfsGen.buildTree();
            TreeNode root = parse_decisionTree(graph);
            outputString = gson.toJson(root);
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
