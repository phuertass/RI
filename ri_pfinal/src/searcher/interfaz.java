/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package searcher;

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
import org.apache.lucene.store.Directory;

/**
 *
 * @author anne
 */
public class interfaz extends javax.swing.JFrame {

    private Searcher busqueda;
    String resultado_busqueda;
    String[] matriz;
    ArrayList<String> campos = new ArrayList<String>();
    String coleccion;
    String dir, fdir;
    Directory directorio;
    FSDirectory fdirectorio;

    private Boolean[][] filtros = new Boolean[][]{{false, false, false}, {false, false, false}, {false, false, false}, {false, false, false}};

    /**
     * Creates new form interfaz
     */
    public interfaz() {
        initComponents();
        spoken_wordsg.setVisible(false);
        raw_character_textg.setVisible(false);
        episode_id.setVisible(false);
        timestamp_in_ms.setVisible(false);
        raw_location_text.setVisible(false);
        spoken_words.setVisible(false);
        raw_character_text.setVisible(false);
        title.setVisible(false);
        imdb_rating.setVisible(false);
        original_air_date.setVisible(false);
        season.setVisible(false);
        todos.setVisible(false);
        todosg.setVisible(false);

        dir = obtenerRutaIndice();
        fdir = obtenerRutaFaceta();

        busqueda = new Searcher(dir, fdir);
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

        txtConsulta = new javax.swing.JTextField();
        botonConsulta = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tipoConsulta = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        tipoColeccion = new javax.swing.JComboBox<>();
        spoken_words = new javax.swing.JCheckBox();
        raw_character_text = new javax.swing.JCheckBox();
        imdb_rating = new javax.swing.JCheckBox();
        original_air_date = new javax.swing.JCheckBox();
        season = new javax.swing.JCheckBox();
        title = new javax.swing.JCheckBox();
        episode_id = new javax.swing.JCheckBox();
        timestamp_in_ms = new javax.swing.JCheckBox();
        raw_character_textg = new javax.swing.JCheckBox();
        raw_location_text = new javax.swing.JCheckBox();
        spoken_wordsg = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        todotexto = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        todos = new javax.swing.JCheckBox();
        todosg = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtConsulta.setText("Ingrese una consulta...");
        txtConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtConsultaActionPerformed(evt);
            }
        });

        botonConsulta.setText("Buscar");
        botonConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonConsultaActionPerformed(evt);
            }
        });

        jLabel1.setText("Tipo de consulta");

        tipoConsulta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Consulta Generica", "Consulta Booleana", "Consulta Frases", "Consulta Multiple" }));

        jLabel2.setText("Colección");

        tipoColeccion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Guiones", "Capitulos" }));
        tipoColeccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoColeccionActionPerformed(evt);
            }
        });

        spoken_words.setText("Diálogo");
        spoken_words.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spoken_wordsActionPerformed(evt);
            }
        });

        raw_character_text.setText("Personaje");
        raw_character_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                raw_character_textActionPerformed(evt);
            }
        });

        imdb_rating.setText("Puntuación");
        imdb_rating.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imdb_ratingActionPerformed(evt);
            }
        });

        original_air_date.setText("Fecha de estreno");
        original_air_date.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                original_air_dateActionPerformed(evt);
            }
        });

        season.setText("Temporada");
        season.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seasonActionPerformed(evt);
            }
        });

        title.setText("Título");
        title.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titleActionPerformed(evt);
            }
        });

        episode_id.setText("ID");
        episode_id.setActionCommand("");
        episode_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                episode_idActionPerformed(evt);
            }
        });

        timestamp_in_ms.setText("Instante de tiempo");
        timestamp_in_ms.setActionCommand("");
        timestamp_in_ms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timestamp_in_msActionPerformed(evt);
            }
        });

        raw_character_textg.setText("Personaje");
        raw_character_textg.setActionCommand("");
        raw_character_textg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                raw_character_textgActionPerformed(evt);
            }
        });

        raw_location_text.setText("Localización");
        raw_location_text.setActionCommand("");
        raw_location_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                raw_location_textActionPerformed(evt);
            }
        });

        spoken_wordsg.setText("Diálogo");
        spoken_wordsg.setActionCommand("");
        spoken_wordsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spoken_wordsgActionPerformed(evt);
            }
        });

        todotexto.setColumns(20);
        todotexto.setRows(5);
        jScrollPane1.setViewportView(todotexto);

        jLabel3.setText("RESULTADOS");

        todos.setText("Todos");
        todos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todosActionPerformed(evt);
            }
        });

        todosg.setText("Todos");
        todosg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todosgActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(70, 70, 70)
                        .addComponent(tipoConsulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtConsulta, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(botonConsulta))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tipoColeccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(episode_id)
                                    .addComponent(timestamp_in_ms)
                                    .addComponent(raw_character_textg)
                                    .addComponent(raw_location_text)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(19, 19, 19)
                                        .addComponent(jLabel3)))
                                .addGap(69, 69, 69)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(spoken_words)
                                            .addComponent(raw_character_text))
                                        .addGap(69, 69, 69)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(imdb_rating)
                                            .addComponent(title)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(original_air_date)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(season))
                                    .addComponent(todos)))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 699, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(spoken_wordsg)
                                .addGap(18, 18, 18)
                                .addComponent(todosg)))))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtConsulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonConsulta))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tipoConsulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tipoColeccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spoken_words)
                    .addComponent(title))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(episode_id)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timestamp_in_ms)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(raw_character_textg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(raw_location_text)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spoken_wordsg)
                            .addComponent(todosg))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(raw_character_text)
                            .addComponent(imdb_rating))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(original_air_date)
                            .addComponent(season))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(todos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtConsultaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtConsultaActionPerformed

    private void botonConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonConsultaActionPerformed
        if (txtConsulta.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese una consulta");
        } else {
            String q = txtConsulta.getText();
            String tipo = tipoConsulta.getSelectedItem().toString();

            campos = obtenerCamposCapitulos();
            if (tipoColeccion.getSelectedItem().toString() == "Guiones") {
                dir = "src/searcher/indicecaps";
                fdir = "src/searcher/facetascaps";
                try {
                    directorio = FSDirectory.open(Paths.get(dir));
                    fdirectorio = FSDirectory.open(Paths.get(fdir));
                } catch (IOException ex) {
                    Logger.getLogger(interfaz.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (tipoColeccion.getSelectedItem().toString() == "Capitulos") {
                dir = "src/searcher/indicecapunidos";
                fdir = "src/searcher/facetasunidos";
                try {
                    directorio = FSDirectory.open(Paths.get(dir));
                    fdirectorio = FSDirectory.open(Paths.get(fdir));
                } catch (IOException ex) {
                    Logger.getLogger(interfaz.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println(campos);
            resultado_busqueda = busqueda.indexSearch(q, tipo, campos, directorio, fdirectorio);
            todotexto.setText(resultado_busqueda);
        }
    }//GEN-LAST:event_botonConsultaActionPerformed

    private void spoken_wordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spoken_wordsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_spoken_wordsActionPerformed

    private void raw_character_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_raw_character_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_raw_character_textActionPerformed

    private void imdb_ratingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imdb_ratingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_imdb_ratingActionPerformed

    private void original_air_dateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_original_air_dateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_original_air_dateActionPerformed

    private void seasonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seasonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_seasonActionPerformed

    private void titleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_titleActionPerformed

    private void tipoColeccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoColeccionActionPerformed
        String seleccion = tipoColeccion.getSelectedItem().toString();

        if ("Guiones".equals(seleccion)) {
            ocultarCamposCapitulos();
            mostrarCamposGuiones();
        } else if ("Capitulos".equals(seleccion)) {
            ocultarCamposGuiones();
            mostrarCamposCapitulos();

        }
    }//GEN-LAST:event_tipoColeccionActionPerformed

    private void episode_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_episode_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_episode_idActionPerformed

    private void timestamp_in_msActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timestamp_in_msActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_timestamp_in_msActionPerformed

    private void raw_character_textgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_raw_character_textgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_raw_character_textgActionPerformed

    private void raw_location_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_raw_location_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_raw_location_textActionPerformed

    private void spoken_wordsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spoken_wordsgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_spoken_wordsgActionPerformed

    private void todosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_todosActionPerformed

    private void todosgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todosgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_todosgActionPerformed
    private void ocultarCamposCapitulos() {
        spoken_words.setVisible(false);
        raw_character_text.setVisible(false);
        title.setVisible(false);
        imdb_rating.setVisible(false);
        original_air_date.setVisible(false);
        season.setVisible(false);
        todos.setVisible(false);
    }

    private void ocultarCamposGuiones() {
        spoken_wordsg.setVisible(false);
        raw_character_textg.setVisible(false);
        episode_id.setVisible(false);
        timestamp_in_ms.setVisible(false);
        raw_location_text.setVisible(false);
        todosg.setVisible(false);
    }

    private void mostrarCamposCapitulos() {
        spoken_words.setVisible(true);
        raw_character_text.setVisible(true);
        title.setVisible(true);
        imdb_rating.setVisible(true);
        original_air_date.setVisible(true);
        season.setVisible(true);
        todos.setVisible(true);
    }

    private void mostrarCamposGuiones() {
        spoken_wordsg.setVisible(true);
        raw_character_textg.setVisible(true);
        episode_id.setVisible(true);
        timestamp_in_ms.setVisible(true);
        raw_location_text.setVisible(true);
        todosg.setVisible(true);
    }

    public String obtenerRutaIndice() {
        if (tipoColeccion.getSelectedItem().toString() == "Guiones") {
            dir = "./src/searcher/indicecaps";
            fdir = "./src/searcher/facetascaps";
            try {
                directorio = FSDirectory.open(Paths.get(dir));
                fdirectorio = FSDirectory.open(Paths.get(fdir));
            } catch (IOException ex) {
                Logger.getLogger(interfaz.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (tipoColeccion.getSelectedItem().toString() == "Capitulos") {
            dir = "./src/searcher/indicecapunidos";
            fdir = "./src/searcher/facetasunidos";
            try {
                directorio = FSDirectory.open(Paths.get(dir));
                fdirectorio = FSDirectory.open(Paths.get(fdir));
            } catch (IOException ex) {
                Logger.getLogger(interfaz.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dir;
    }

    String obtenerRutaFaceta() {
        if (tipoColeccion.getSelectedItem().toString() == "Guiones") {
            dir = "./src/searcher/indicecaps";
            fdir = "./src/searcher/facetascaps";

        }
        if (tipoColeccion.getSelectedItem().toString() == "Capitulos") {
            dir = "./src/searcher/indicecapunidos";
            fdir = "./src/searcher/facetasunidos";

        }
        return fdir;
    }
//}

    private ArrayList<String> obtenerCamposCapitulos() {

        if (spoken_words.isSelected()) {
            campos.add("spoken_words");
        }
        if (raw_character_text.isSelected()) {
            campos.add("raw_character_text");
        }
        if (title.isSelected()) {
            campos.add("title");
        }
        if (imdb_rating.isSelected()) {
            campos.add("imdb_rating");
        }
        if (original_air_date.isSelected()) {
            campos.add("original_air_date");
        }
        if (season.isSelected()) {
            campos.add("season");
        }

        if (spoken_wordsg.isSelected()) {
            campos.add("spoken_words");
        }
        if (raw_location_text.isSelected()) {
            campos.add("raw_location_text");
        }
        if (raw_character_textg.isSelected()) {
            campos.add("raw_character_text");
        }
        if (timestamp_in_ms.isSelected()) {
            campos.add("timestamp_in_ms");
        }
        if (episode_id.isSelected()) {
            campos.add("episode_id");
        }
        if (todos.isSelected()) {
            campos.add("TODO");
        }
        if (todosg.isSelected()) {
            campos.add("TODO");
        }

        return campos;
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
            java.util.logging.Logger.getLogger(interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*String directorioActual = "./src";

        // Crea un objeto File para representar el directorio actual
        File dir = new File(directorioActual);

        // Verifica si el directorio existe
        if (dir.exists() && dir.isDirectory()) {
            // Obtiene la lista de archivos en el directorio actual
            String[] archivos = dir.list();

            // Muestra la lista de archivos
            System.out.println("Archivos en el directorio actual:");
            for (String archivo : archivos) {
                System.out.println(archivo);
            }
        } else {
            System.out.println("El directorio actual no existe o no es un directorio válido.");
        }*/
 /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new interfaz().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonConsulta;
    private javax.swing.JCheckBox episode_id;
    private javax.swing.JCheckBox imdb_rating;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JCheckBox original_air_date;
    private javax.swing.JCheckBox raw_character_text;
    private javax.swing.JCheckBox raw_character_textg;
    private javax.swing.JCheckBox raw_location_text;
    private javax.swing.JCheckBox season;
    private javax.swing.JCheckBox spoken_words;
    private javax.swing.JCheckBox spoken_wordsg;
    private javax.swing.JCheckBox timestamp_in_ms;
    private javax.swing.JComboBox<String> tipoColeccion;
    private javax.swing.JComboBox<String> tipoConsulta;
    private javax.swing.JCheckBox title;
    private javax.swing.JCheckBox todos;
    private javax.swing.JCheckBox todosg;
    private javax.swing.JTextArea todotexto;
    private javax.swing.JTextField txtConsulta;
    // End of variables declaration//GEN-END:variables
}
