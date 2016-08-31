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
@WebServlet(name = "drivingFeatureServlet", urlPatterns = {"/drivingFeatureServlet"})
public class drivingFeatureServlet extends HttpServlet {

    
    private Gson gson = new Gson();
    Scheme scheme;
    boolean init = false;
    String displayOutput;
    DrivingFeaturesGenerator dfsGen;
    ArrayList<DrivingFeature> DFs;
    ArrayList<DrivingFeature> sortedDFs;
    
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
            out.println("<title>Servlet drivingFeatureServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet drivingFeatureServlet at " + request.getContextPath() + "</h1>");
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
            
            
        
        if(requestID.equalsIgnoreCase("getDrivingFeatures")){
            
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
                
                System.out.println(cand_tmp);
            }
            
            System.out.println("selectedLength: " + selected.size());
            System.out.println("allLength: " + all.size());
            
            dfsGen.initialize(selected, all, support_threshold,confidence_threshold,lift_threshold);
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
                    value = df.getLift();
                    maxVal = sortedDFs.get(0).getLift();
                    minVal = sortedDFs.get(sortedDFs.size()-1).getLift();
                } else if(sortingCriteria.equalsIgnoreCase("supp")){
                    value = df.getSupport();
                    maxVal = sortedDFs.get(0).getSupport();
                    minVal = sortedDFs.get(sortedDFs.size()-1).getSupport();
                } else if(sortingCriteria.equalsIgnoreCase("confave")){
                    value = (double) (df.getConfidence() + df.getConfidence2())/2;
                    maxVal = (double) (sortedDFs.get(0).getConfidence() + sortedDFs.get(0).getConfidence2())/2;
                    minVal = (double) (sortedDFs.get(sortedDFs.size()-1).getConfidence() + sortedDFs.get(sortedDFs.size()-1).getConfidence2())/2;
                } else if(sortingCriteria.equalsIgnoreCase("conf1")){
                    value = df.getConfidence();
                    maxVal = sortedDFs.get(0).getConfidence();
                    minVal = sortedDFs.get(sortedDFs.size()-1).getConfidence();
                } else if(sortingCriteria.equalsIgnoreCase("conf2")){
                    value = df.getConfidence2();
                    maxVal = sortedDFs.get(0).getConfidence2();
                    minVal = sortedDFs.get(sortedDFs.size()-1).getConfidence2();
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
                                refval = sortedDFs.get(j).getLift();
                                refval2 = sortedDFs.get(j+1).getLift();
                            } else if(sortingCriteria.equalsIgnoreCase("supp")){
                                refval = sortedDFs.get(j).getSupport();
                                refval2 = sortedDFs.get(j+1).getSupport();
                            } else if(sortingCriteria.equalsIgnoreCase("confave")){
                                refval = (double) (sortedDFs.get(j).getConfidence() + sortedDFs.get(j).getConfidence2())/2;
                                refval2 = (double) (sortedDFs.get(j+1).getConfidence() + sortedDFs.get(j+1).getConfidence2())/2;
                            } else if(sortingCriteria.equalsIgnoreCase("conf1")){
                                refval = sortedDFs.get(j).getConfidence();
                                refval2 = sortedDFs.get(j+1).getConfidence();
                            } else if(sortingCriteria.equalsIgnoreCase("conf2")){
                                refval = sortedDFs.get(j).getConfidence2();
                                refval2 = sortedDFs.get(j+1).getConfidence2();
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
            System.out.println(outputString);
        } 

//        else if(requestID.equalsIgnoreCase("addUserDefFeatures")){
//            
//            String names_input = request.getParameter("name");
//            String expressions_input = request.getParameter("expression");
//          
//            dfsGen.addUserDefFilter(names_input,expressions_input);
//        }
        }
        catch(Exception e){ e.printStackTrace();}
        
        response.flushBuffer();
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write(outputString);
        
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
