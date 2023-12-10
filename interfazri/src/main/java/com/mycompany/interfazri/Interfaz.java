/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.interfazri;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.lucene.document.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.*;
import java.util.*;
import java.nio.file.Paths;


import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.analysis.CharArraySet;


import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.LabelAndValue;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.QueryBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.lucene.facet.DrillSideways;
import org.apache.lucene.queryparser.classic.ParseException;
import java.awt.Color;

/**
 *
 * @author patri
 */
public class Interfaz extends javax.swing.JFrame {
    IndiceSearcherInterfaz index;
    String resultado_busqueda;
    String[] matriz;
    private Boolean[][] filtrosfacetas = new Boolean[][]{{false, false, false},{false,false,false},{false,false,false},{false,false,false}};
    
    /**
     * Creates new form Interfaz
     */
    public Interfaz() {
        getContentPane().setBackground(Color.lightGray); 
        initComponents();
        index = new IndiceSearcherInterfaz();   
        resultado_busqueda = new String();
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jQuery = new javax.swing.JTextField();
        jTipo = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jResultArea = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jButtonSalir = new javax.swing.JButton();
        jLimpiar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jGenre0 = new javax.swing.JCheckBox();
        jGenre1 = new javax.swing.JCheckBox();
        jGenre2 = new javax.swing.JCheckBox();
        jYear0 = new javax.swing.JCheckBox();
        jYear1 = new javax.swing.JCheckBox();
        jYear2 = new javax.swing.JCheckBox();
        jStars0 = new javax.swing.JCheckBox();
        jStars1 = new javax.swing.JCheckBox();
        jStars2 = new javax.swing.JCheckBox();
        jDirector0 = new javax.swing.JCheckBox();
        jDirector1 = new javax.swing.JCheckBox();
        jDirector2 = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 3, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(102, 0, 0));
        jLabel5.setText("BUSCADOR PRACTICAS RECUPERACION DE INFORMACION");

        jQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jQueryActionPerformed(evt);
            }
        });

        jTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Consulta Generica", "Consulta Booleana", "Consulta Frases"}));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel1.setText("Introduzca la consulta");

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel2.setText("Elige el tipo de consulta");

        jButton.setText("Buscar");
        jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActionPerformed(evt);
            }
        });

        jResultArea.setColumns(20);
        jResultArea.setRows(5);
        jScrollPane1.setViewportView(jResultArea);

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel3.setText("Resultados obtenidos");

        jButtonSalir.setText("Salir");
        jButtonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalirActionPerformed(evt);
            }
        });

        jLimpiar.setText("Limpiar busqueda");
        jLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLimpiarActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel4.setText("Filtrar la busqueda por facetas");

        jGenre0.setAutoscrolls(true);
        jGenre0.setEnabled(false);
        jGenre0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGenre0ActionPerformed(evt);
            }
        });

        jGenre1.setAutoscrolls(true);
        jGenre1.setEnabled(false);
        jGenre1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGenre1ActionPerformed(evt);
            }
        });

        jGenre2.setAutoscrolls(true);
        jGenre2.setEnabled(false);
        jGenre2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGenre2ActionPerformed(evt);
            }
        });

        jYear0.setToolTipText("");
        jYear0.setEnabled(false);
        jYear0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jYear0ActionPerformed(evt);
            }
        });

        jYear1.setEnabled(false);
        jYear1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jYear1ActionPerformed(evt);
            }
        });

        jYear2.setEnabled(false);
        jYear2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jYear2ActionPerformed(evt);
            }
        });

        jStars0.setEnabled(false);
        jStars0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStars0ActionPerformed(evt);
            }
        });

        jStars1.setEnabled(false);
        jStars1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStars1ActionPerformed(evt);
            }
        });

        jStars2.setEnabled(false);
        jStars2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStars2ActionPerformed(evt);
            }
        });

        jDirector0.setEnabled(false);
        jDirector0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDirector0ActionPerformed(evt);
            }
        });

        jDirector1.setEnabled(false);
        jDirector1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDirector1ActionPerformed(evt);
            }
        });

        jDirector2.setEnabled(false);
        jDirector2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDirector2ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel6.setText("El filtrado de facetas se puede hacer por: Genre, Year, Stars o Director");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jQuery))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton)
                            .addComponent(jLimpiar))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonSalir)
                        .addGap(17, 17, 17))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jGenre1)
                            .addComponent(jGenre2)
                            .addComponent(jYear0)
                            .addComponent(jYear1)
                            .addComponent(jYear2)
                            .addComponent(jStars0)
                            .addComponent(jStars1)
                            .addComponent(jStars2)
                            .addComponent(jDirector0)
                            .addComponent(jDirector1)
                            .addComponent(jDirector2)
                            .addComponent(jGenre0)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(275, 275, 275))))))
            .addGroup(layout.createSequentialGroup()
                .addGap(215, 215, 215)
                .addComponent(jLabel5)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel5)
                .addGap(55, 55, 55)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jQuery, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLimpiar))
                .addGap(64, 64, 64)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonSalir)
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(jGenre0)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jGenre1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jGenre2)
                        .addGap(18, 18, 18)
                        .addComponent(jYear0)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jYear1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jYear2)
                        .addGap(18, 18, 18)
                        .addComponent(jStars0)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jStars1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jStars2)
                        .addGap(17, 17, 17)
                        .addComponent(jDirector0)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDirector1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDirector2)
                        .addContainerGap(188, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jQueryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jQueryActionPerformed

    private void jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActionPerformed
        // TODO add your handling code here:
        if(jQuery.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Introduzca la consulta");
        }else{
            //obtengo texto consulta
            String consulta= jQuery.getText();
            //obtengo tipo consulta
            String value = jTipo.getSelectedItem().toString();
            
            //llamo al metodo
            resultado_busqueda = index.indexSearch(consulta, value);
            
            //le paso a la caja los resultados de la busqueda
            jResultArea.setText(resultado_busqueda);
            //jResultArea.append(resultado_busqueda);
            jButton.setEnabled(true);
            
            //preparo las categorias de los filtros de las facetas
            matriz = index.getMatriz();
            
            jGenre0.setText(matriz[0]); //asignamos valor
            jGenre0.setEnabled(true); //asctivamos boton
            jGenre1.setText(matriz[1]);
            jGenre1.setEnabled(true);
            jGenre2.setText(matriz[2]);
            jGenre2.setEnabled(true);
            
            jYear0.setText(matriz[9]);
            jYear0.setEnabled(true);
            jYear1.setText(matriz[10]);
            jYear1.setEnabled(true);
            jYear2.setText(matriz[11]);
            jYear2.setEnabled(true);
            
            jStars0.setText(matriz[6]);
            jStars0.setEnabled(true);
            jStars1.setText(matriz[7]);
            jStars1.setEnabled(true);
            jStars2.setText(matriz[8]);
            jStars2.setEnabled(true);
            
            jDirector0.setText(matriz[3]);
            jDirector0.setEnabled(true);
            jDirector1.setText(matriz[4]);
            jDirector1.setEnabled(true);
            jDirector2.setText(matriz[5]);
            jDirector2.setEnabled(true);
            
            jResultArea.setCaretPosition(0);
        }      
    }//GEN-LAST:event_jButtonActionPerformed
   
    private void MostrarFacetaInterfaz(){
        String escogidas=new String();
        
        for(int i=0; i<4; i++){ //recorro las 4 facetas
            for(int j=0; j<3; j++){ //recorro las 3 subcategorias
                if(filtrosfacetas[i][j]){
                    int seleccionada = i*3+j;
                    escogidas += seleccionada + " ";
        	}
            }
        }
        
        if(!escogidas.equals("")){
        	String result2 = index.FiltrarPorFacetas(escogidas);
                jResultArea.setText(result2);
        	//jResultArea.append(result2);
        }else{
            jResultArea.setText(resultado_busqueda);
            //jResultArea.append(resultado_busqueda);
        }
        
        jResultArea.setCaretPosition(0);
    }
    
    
    private void jButtonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalirActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButtonSalirActionPerformed

    private void jLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLimpiarActionPerformed
        // TODO add your handling code here:
        jResultArea.setText(null);
        jQuery.setText(null);
    }//GEN-LAST:event_jLimpiarActionPerformed

    private void jGenre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGenre1ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[0][1]=jGenre1.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jGenre1ActionPerformed

    private void jGenre0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGenre0ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[0][0]=jGenre0.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jGenre0ActionPerformed

    private void jGenre2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGenre2ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[0][2]=jGenre2.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jGenre2ActionPerformed

    private void jYear0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jYear0ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[3][0]=jYear0.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jYear0ActionPerformed

    private void jYear1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jYear1ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[3][1]=jYear1.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jYear1ActionPerformed

    private void jYear2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jYear2ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[3][2]=jYear2.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jYear2ActionPerformed

    private void jStars0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStars0ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[2][0]=jStars0.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jStars0ActionPerformed

    private void jStars1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStars1ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[2][1]=jStars1.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jStars1ActionPerformed

    private void jStars2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStars2ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[2][2]=jStars2.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jStars2ActionPerformed

    private void jDirector2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDirector2ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[1][2]=jDirector2.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jDirector2ActionPerformed

    private void jDirector0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDirector0ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[1][0]=jDirector0.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jDirector0ActionPerformed

    private void jDirector1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDirector1ActionPerformed
        // TODO add your handling code here:
        filtrosfacetas[1][1]=jDirector1.isSelected();
        MostrarFacetaInterfaz();
    }//GEN-LAST:event_jDirector1ActionPerformed

   
     
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
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Interfaz().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton;
    private javax.swing.JButton jButtonSalir;
    private javax.swing.JCheckBox jDirector0;
    private javax.swing.JCheckBox jDirector1;
    private javax.swing.JCheckBox jDirector2;
    private javax.swing.JCheckBox jGenre0;
    private javax.swing.JCheckBox jGenre1;
    private javax.swing.JCheckBox jGenre2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JButton jLimpiar;
    private javax.swing.JTextField jQuery;
    private javax.swing.JTextArea jResultArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JCheckBox jStars0;
    private javax.swing.JCheckBox jStars1;
    private javax.swing.JCheckBox jStars2;
    private javax.swing.JComboBox<String> jTipo;
    private javax.swing.JCheckBox jYear0;
    private javax.swing.JCheckBox jYear1;
    private javax.swing.JCheckBox jYear2;
    // End of variables declaration//GEN-END:variables
}
