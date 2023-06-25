package com.example.fact_express.Actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fact_express.CRUD.Access_Venta;
import com.example.fact_express.Modelos.Ventas;
import com.example.fact_express.R;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReportePDF_BD extends AppCompatActivity {

    private Paragraph parrafo;
    private Document documento;
    private File file;
    private Font fTitulo = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
    private Font fsubTitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private Font fTexto = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private Font fresaltado = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.RED);

    String fechaactual;


    private ArrayList<Ventas> rows = new ArrayList<>();

    private String [] header={"OPER. N°","TIMBRADO","FACTURA N°","CONDICIÓN","FECHA/HORA","CLIENTE","PRODUCTO","IVA","CANTIDAD","PRECIO"
            ,"SUB-TOTAL","ANULADO"};

    private String textoCorto="PRUEBA";
    private String textoLargo="Un DatePicker se ve genial, y funciona de maravilla.\n" +
            "\n" +
            "El detalle es que no viene enlazado a ningún campo.\n" +
            "\n" +
            "Y resulta muchas veces inviable para nosotros situarlo directamente sobre nuestros formularios, por el espacio que demanda.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_pdf_bd);

        long ahora = System.currentTimeMillis();
        Date fecha = new Date(ahora);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        fechaactual = df.format(fecha);

        ((Button)findViewById(R.id.bt_Generar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPDF();
                ViewPDFApp();
            }
        });
    }

    private ArrayList<String[]>getVentas(){
        Access_Venta db = Access_Venta.getInstance(getApplicationContext());
        db.openWritable();
        Cursor c = db.getv_ventadetalle(fechaactual);
        ArrayList<String[]> rows = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                rows.add(new String[]{String.valueOf(c.getInt(0)), c.getString(4),c.getString(1)+"-"+c.getString(2)+"-"+c.getString(3)
                ,c.getString(5),c.getString(6),c.getString(8)+"  -  "+c.getString(7),c.getString(16),
                String.valueOf(c.getInt(20)+"%"),c.getString(17)+" "+c.getString(21),String.valueOf(c.getInt(18)),
                String.valueOf(c.getInt(19)),Estado(c.getString(14))});
            }while (c.moveToNext());
        }
        return rows;
    }
    public String Estado(String estado){
        String est;
        if(estado.equals("S")){
            est="NO";
        }else{
            est="SI";
        }
        return est;
    }

    public void crearPDF(){
        documento = new Document(PageSize.B2,5,5,5,5);
        try{
            file = crearFichero("TEST_REPORT"+"fechadehoy"+".pdf");
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());
            PdfWriter writer = PdfWriter.getInstance(documento,ficheroPDF);
            documento.open();
            addMetaData("Reporte","Venta diaria", "nombre vendedor");
            addTitulo("Reporte de Ventas","Ventas diaras", fechaactual);
            addParrafo(textoCorto);
            addParrafo(textoLargo);
            CreateTabla(header,getVentas());

        } catch (DocumentException e) {
        }catch (IOException e){
        }finally {
            documento.close();
        }
    }
    public File crearFichero(String nombreFichero){
        File ruta = getRuta();

        File fichero = null;
        if(ruta!=null){
            fichero = new File(ruta, nombreFichero);
        }

        return fichero;
    }

    public File getRuta(){
        File ruta = null;

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            ruta= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"FACT_EXPRESS_REPORTES");

            if(!ruta.mkdir()){
                if(!ruta.exists()){
                    return null;
                }

            }
        }
        return ruta;
    }

    public void addMetaData(String titulo, String tema, String autor){
        documento.addTitle(titulo);
        documento.addSubject(tema);
        documento.addAuthor(autor);
    }

    public void addTitulo(String titulo, String subTitulo, String fecha){
        parrafo = new Paragraph();
        addParrafoHijo( new Paragraph(titulo,fTitulo));
        addParrafoHijo( new Paragraph(subTitulo,fsubTitulo));
        addParrafoHijo( new Paragraph("Generado el: "+fecha,fresaltado));
        parrafo.setSpacingAfter(30);
        try{
            documento.add(parrafo);
        }catch (Exception e){
            Log.e("addTitulo",e.toString());
        }

    }

    private void addParrafoHijo(Paragraph parrafoHijo){
        parrafoHijo.setAlignment(Element.ALIGN_CENTER);
        parrafo.add(parrafoHijo);
    }

    public void addParrafo(String texto){
        parrafo = new Paragraph(texto,fTexto);
        parrafo.setSpacingAfter(5);
        parrafo.setSpacingBefore(5);
        try{
            documento.add(parrafo);
        }catch (Exception e){
            Log.e("addParrafo",e.toString());
        }
    }

    public void CreateTabla(String[] header, ArrayList<String []>Venta){
        parrafo = new Paragraph();
        parrafo.setFont(fTexto);
        PdfPTable pdfPTable =  new PdfPTable(header.length);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setSpacingBefore(20);
        PdfPCell pdfPCell;
        int indexC=0;
        while (indexC<header.length){
            pdfPCell = new PdfPCell(new Phrase(header[indexC++],fTexto));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBorder(2);
            pdfPCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pdfPTable.addCell(pdfPCell);
        }

        for (int indexRow=0; indexRow<Venta.size();indexRow++){
            String[] row = Venta.get(indexRow);
            for (indexC=0; indexC<header.length;indexC++){
                pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBorder(2);
                pdfPTable.addCell(pdfPCell);
            }
        }
        parrafo.add(pdfPTable);
        try{
            documento.add(parrafo);
            Toast.makeText(getApplicationContext(),"Reporte generado exitosamente!",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Log.e("CreateTabla",e.toString());
        }
    }

    public void ViewPDFApp( ){
        try{
            if(file.exists()){
                Uri uri = null;//= Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(this,
                            this.getApplicationContext().getPackageName() + ".provider", file);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                }else{
                    intent.setDataAndType(uri, "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                }

                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                    Toast.makeText(getApplicationContext(),"No cuentas con una APP para visualizar su Reporte",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Reporte no encontrado",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Log.e("ViewPDFApp",e.getMessage());
        }

    }
}