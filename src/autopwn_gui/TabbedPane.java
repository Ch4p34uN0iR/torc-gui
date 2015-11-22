/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autopwn_gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author user
 */
public class TabbedPane extends javax.swing.JFrame {

    /**
     * Creates new form TabbedPane
     */
    public TabbedPane() {
        initComponents();
        // Check if we're connected to autopwn instance
        CheckConnection();
        // Populate tool list
        PopulateTools();
        // Populate assessment list
        PopulateAssessments();
        // Populate tool jobs
        PopulateToolJobs();
    }

    public final boolean PopulateTools() {
        String sURL = "http://127.0.0.1:5000/tools"; //just a string
        JsonArray tools;

        // Connect to the URL using java's native library
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            tools = rootobj.get("result").getAsJsonArray();
            for (int i = 0; i < tools.size(); i++) {
                JsonObject row = tools.get(i).getAsJsonObject();
                comboboxRunToolTools.addItem(row.get("name").toString().replaceAll("^\"|\"$", ""));
            }
        } catch (Exception exc) {
            //test
            System.err.println("error");
        }

        return true;
    }

    public final boolean PopulateAssessments() {
        String sURL = "http://127.0.0.1:5000/assessments"; //just a string
        JsonArray tools;

        // Connect to the URL using java's native library
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            tools = rootobj.get("result").getAsJsonArray();
            for (int i = 0; i < tools.size(); i++) {
                JsonObject row = tools.get(i).getAsJsonObject();
                comboboxRunAssessmentAssessments.addItem(row.get("name").toString().replaceAll("^\"|\"$", ""));
            }
        } catch (Exception exc) {
            //test
            System.err.println("error");
        }

        return true;
    }
    
    public final String getToolById(int tool_id) {
        String toolName = "error", sURL = "http://127.0.0.1:5000/tools/" + tool_id; //just a string
        JsonArray tools;

        // Connect to the URL using java's native library
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            tools = rootobj.get("result").getAsJsonArray();
            JsonObject row = tools.get(0).getAsJsonObject();
            toolName = (row.get("name").toString());
        } catch (Exception exc) {
            //test
            System.err.println("error");
        }

        return toolName;
    }

    public final boolean PopulateToolJobs() {
        String sURL = "http://127.0.0.1:5000/tools/jobs"; //just a string
        String target_name, tool_name, target, id, return_code;
        JsonArray tools;
        int k;

        // Connect to the URL using java's native library
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setUseCaches(false);
            request.connect();
            request.disconnect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            tools = rootobj.get("result").getAsJsonArray();
            System.out.println(tools);
            DefaultTableModel model = (DefaultTableModel) TableToolJobs.getModel();
            // Remove rows
            if (model.getRowCount() > 0) {
                for (int i = model.getRowCount() - 1; i > -1; i--) {
                    model.removeRow(i);
                }
            }

            for (int i = 0; i < tools.size(); i++) {
                JsonObject row = tools.get(i).getAsJsonObject();
                id = row.get("id").toString().replaceAll("^\"|\"$", "");
                target_name = row.get("target_name").toString().replaceAll("^\"|\"$", "");
                tool_name = getToolById(row.get("tool").getAsInt()).replaceAll("^\"|\"$", "");
                target = row.get("target").toString().replaceAll("^\"|\"$", "");
                return_code = row.get("return_code").toString().replaceAll("^\"|\"$", "");
                model.addRow(new Object[]{i});
                TableToolJobs.setValueAt(id, i, 0);
                TableToolJobs.setValueAt(target_name, i, 1);
                TableToolJobs.setValueAt(tool_name, i, 2);
                TableToolJobs.setValueAt(target, i, 3);
                TableToolJobs.setValueAt(return_code, i, 4);
            }
        } catch (Exception exc) {
            //test
            System.err.println("error" + exc);
        }

        return true;
    }
    
    public final boolean CheckConnection() {
        String sURL = "http://127.0.0.1:5000/ping"; //just a string
        String message;
        
        // Connect to the URL using java's native library
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
            message = rootobj.get("message").getAsString();
            if (message.equals("pong")) {
                labelRunToolConnectionStatus.setForeground(Color.green);
                labelRunToolConnectionStatus.setText("Connected!");
                labelRunAssessmentConnectionStatus.setForeground(Color.green);
                labelRunAssessmentConnectionStatus.setText("Connected!");
            }
        } catch (Exception exc) {
            //test
        }

        return true;
    }

    public final int SaveToolJob(int param_tool_id) {
        String sURL = "http://127.0.0.1:5000/tools/jobs"; //just a string
        int job_id = 0, tool_id = 0;
        String urlParameters;
        
        // Depending on which panel is visible we need the contents of the text field in that panel
        try {
            if (PanelRunTool.isVisible() ==  true){
                 urlParameters = "tool=" + comboboxRunToolTools.getSelectedIndex() +
                        "&target=" + textfieldRunToolTarget.getText() +
                        "&target_name=" + textfieldRunToolTargetName.getText() +
                        "&protocol=" + textfieldRunToolProtocol.getText() +
                        "&port_number=" + formattedtextfieldRunToolPortNumber.getText() +
                        "&user=" + textfieldRunToolUser.getText() +
                        "&password=" + textfieldRunToolPassword.getText() +
                        "&user_file=" + textfieldRunToolUserFile.getText() +
                        "&password_file=" + textfieldRunToolPasswordFile.getText();
            } else {
                tool_id = param_tool_id;
                urlParameters = "tool=" + tool_id +
                        "&target=" + textfieldRunAssessmentTarget.getText() +
                        "&target_name=" + textfieldRunAssessmentTargetName.getText() +
                        "&protocol=" + textfieldRunAssessmentProtocol.getText() +
                        "&port_number=" + formattedtextfieldRunAssessmentPortNumber.getText() +
                        "&user=" + textfieldRunAssessmentUser.getText() +
                        "&password=" + textfieldRunAssessmentPassword.getText() +
                        "&user_file=" + textfieldRunAssessmentUserFile.getText() +
                        "&password_file=" + textfieldRunAssessmentPasswordFile.getText();
            }

            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;
            URL url = new URL(sURL);

            // Connect to the URL using java's native library
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );
            try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                wr.write( postData );
            }
            conn.disconnect();
            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) conn.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
            job_id = rootobj.get("id").getAsInt();
            System.out.println("Job id: " + job_id);
        } catch (Exception exc) {
            //test
        }

        return job_id;
    }

    public final int RunAssessmentJob(int job_id) {
        String sURL = "http://127.0.0.1:5000/assessments/jobs/execute"; //just a string

        // Connect to the URL using java's native library
        try {
            String urlParameters = "id=" + job_id;
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;
            URL url = new URL(sURL);

            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );
            try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                wr.write( postData );
            }
            conn.disconnect();
            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) conn.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
            job_id = rootobj.get("id").getAsInt();
            System.out.println("Job id: " + job_id);
        } catch (Exception exc) {
            //test
        }

        return 1;
    }

    public int RunToolJob(int job_id) {
        String sURL = "http://127.0.0.1:5000/tools/jobs/execute"; //just a string

        // Connect to the URL using java's native library
        try {
            String urlParameters = "id=" + job_id;
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;
            URL url = new URL(sURL);

            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );
            try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                wr.write( postData );
            }
            conn.disconnect();
            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) conn.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
            job_id = rootobj.get("id").getAsInt();
            System.out.println("Job id: " + job_id);
        } catch (Exception exc) {
            //test
        }

        return 1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TabbedAutopwn = new javax.swing.JTabbedPane();
        PanelRunTool = new javax.swing.JPanel();
        textfieldRunToolTargetName = new javax.swing.JTextField();
        textfieldRunToolTarget = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        textfieldRunToolPasswordFile = new javax.swing.JTextField();
        textfieldRunToolUserFile = new javax.swing.JTextField();
        textfieldRunToolProtocol = new javax.swing.JTextField();
        textfieldRunToolURL = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        comboboxRunToolTools = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        buttonRunToolRun = new javax.swing.JButton();
        labelRunToolConnectionStatus = new javax.swing.JLabel();
        formattedtextfieldRunToolPortNumber = new javax.swing.JFormattedTextField();
        textfieldRunToolUser = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        textfieldRunToolPassword = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        PanelRunAssessment = new javax.swing.JPanel();
        textfieldRunAssessmentTargetName = new javax.swing.JTextField();
        textfieldRunAssessmentTarget = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        textfieldRunAssessmentPasswordFile = new javax.swing.JTextField();
        textfieldRunAssessmentUserFile = new javax.swing.JTextField();
        textfieldRunAssessmentProtocol = new javax.swing.JTextField();
        textfieldRunAssessmentURL = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        buttonRunAssessmentRun = new javax.swing.JButton();
        labelRunAssessmentConnectionStatus = new javax.swing.JLabel();
        formattedtextfieldRunAssessmentPortNumber = new javax.swing.JFormattedTextField();
        textfieldRunAssessmentUser = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        textfieldRunAssessmentPassword = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        comboboxRunAssessmentAssessments = new javax.swing.JComboBox<>();
        PanelJobs = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableToolJobs = new javax.swing.JTable();
        ButtonRefreshTable = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        ButtonToolExport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        textfieldRunToolTarget.setToolTipText("");

        jLabel2.setText("target_name");

        jLabel5.setText("target");

        jLabel6.setText("port_number");

        jLabel7.setText("password_file");

        jLabel8.setText("Autopwn GUI v0.1.0");

        comboboxRunToolTools.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None" }));

        jLabel9.setText("Tool");

        buttonRunToolRun.setText("Run");
        buttonRunToolRun.setToolTipText("");
        buttonRunToolRun.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonRunToolRunMouseClicked(evt);
            }
        });

        labelRunToolConnectionStatus.setForeground(new java.awt.Color(204, 51, 0));
        labelRunToolConnectionStatus.setText("Not Connected!");
        labelRunToolConnectionStatus.setName("labelRunToolConnectionStatus"); // NOI18N

        jLabel12.setText("password");

        jLabel13.setText("user");

        jLabel14.setText("user_file");

        jLabel15.setText("protocol");

        jLabel16.setText("url");

        javax.swing.GroupLayout PanelRunToolLayout = new javax.swing.GroupLayout(PanelRunTool);
        PanelRunTool.setLayout(PanelRunToolLayout);
        PanelRunToolLayout.setHorizontalGroup(
            PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRunToolLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelRunToolLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                        .addComponent(labelRunToolConnectionStatus))
                    .addComponent(buttonRunToolRun, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PanelRunToolLayout.createSequentialGroup()
                        .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16)
                            .addComponent(jLabel9))
                        .addGap(12, 12, 12)
                        .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboboxRunToolTools, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(textfieldRunToolUserFile)
                            .addComponent(textfieldRunToolUser)
                            .addComponent(textfieldRunToolPassword)
                            .addComponent(textfieldRunToolPasswordFile)
                            .addComponent(textfieldRunToolTargetName)
                            .addComponent(textfieldRunToolTarget)
                            .addComponent(formattedtextfieldRunToolPortNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                            .addComponent(textfieldRunToolProtocol)
                            .addComponent(textfieldRunToolURL))))
                .addContainerGap())
        );
        PanelRunToolLayout.setVerticalGroup(
            PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRunToolLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(labelRunToolConnectionStatus))
                .addGap(18, 18, 18)
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboboxRunToolTools, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(textfieldRunToolTargetName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textfieldRunToolTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(formattedtextfieldRunToolPortNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textfieldRunToolPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textfieldRunToolPasswordFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(textfieldRunToolUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(textfieldRunToolUserFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(textfieldRunToolProtocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunToolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textfieldRunToolURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRunToolRun)
                .addContainerGap(91, Short.MAX_VALUE))
        );

        TabbedAutopwn.addTab("Run Tool", PanelRunTool);

        textfieldRunAssessmentTarget.setToolTipText("");

        jLabel4.setText("target_name");

        jLabel11.setText("target");

        jLabel17.setText("port_number");

        jLabel18.setText("password_file");

        jLabel19.setText("Autopwn GUI v0.1.0");

        buttonRunAssessmentRun.setText("Run");
        buttonRunAssessmentRun.setToolTipText("");
        buttonRunAssessmentRun.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonRunAssessmentRunMouseClicked(evt);
            }
        });

        labelRunAssessmentConnectionStatus.setForeground(new java.awt.Color(204, 51, 0));
        labelRunAssessmentConnectionStatus.setText("Not Connected!");
        labelRunAssessmentConnectionStatus.setName("labelConnectionStatus"); // NOI18N

        jLabel21.setText("password");

        jLabel22.setText("user");

        jLabel23.setText("user_file");

        jLabel24.setText("protocol");

        jLabel25.setText("url");

        jLabel26.setText("Assessment");

        comboboxRunAssessmentAssessments.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None" }));

        javax.swing.GroupLayout PanelRunAssessmentLayout = new javax.swing.GroupLayout(PanelRunAssessment);
        PanelRunAssessment.setLayout(PanelRunAssessmentLayout);
        PanelRunAssessmentLayout.setHorizontalGroup(
            PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRunAssessmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelRunAssessmentLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                        .addComponent(labelRunAssessmentConnectionStatus))
                    .addComponent(buttonRunAssessmentRun, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PanelRunAssessmentLayout.createSequentialGroup()
                        .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel11)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)
                            .addComponent(jLabel21)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23)
                            .addComponent(jLabel24)
                            .addComponent(jLabel25)
                            .addComponent(jLabel26))
                        .addGap(12, 12, 12)
                        .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboboxRunAssessmentAssessments, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(textfieldRunAssessmentUserFile)
                            .addComponent(textfieldRunAssessmentUser)
                            .addComponent(textfieldRunAssessmentPassword)
                            .addComponent(textfieldRunAssessmentPasswordFile)
                            .addComponent(textfieldRunAssessmentTargetName)
                            .addComponent(textfieldRunAssessmentTarget)
                            .addComponent(formattedtextfieldRunAssessmentPortNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                            .addComponent(textfieldRunAssessmentProtocol)
                            .addComponent(textfieldRunAssessmentURL))))
                .addContainerGap())
        );
        PanelRunAssessmentLayout.setVerticalGroup(
            PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRunAssessmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(labelRunAssessmentConnectionStatus))
                .addGap(18, 18, 18)
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboboxRunAssessmentAssessments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(textfieldRunAssessmentTargetName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textfieldRunAssessmentTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(formattedtextfieldRunAssessmentPortNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textfieldRunAssessmentPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textfieldRunAssessmentPasswordFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(textfieldRunAssessmentUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(textfieldRunAssessmentUserFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(textfieldRunAssessmentProtocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRunAssessmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textfieldRunAssessmentURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRunAssessmentRun)
                .addContainerGap(91, Short.MAX_VALUE))
        );

        TabbedAutopwn.addTab("Run Assessment", PanelRunAssessment);

        TableToolJobs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Tool", "Target", "Return Code"
            }
        ));
        jScrollPane1.setViewportView(TableToolJobs);

        ButtonRefreshTable.setText("Refresh tables");
        ButtonRefreshTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ButtonRefreshTableMouseClicked(evt);
            }
        });

        jLabel3.setText("Jobs");

        ButtonToolExport.setText("Export");
        ButtonToolExport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ButtonToolExportMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout PanelJobsLayout = new javax.swing.GroupLayout(PanelJobs);
        PanelJobs.setLayout(PanelJobsLayout);
        PanelJobsLayout.setHorizontalGroup(
            PanelJobsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelJobsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelJobsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelJobsLayout.createSequentialGroup()
                        .addGroup(PanelJobsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ButtonRefreshTable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ButtonToolExport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(PanelJobsLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 411, Short.MAX_VALUE))))
        );
        PanelJobsLayout.setVerticalGroup(
            PanelJobsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelJobsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonToolExport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonRefreshTable)
                .addGap(21, 21, 21))
        );

        TabbedAutopwn.addTab("Jobs", PanelJobs);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TabbedAutopwn)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TabbedAutopwn)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonRunToolRunMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonRunToolRunMouseClicked
        // Run job
        int job_id = SaveToolJob(0);
        try {
            Thread.sleep(100);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        RunToolJob(job_id);
    }//GEN-LAST:event_buttonRunToolRunMouseClicked

    private void ButtonToolExportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ButtonToolExportMouseClicked
        // TODO add your handling code here:
        // TODO add your handling code here:
        String defaultFileName, jobId = TableToolJobs.getValueAt(TableToolJobs.getSelectedRow(), 0).toString();
        System.out.println(jobId);
        String sURL = "http://127.0.0.1:5000/tools/jobs/exports/" + jobId; //just a string
        String message;
        InputStream fileContent;
        byte[] buffer = new byte[4096];
        int n = -1;

        // Connect to the URL using java's native library
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();
            fileContent = request.getInputStream();

            // parent component of the dialog
            JFrame parentFrame = new JFrame();

            defaultFileName = TableToolJobs.getValueAt(TableToolJobs.getSelectedRow(), 3).toString() +
                                "_" + TableToolJobs.getValueAt(TableToolJobs.getSelectedRow(), 2).toString() +
                                ".zip";
            // New file path
            File fileToSavePath = new File(System.getProperty("user.home") + "/" + defaultFileName);
            // New fileChooser
            JFileChooser fileChooser = new JFileChooser();
            // Set fileChooser path to file
            fileChooser.setSelectedFile(fileToSavePath);
            fileChooser.setDialogTitle("Specify a file to save");

            int userSelection = fileChooser.showSaveDialog(parentFrame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                System.out.println("Save as file: " + fileToSave.getAbsolutePath());

                OutputStream output = new FileOutputStream(fileToSave);
                while ((n = fileContent.read(buffer)) != -1)
                {
                    output.write(buffer, 0, n);
                }
                output.close();
            }
        } catch (Exception anexc) {
            // something
        }
    }//GEN-LAST:event_ButtonToolExportMouseClicked

    private void ButtonRefreshTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ButtonRefreshTableMouseClicked
        // TODO add your handling code here:
        // Populate tool jobs
        PopulateToolJobs();
    }//GEN-LAST:event_ButtonRefreshTableMouseClicked

    private void buttonRunAssessmentRunMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonRunAssessmentRunMouseClicked
        // Fetch tools from assessment
        int assessment_id = comboboxRunAssessmentAssessments.getSelectedIndex();
        String sURL = "http://127.0.0.1:5000/assessments/" + assessment_id; //just a string
        JsonArray tools;

        // Connect to the URL using java's native library
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            // Get result/tool
            tools = rootobj.get("result").getAsJsonArray();

            JsonObject row = tools.get(0).getAsJsonObject();
            JsonArray tool = row.get("tools").getAsJsonArray();

            for (int i = 0; i < tool.size(); i++) {
                JsonObject tool_id_json = tool.get(i).getAsJsonObject();
                int tool_id = tool_id_json.get("tool").getAsInt();

                // Run job
                int job_id = SaveToolJob(tool_id);
                try {
                    Thread.sleep(100);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                RunToolJob(job_id);
            }
        } catch (Exception exc) {
            //test
            System.err.println("error " + exc);
        }


    }//GEN-LAST:event_buttonRunAssessmentRunMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TabbedPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TabbedPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TabbedPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TabbedPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TabbedPane().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonRefreshTable;
    private javax.swing.JButton ButtonToolExport;
    private javax.swing.JPanel PanelJobs;
    private javax.swing.JPanel PanelRunAssessment;
    private javax.swing.JPanel PanelRunTool;
    private javax.swing.JTabbedPane TabbedAutopwn;
    private javax.swing.JTable TableToolJobs;
    private javax.swing.JButton buttonRunAssessmentRun;
    private javax.swing.JButton buttonRunToolRun;
    private javax.swing.JComboBox<String> comboboxRunAssessmentAssessments;
    private javax.swing.JComboBox<String> comboboxRunToolTools;
    private javax.swing.JFormattedTextField formattedtextfieldRunAssessmentPortNumber;
    private javax.swing.JFormattedTextField formattedtextfieldRunToolPortNumber;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelRunAssessmentConnectionStatus;
    private javax.swing.JLabel labelRunToolConnectionStatus;
    private javax.swing.JTextField textfieldRunAssessmentPassword;
    private javax.swing.JTextField textfieldRunAssessmentPasswordFile;
    private javax.swing.JTextField textfieldRunAssessmentProtocol;
    private javax.swing.JTextField textfieldRunAssessmentTarget;
    private javax.swing.JTextField textfieldRunAssessmentTargetName;
    private javax.swing.JTextField textfieldRunAssessmentURL;
    private javax.swing.JTextField textfieldRunAssessmentUser;
    private javax.swing.JTextField textfieldRunAssessmentUserFile;
    private javax.swing.JTextField textfieldRunToolPassword;
    private javax.swing.JTextField textfieldRunToolPasswordFile;
    private javax.swing.JTextField textfieldRunToolProtocol;
    private javax.swing.JTextField textfieldRunToolTarget;
    private javax.swing.JTextField textfieldRunToolTargetName;
    private javax.swing.JTextField textfieldRunToolURL;
    private javax.swing.JTextField textfieldRunToolUser;
    private javax.swing.JTextField textfieldRunToolUserFile;
    // End of variables declaration//GEN-END:variables
}
