/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.GUI;

import client.tinyClient;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shinelon
 */
public class LoginFrame extends javax.swing.JFrame {

    /**
     * Creates new form LoginFrame
     */
    public LoginFrame() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton_testConnect = new javax.swing.JButton();
        jButton_login = new javax.swing.JButton();
        jButton_reset = new javax.swing.JButton();
        jTextField_host = new javax.swing.JTextField();
        jTextField_port = new javax.swing.JTextField();
        jTextField_username = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel1_login_result = new javax.swing.JLabel();
        jPasswordField = new javax.swing.JPasswordField();
        jButton_register = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("cool_DB");

        jLabel2.setText("主机：");

        jLabel3.setText("端口：");

        jLabel4.setText("用户名：");

        jLabel5.setText("密码：");

        jButton_testConnect.setText("测试连接");
        jButton_testConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_testConnectActionPerformed(evt);
            }
        });

        jButton_login.setText("登陆");
        jButton_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_loginActionPerformed(evt);
            }
        });

        jButton_reset.setText("重置");
        jButton_reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_resetActionPerformed(evt);
            }
        });

        jTextField_host.setText("127.0.0.1");

        jTextField_port.setText("6666");

        jTextField_username.setText("123");
        jTextField_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_usernameActionPerformed(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon("F:\\大三小学期\\tinySQL-2\\src\\client\\GUI\\QQ图片20200713092249.png")); // NOI18N

        jPasswordField.setText("123");

        jButton_register.setText("注册");
        jButton_register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_registerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton_testConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1_login_result, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton_login)
                                .addGap(14, 14, 14)
                                .addComponent(jButton_register))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(72, 72, 72)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPasswordField, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                                    .addComponent(jTextField_port, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField_username, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                                    .addComponent(jTextField_host))))))
                .addGap(18, 18, 18)
                .addComponent(jButton_reset)
                .addGap(0, 27, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addComponent(jLabel6)
                .addContainerGap(164, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_host, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(255, 255, 255)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1_login_result, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_testConnect)
                        .addComponent(jButton_login)
                        .addComponent(jButton_reset)
                        .addComponent(jButton_register)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

    private void jButton_loginActionPerformed(java.awt.event.ActionEvent evt) {                                              
        // 校验参数
        if (!checkParameter()) {
            //参数校验失败
            resetInput();
            return;
        }
        //登陆成功
        tinyClient client = tinyClient.getClient();
        
        client.setHost(host);
        client.setPort(port);
        client.setUsername(username);
        client.setPassword(password);
//        boolean isSuccess =false;
        boolean isSuccess =false;
        try {
             isSuccess  =  client.login(host, port, username, password);
        } catch (Exception ex) {
            Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //登陆成功
        if(isSuccess){
            //跳转到DBMS页面
            dbmsFrame = new DBMSFrame();
            this.setVisible(false);
            dbmsFrame.setVisible(true);
        }
        //登陆失败
        else{
            jLabel1_login_result.setForeground(Color.red);
            jLabel1_login_result.setText("登陆失败");
        }
        
    }                                             

    private void jButton_resetActionPerformed(java.awt.event.ActionEvent evt) {                                              
        resetInput();
    }                                             

    private void jTextField_usernameActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        // TODO add your handling code here:
    }                                                   

    private void jButton_testConnectActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        //测试服务器连通性
        //获取服务器ip 端口
        host = jTextField_host.getText();
        try {
            port = Integer.parseInt(jTextField_port.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "请填写正确的服务器ip、端口号", null, JOptionPane.INFORMATION_MESSAGE);           
        }     
        
        tinyClient client = tinyClient.getClient();
        boolean isAvailable = client.testConnect(host,port);
        
        if(isAvailable){
            jLabel1_login_result.setForeground(new Color(34,221,72));
            jLabel1_login_result.setText("与服务器连接通畅");
        }else{
             jLabel1_login_result.setForeground(Color.red);
            jLabel1_login_result.setText("与服务器连接失败");
        }
    }                                                   

    private void jButton_registerActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        // 校验参数
        if (!checkParameter()) {
            //参数校验失败
            resetInput();
            return;
        }
        //登陆成功
        tinyClient client = tinyClient.getClient();
        
        client.setHost(host);
        client.setPort(port);
        client.setUsername(username);
        client.setPassword(password);
//        boolean isSuccess =false;
        boolean isSuccess =false;
        try {
             isSuccess  =  client.register(host, port, username, password);
        } catch (Exception ex) {
            Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //登陆成功
        if(isSuccess){
            jLabel1_login_result.setForeground(new Color(34,221,72));
            jLabel1_login_result.setText("注册成功");
        }
        //登陆失败
        else{
            jLabel1_login_result.setForeground(Color.red);
            jLabel1_login_result.setText("注册失败");
        }
    }                                                

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
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }

    //frame对象
    private DBMSFrame dbmsFrame = null;
    String host, username, password;
    int port;

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton_login;
    private javax.swing.JButton jButton_register;
    private javax.swing.JButton jButton_reset;
    private javax.swing.JButton jButton_testConnect;
    private javax.swing.JLabel jLabel1_login_result;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPasswordField jPasswordField;
    private javax.swing.JTextField jTextField_host;
    private javax.swing.JTextField jTextField_port;
    private javax.swing.JTextField jTextField_username;
    // End of variables declaration                   

    private boolean checkParameter() {
        host = jTextField_host.getText();
        try {
            port = Integer.parseInt(jTextField_port.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "参数填写错误", null, JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        username = jTextField_username.getText();
        password = new String(jPasswordField.getPassword());
        return (host != null && !host.isEmpty()) && (username != null && !username.isEmpty()) && (password != null && !password.isEmpty()) && port > 0;
    }

    private void resetInput() {
        jTextField_host.setText("");
        jTextField_port.setText("");
        jTextField_username.setText("");
        jPasswordField.setText("");
        jLabel1_login_result.setText("");
    }

}
