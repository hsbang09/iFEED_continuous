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
import com.google.gson.Gson;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


/**
 *
 * @author Bang
 */
@WebServlet(name = "DataFileServlet", urlPatterns = {"/DataFileServlet"})
public class DataFileServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    private static DataFileServlet instance=null;
   
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
            out.println("<title>Servlet DataFileServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DataFileServlet at " + request.getContextPath() + "</h1>");
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
            
            if (requestID.equalsIgnoreCase("import_data")){
                
                
                String path = request.getParameter("path");
                int nInputs = Integer.parseInt(request.getParameter("numInputs"));
                int nOutputs = Integer.parseInt(request.getParameter("numOutputs"));

    //            String resultPath = request.getParameter("filePath");
                String resultPath = path;
                Workbook results_xls = Workbook.getWorkbook( new File( resultPath ) );
                Sheet meas = results_xls.getSheet("Sheet1");    
                ArrayList<Architecture> results = new ArrayList<>();

                int nrows = meas.getRows();
                int ncols = meas.getColumns();
                Cell[] header = meas.getRow(0);
                ArrayList<String> input_names = new ArrayList<>(); 
                ArrayList<String> output_names = new ArrayList<>();

                for (int i = 0;i<nInputs;i++) {
                    input_names.add(header[i].getContents());
                }
                for (int i = 0;i<nOutputs;i++) {
                    output_names.add(header[i+nInputs].getContents());
                }
                for (int i = 1;i<nrows;i++) {
                    Cell[] row = meas.getRow(i);
                    ArrayList<String> inputs = new ArrayList<>();
                    ArrayList<String> outputs = new ArrayList<>();
                    for (int j = 0;j < ncols; j++) {
                        String value = row[j].getContents();
                        if(j<nInputs){
                            inputs.add(value);
                        } else{
                            outputs.add(value);
                        }
                    }
                    results.add(new Architecture(inputs,outputs,input_names,output_names));
                }

                String jsonObj = gson.toJson(results);
                outputString = jsonObj;

                DataManagement dm = new DataManagement();
            }

        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        
        response.flushBuffer();
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write(outputString);
    }
    
    

    
    public static DataFileServlet getInstance()
    {
        if( instance == null ) 
        {
            instance = new DataFileServlet();
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


        



