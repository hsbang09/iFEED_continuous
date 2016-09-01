/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webinterfaces;

import ifeed.Scheme;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import jess.Rete;
import jess.*;
import java.util.Stack;

/**
 *
 * @author Bang
 */
@WebServlet(name = "classificationTreeServlet", urlPatterns = {"/classificationTreeServlet"})
public class classificationTreeServlet extends HttpServlet {

    private Gson gson = new Gson();

    Scheme scheme;
    boolean init = false;
    int norb;
    int ninstr;
    String[] instrument_list;
    String[] orbit_list;
//    DrivingFeaturesGenerator dfsGen;
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
            out.println("<title>Servlet classificationTreeServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet classificationTreeServlet at " + request.getContextPath() + "</h1>");
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
    
        String outputString="";
        String requestID = request.getParameter("ID");
        try {
            
        if (requestID.equalsIgnoreCase("initializeClassificationTree")){

        } else if(requestID.equalsIgnoreCase("addUserDefFeatures")){

        }else if (requestID.equalsIgnoreCase("buildClassificationTree")){
//            String graph = dfsGen.buildTree();
//            TreeNode root = parse_decisionTree(graph);
//            outputString = gson.toJson(root);
//            System.out.println(outputString);
            
        } else if(requestID.equalsIgnoreCase("getTreeSummary")){
            

//            output = treeSummary + "\n==========\n" +
//                    evalSummary + "\n==========\n" + 
//                    confusion + "\n==========\n" +
//                    graph;

//Number of leaves: 21
//Size of the tree: 41
//Results
//Correctly Classified Instances        2550               97.3654 %
//Incorrectly Classified Instances        69                2.6346 %
//Kappa statistic                          0.9385
//Mean absolute error                      0.0418
//Root mean squared error                  0.1603
//Relative absolute error                  9.6708 %
//Root relative squared error             34.4579 %
//Total Number of Instances             2619
//=== Confusion Matrix ===
//    a    b   <-- classified as
// 1771   19 |    a = false
//   50  779 |    b = true

        }
        

            
        }catch(Exception e){
            e.printStackTrace();
        }
        
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
