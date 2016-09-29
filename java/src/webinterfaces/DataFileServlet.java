	/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webinterfaces;

import java.util.HashMap;
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
    DataManagement dm = new DataManagement();

    
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
                
                dm.createNewDB();
                int data_id = 0;
                
                String path = request.getParameter("path");
                int nInputs = Integer.parseInt(request.getParameter("numInputs"));
                int nOutputs = Integer.parseInt(request.getParameter("numOutputs"));

                String resultPath = path;
                Workbook results_xls = Workbook.getWorkbook( new File( resultPath ) );
                Sheet meas = results_xls.getSheet("Sheet1");    
                ArrayList<Architecture> arch_data = new ArrayList<>();

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
                    dm.insertDocument_data(data_id,inputs, outputs);
                    arch_data.add(new Architecture(data_id,inputs,outputs));
                    data_id++;
                }
                dm.encodeMetadata(nrows, input_names, output_names);
                CombinedDataSet dataset = new CombinedDataSet(nrows,input_names,output_names,arch_data);
                String jsonObj = gson.toJson(dataset);
                outputString = jsonObj;

            }
            
            else if(requestID.equalsIgnoreCase("encode_candidate_features")){
            
                String candidate_raw = request.getParameter("candidateDrivingFeatures"); 
                String candidate_names_raw = request.getParameter("candidateDrivingFeatures_names");

    //              ["NSAT-exact:5","NSAT-exact:4","NSAT-min:1-max:3"]
    //            
                candidate_raw = candidate_raw.substring(1,candidate_raw.length()-1);
                candidate_names_raw = candidate_names_raw.substring(1,candidate_names_raw.length()-1);
                String[] candidate_split = candidate_raw.split(",");
                String[] candidate_names_split = candidate_names_raw.split(",");
    //            
                ArrayList<String> candidates = new ArrayList<>();
                ArrayList<String> candidates_names = new ArrayList<>();
    //            
                for(int i=0;i<candidate_split.length;i++){
                    String cand_tmp = candidate_split[i];
                    String candidateFeatureExpression = cand_tmp.substring(1,cand_tmp.length()-1);

                    String cand_name_tmp = candidate_names_split[i];
                    String candidateFeatureName = cand_name_tmp.substring(1,cand_name_tmp.length()-1);
                    
                    dm.insertDocument_candidateFeatures(i, candidateFeatureName, candidateFeatureExpression);
                }
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


class CombinedDataSet{
    
    Metadata metadata;
    ArrayList<Architecture> data;
    
    public CombinedDataSet(int nData, ArrayList<String> inputNames, ArrayList<String> outputNames, ArrayList<Architecture> data){
        this.metadata = new Metadata(nData,inputNames,outputNames);
        this.data = data;
    }
}
class Metadata{
    int nData;
    ArrayList<String> inputNames;
    ArrayList<String> outputNames;
    
    public Metadata(int nData, ArrayList<String> inputNames, ArrayList<String> outputNames){
        this.nData=nData;
        this.inputNames=inputNames;
        this.outputNames=outputNames;
    }
}


