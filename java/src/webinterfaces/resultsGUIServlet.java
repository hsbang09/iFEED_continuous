	/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webinterfaces;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ifeed.*;
import java.util.Stack;

import com.google.gson.Gson;


import jess.*;
import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;



/**
 *
 * @author Bang
 */
@WebServlet(name = "resultsGUIServlet", urlPatterns = {"/resultsGUIServlet"})
public class resultsGUIServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    private static resultsGUIServlet instance=null;
    
    private String path = "C:\\Users\\Bang\\Documents\\";

    /**
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException{
        instance = this;
    }
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
            out.println("<title>Servlet resultsGUIServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet resultsGUIServlet at " + request.getContextPath() + "</h1>");
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

        String outputString="";
        String requestID = request.getParameter("ID");
        try {
            

        if (requestID.equalsIgnoreCase("resultFileURL_newData")){
            
//            String resultPath = request.getParameter("filePath");
            String resultPath = path + "dataset.xls";
            Workbook results_xls = Workbook.getWorkbook( new File( resultPath ) );
            Sheet meas = results_xls.getSheet("Sheet1");    
            ArrayList<Architecture> results = new ArrayList<>();
            
            int nrows = meas.getRows();
            int ncols = meas.getColumns();
            Cell[] header = meas.getRow(0);
            String[] input_names = new String[6]; 
            String[] output_names = new String[12];
            
            for (int i = 0;i<6;i++) {
                input_names[i] = header[i].getContents();
            }
            for (int i = 0;i<12;i++) {
                output_names[i] = header[i+6].getContents();
            }
            for (int i = 1;i<nrows;i++) {
                Cell[] row = meas.getRow(i);
                ArrayList<String> inputs = new ArrayList<>();
                ArrayList<String> outputs = new ArrayList<>();
                for (int j = 0;j < ncols; j++) {
                    String value = row[j].getContents();
                    if(j<6){
                        inputs.add(value);
                    } else{
                        outputs.add(value);
                    }
                }
                results.add(new Architecture(inputs,outputs));
            }

            String jsonObj = gson.toJson(results);
            outputString = jsonObj;
   
        }
//        else if (requestID.equalsIgnoreCase("getDrivingFeatures")){    
//            System.out.println("get driving features");               
//        }
        else if (requestID.equalsIgnoreCase("resultFileURL_addData")){
            
//            String resultPath = request.getParameter("filePath");
//
//            Stack<Result> tmpResults = RC.getResults();
//            Stack<Result> newResults = new Stack<>();
//            
//            for (Result tmpResult:tmpResults){
//                if(tmpResult.getScience()>=0.001){
//                    if(!results.contains(tmpResult)){
//                        newResults.add(tmpResult);                        
//                    }
//                    else{
//                        System.out.println("repeated result detected");
//                    }
//                }
//            }
//            int numNewResults = newResults.size();
//            int numOriginalResults = results.size();
//            ArrayList<archEvalResults> aerArray = new ArrayList<>();
//            
//            for (int i=0;i<numNewResults;i++){
//                double sci = newResults.get(i).getScience();
//                double cost = newResults.get(i).getCost();
//                boolean[] bitString = newResults.get(i).getArch().getBitString();
//                archEvalResults aer = new archEvalResults(sci,cost,bitString);
//                aer.setStatus("originalData");
//                aerArray.add(aer);
//            }
//            for (int i=0;i<numOriginalResults;i++){
//                double sci = results.get(i).getScience();
//                double cost = results.get(i).getCost();
//                boolean[] bitString = results.get(i).getArch().getBitString();
//                archEvalResults aer = new archEvalResults(sci,cost,bitString);
//                aer.setStatus("originalData");
//                aerArray.add(aer);
//            }
//            String jsonObj = gson.toJson(aerArray);
//            outputString = jsonObj;
//            results.addAll(newResults);
        }
        


        
        else if (requestID.equalsIgnoreCase("evalNewArch")){
//            String bitString = request.getParameter("bitString");
//            
//            ai = ArchWebInterface.getInstance();
//            ai.initialize();
//            Result resu = ai.evaluateArch(bitString, 1);
//            this.results.add(resu);
//            archEvalResults aer = new archEvalResults(resu.getScience(), resu.getCost(), resu.getArch().getBitString());
//            aer.setStatus("justAdded");
//            String jsonObj = gson.toJson(aer);
//            outputString = jsonObj;

        }



        
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        
        
        response.flushBuffer();
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write(outputString);

        
//        processRequest(request, response);
    }
    
    

    
    public static resultsGUIServlet getInstance()
    {
        if( instance == null ) 
        {
            instance = new resultsGUIServlet();
        }
        return instance;
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


        



