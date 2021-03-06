package com.pos.leaders.leaderspossystem.Printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.pos.leaders.leaderspossystem.DataBaseAdapter.ChecksDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.CreditCardPaymentDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.Currency.CashPaymentDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.Currency.CurrencyDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.Currency.CurrencyTypeDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.EmployeeDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.OpiningReportDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.OpiningReportDetailsDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.OrderDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.PosInvoiceDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.XReportDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.ZReportDBAdapter;
import com.pos.leaders.leaderspossystem.DocumentType;
import com.pos.leaders.leaderspossystem.Models.Check;
import com.pos.leaders.leaderspossystem.Models.CreditCardPayment;
import com.pos.leaders.leaderspossystem.Models.Currency.CashPayment;
import com.pos.leaders.leaderspossystem.Models.Currency.Currency;
import com.pos.leaders.leaderspossystem.Models.Currency.CurrencyOperation;
import com.pos.leaders.leaderspossystem.Models.Currency.CurrencyType;
import com.pos.leaders.leaderspossystem.Models.Employee;
import com.pos.leaders.leaderspossystem.Models.InvoiceStatus;
import com.pos.leaders.leaderspossystem.Models.OpiningReport;
import com.pos.leaders.leaderspossystem.Models.Order;
import com.pos.leaders.leaderspossystem.Models.OrderDetails;
import com.pos.leaders.leaderspossystem.Models.Payment;
import com.pos.leaders.leaderspossystem.Models.PosInvoice;
import com.pos.leaders.leaderspossystem.Models.XReport;
import com.pos.leaders.leaderspossystem.Models.ZReport;
import com.pos.leaders.leaderspossystem.R;
import com.pos.leaders.leaderspossystem.Tools.CONSTANT;
import com.pos.leaders.leaderspossystem.Tools.DateConverter;
import com.pos.leaders.leaderspossystem.Tools.SETTINGS;
import com.pos.leaders.leaderspossystem.Tools.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.pos.leaders.leaderspossystem.Tools.Util.currencyOperationPaymentList;
import static com.pos.leaders.leaderspossystem.Tools.Util.paymentList;

/**
 * Created by Karam on 26/12/2016.
 */

public class BitmapInvoice {
    static  int invoiceReceiptCount=0 ,invoiceCount=0 , CreditInvoiceCount=0 ,
            firstTypeCount=0 ,secondTypeCount=0 , thirdTypeCount=0,fourthTypeCount=0 ,checkCount=0 , creditCardCount=0 ,receiptInvoiceAmountCheck=0 , cashCount=0,receiptInvoiceAmount=0;
    static  double cashAmount=0;
   static List<List<Check>> checkList = new ArrayList<>();
    static List<OpiningReport> opiningReportList=new ArrayList<>();
    static double aReportDetailsForFirstCurrency=0;
    static double aReportDetailsForSecondCurrency=0;
    static double aReportDetailsForThirdCurrency=0;
    static double aReportDetailsForForthCurrency=0;
    static double aReportAmount = 0;
    public static Bitmap print(int id, List<OrderDetails> orders, Order sale, boolean isCopy, Employee user, Context context) {
        //miriam_libre_bold.ttf
        //miriam_libre_regular.ttf
        //carmelitregular.ttf
        int PAGE_WIDTH = 800;
        StaticLayout sHead;
        String status = context.getString(R.string.source_invoice);
        Typeface plain = Typeface.createFromAsset(context.getAssets(), "miriam_libre_regular.ttf");
        Typeface normal = Typeface.create(plain, Typeface.NORMAL);
        Typeface bold = Typeface.create(plain, Typeface.BOLD);

        TextPaint head = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        head.setStyle(Paint.Style.FILL);
        head.setColor(Color.BLACK);
        head.setTypeface(normal);
        head.setTextSize(40);
        if (isCopy)
            status = context.getString(R.string.copy_invoice);
        if (SETTINGS.company.name().equals("BO_EXEMPT_DEALER")){
            sHead = new StaticLayout(context.getString(R.string.privet_company_status) + ":" + SETTINGS.companyID + "\n\r" + status, head,
                    PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
        }
        else {
             sHead = new StaticLayout(context.getString(R.string.private_company) + ":" + SETTINGS.companyID + "\n\r" + status, head,
                    PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
        }



        TextPaint invoiceHead = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceHead.setStyle(Paint.Style.FILL);
        invoiceHead.setColor(Color.BLACK);
        invoiceHead.setTypeface(bold);
        invoiceHead.setTextSize(60);
        StaticLayout sInvoiceHead = new StaticLayout(context.getString(R.string.invoice_with_tax) + " " + String.format("%06d", id) + "\n\r-----------------------------", invoiceHead,
                PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);


        TextPaint invoiceD = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceD.setStyle(Paint.Style.FILL);
        invoiceD.setColor(Color.BLACK);
        invoiceD.setTypeface(bold);

        invoiceD.setTextSize(40);
        // invoiceD.setTextAlign(Paint.Align.CENTER);

        StaticLayout sInvoiceD = new StaticLayout("פריט        \t\t\t\t\t\t\t\t\t מק''ט  \t\t\t\t\t\t כמות \t מחיר", invoiceD,
                PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);

        TextPaint orderTP = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        orderTP.setStyle(Paint.Style.FILL);
        orderTP.setColor(Color.BLACK);
        orderTP.setTypeface(normal);
        orderTP.setTextSize(40);
        orderTP.setTextAlign(Paint.Align.LEFT);
        orderTP.setLinearText(true);


        String names = "", prices = "", count = "", barcode = "";
        for (OrderDetails o : orders) {
            int cut = 10;
            if (o.getProduct().getDisplayName().length() < 10)
                cut = o.getProduct().getDisplayName().length() - 1;
            String productName = o.getProduct().getDisplayName().substring(0, cut);
            names += "\u200F" + productName + "\n";
            barcode += o.getProduct().getSku() + "\n";
            count += o.getQuantity() + "\n";
            prices += String.format(new Locale("en"), "%.2f", o.getItemTotalPrice()) + "\n";
        }
        StaticLayout slNames = new StaticLayout(names, orderTP,
                (int) (PAGE_WIDTH * 0.35), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slCount = new StaticLayout(count, orderTP,
                (int) (PAGE_WIDTH * 0.1), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slBarcode = new StaticLayout(barcode, orderTP,
                (int) (PAGE_WIDTH * 0.35), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slPrices = new StaticLayout(prices, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

        //orderTP.setTextSize(60);
        //orderTP.setTypeface(bold);


        StaticLayout sNewLine = new StaticLayout("-----------------------------", invoiceHead,
                PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);


        TextPaint ntp = new TextPaint(invoiceHead);
        ntp.setTextAlign(Paint.Align.LEFT);
        StaticLayout slTotalText = new StaticLayout("סה''כ", ntp,
                (int) (PAGE_WIDTH * 0.35), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, true);

        StaticLayout slTotalPrice = new StaticLayout(String.format(new Locale("en"), "%.2f", sale.getTotalPrice()), ntp,
                (int) (PAGE_WIDTH * 0.35), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, true);


        TextPaint tax = new TextPaint(ntp);
        tax.setTextSize(35);

        StaticLayout slTaxText = new StaticLayout("חייב במע''מ" + "\n" + "מע''מ " + SETTINGS.tax + " % \n" + "לא חייב במע''מ", tax,
                (int) (PAGE_WIDTH * 0.35), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, true);

        double notax = sale.getTotalPrice() / (1 + (SETTINGS.tax / 100));
        StaticLayout slTaxNumber = new StaticLayout(String.format(new Locale("en"), "%.2f\n%.2f\n%.2f", notax, notax * (SETTINGS.tax / 100), 0.0f), tax,
                (int) (PAGE_WIDTH * 0.35), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, true);

        //// TODO: 29/12/2016 proint tootlal price

        /*
        String str="";
        for (Order o : orders) {
            int cut=10;
            if( o.getProduct().getDisplayName().length()<10)
                cut=o.getProduct().getDisplayName().length()-1;
            String productName=o.getProduct().getDisplayName().substring(0,cut);
            while (productName.length()<12){
                productName+="\u3000";
            }
            String Barcode=o.getProduct().getSku();
            while(Barcode.length()<12){
                Barcode+="\u2007";
            }
            //u2003
            //\u3000
            str+="\u200F"+productName+"\t\t\t"+Barcode+"\t\t"+o.getQuantity()+"\t\t"+String.format(new Locale("en"),"%.2f",o.getItemTotalPrice());
            if(true)
                str+="\n";
        }
        Log.i("str",str);
         //String.format(new Locale("he"),str)
        StaticLayout sOrderTP = new StaticLayout(str, orderTP,
                PAGE_WIDTH, Layout.Alignment.ALIGN_NORMAL,1.0f,1.0f,false);

*/


        // Create bitmap and canvas to draw to
        //RGB_565
        int Page_Height = sHead.getHeight() + sInvoiceHead.getHeight() + sInvoiceD.getHeight() + slNames.getHeight() + sNewLine.getHeight() + slTotalText.getHeight() + slTaxText.getHeight();
        Bitmap b = Bitmap.createBitmap(PAGE_WIDTH, Page_Height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);


        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        sHead.draw(c);
        c.translate(0, sHead.getHeight());
        sInvoiceHead.draw(c);
        c.translate(0, sInvoiceHead.getHeight());
        sInvoiceD.draw(c);
        c.translate(0, sInvoiceD.getHeight());


        slPrices.draw(c);
        c.translate(slPrices.getWidth(), 0);
        slCount.draw(c);
        c.translate(slCount.getWidth(), 0);
        slBarcode.draw(c);
        c.translate(slBarcode.getWidth(), 0);
        slNames.draw(c);


        c.translate(-(int) (PAGE_WIDTH * 0.65), slNames.getHeight());
        sNewLine.draw(c);

        /*total price*/

        c.translate(0, sNewLine.getHeight());
        slTotalPrice.draw(c);
        c.translate(slTotalText.getWidth(), 0);
        slTotalText.draw(c);


        c.translate(-slTotalText.getWidth(), slTotalPrice.getHeight());
        slTaxNumber.draw(c);
        c.translate(slTaxNumber.getWidth(), 0);
        slTaxText.draw(c);


        c.restore();

        return b;

    }

    public static Bitmap textAsBitmap(String text, float textSize, int textColor, int width, int height, Context context) {
        Typeface plain = Typeface.createFromAsset(context.getAssets(), "miriam_libre_regular.ttf");
        Typeface normal = Typeface.create(plain, Typeface.NORMAL);
        Typeface bold = Typeface.create(plain, Typeface.BOLD);

        TextPaint head = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        head.setStyle(Paint.Style.FILL);
        head.setColor(Color.BLACK);
        head.setTypeface(normal);
        head.setTextSize(40);


        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.RIGHT);
        float baseline = -paint.ascent(); //ascent() is negative
        //int width = (int) (paint.measureText(text) + 0.5f); // round
        //int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public static Bitmap drawText(String text, int textWidth, int textSize, Context context) {
        // Get text dimensions
        Typeface plain = Typeface.createFromAsset(context.getAssets(), "miriam_libre_regular.ttf");
        Typeface normal = Typeface.create(plain, Typeface.BOLD);

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTypeface(normal);
        textPaint.setTextSize(textSize);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        // Create bitmap and canvas to draw to
        //RGB_565
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);


        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

    public static Bitmap zPrint(Context context, ZReport zReport ,  boolean isCopy) {
        List<CurrencyType> currencyTypesList = null;
        CurrencyTypeDBAdapter currencyTypeDBAdapter = new CurrencyTypeDBAdapter(context);
        currencyTypeDBAdapter.open();
        currencyTypesList = currencyTypeDBAdapter.getAllCurrencyType();
        currencyTypeDBAdapter.close();
        int layOutHight=0;
        int layOutCurrencyHight=0;

        getCountForZReport(context,zReport);
        ZReportDBAdapter zReportDBAdapter =new ZReportDBAdapter(context);
        zReportDBAdapter.open();
        EmployeeDBAdapter employeeDBAdapter = new EmployeeDBAdapter(context);
        employeeDBAdapter.open();
        zReport.setUser( employeeDBAdapter.getEmployeeByID(zReport.getByUser()));
        //miriam_libre_bold.ttf
        //miriam_libre_regular.ttf
        //carmelitregular.ttf
        int PAGE_WIDTH = CONSTANT.PRINTER_PAGE_WIDTH;

        String status = context.getString(R.string.source_invoice);

        Typeface plain = Typeface.createFromAsset(context.getAssets(), "carmelitregular.ttf");
        Typeface normal = Typeface.create(plain, Typeface.NORMAL);
        Typeface bold = Typeface.create(plain, Typeface.BOLD);

        TextPaint head = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        head.setStyle(Paint.Style.FILL);
        head.setColor(Color.BLACK);
        head.setTypeface(normal);
        head.setTextSize(38);
        TextPaint posSaleStyle =new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        posSaleStyle.setStyle(Paint.Style.FILL);
        posSaleStyle.setColor(Color.BLACK);
        posSaleStyle.setTypeface(normal);
        posSaleStyle.setTextSize(28);
        posSaleStyle.setTextAlign(Paint.Align.LEFT);
        posSaleStyle.setLinearText(true);
        TextPaint opiningReportStyle =new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        opiningReportStyle.setStyle(Paint.Style.FILL);
        opiningReportStyle.setColor(Color.BLACK);
        opiningReportStyle.setTypeface(normal);
        opiningReportStyle.setTextSize(28);
        opiningReportStyle.setTextAlign(Paint.Align.LEFT);
        opiningReportStyle.setLinearText(true);
        TextPaint orderTP = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        orderTP.setStyle(Paint.Style.FILL);
        orderTP.setColor(Color.BLACK);
        orderTP.setTypeface(normal);
        orderTP.setTextSize(25);
        orderTP.setLinearText(true);
        StaticLayout sHead;
        if (isCopy)
            status = context.getString(R.string.copy_invoice);
        if(SETTINGS.company.name().equals("BO_EXEMPT_DEALER")){
        sHead = new StaticLayout(context.getString(R.string.privet_company_status) + ":" + SETTINGS.companyID + "\n\r" + status + "\n\r" + DateConverter.dateToString(zReport.getCreatedAt().getTime()) + "\n\r" + "קןפאי : " + zReport.getUser().getFullName()+ status , head,
                PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);}
        else {
             sHead = new StaticLayout(context.getString(R.string.private_company) + ":" + SETTINGS.companyID + "\n\r" + status + "\n\r" + DateConverter.dateToString(zReport.getCreatedAt().getTime()) + "\n\r" + "קןפאי : " + zReport.getUser().getFullName()+ status , head,
                    PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
        }
        StaticLayout posSales = new StaticLayout( "\n"  +context.getString(R.string.pos_sales)+" "+Util.makePrice(zReport.getTotalPosSales()), posSaleStyle,
                PAGE_WIDTH, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, true);
        StaticLayout opiningReport=  new StaticLayout("\n"+"\u200F"+context.getString(R.string.opining_report) +  "\t\t\t\t\t"+" "  , opiningReportStyle,
                PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout opiningReportAmountLayout = new StaticLayout("\u200F"+context.getString(R.string.amount) +  "\t\t\t\t\t"+aReportAmount  , opiningReportStyle,
                PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout opiningReportStyleLayoutCount = new StaticLayout("\u200F"+context.getString(R.string.count) +  "\t\t\t\t\t"+opiningReportList.size()  , opiningReportStyle,
                PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        List<StaticLayout> currencyDetailsLayOut=new ArrayList<>();

        if(SETTINGS.enableCurrencies){
            String currencyType="" , currencyAmount="";
            currencyType +="\u200F"+ "ILS"+"\n"+"\u200F"+ "USD" +"\n" + "\u200F"+"GBP" + "\n" + "\u200F"+"EUR";
            currencyAmount+="\u200F"+ aReportDetailsForFirstCurrency+"\n"+"\u200F"+ aReportDetailsForSecondCurrency +"\n" + "\u200F"+aReportDetailsForThirdCurrency + "\n" + "\u200F"+aReportDetailsForForthCurrency;
            StaticLayout firstCurrencyLayOut =new StaticLayout("\u200F"+currencyTypesList.get(0).getType()+  "\t\t\t\t\t"+aReportDetailsForFirstCurrency  , opiningReportStyle,
                     PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
            StaticLayout secondCurrencyLayOut =new StaticLayout("\u200F"+currencyTypesList.get(1).getType() +  "\t\t\t\t\t"+aReportDetailsForSecondCurrency  , opiningReportStyle,
                    PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
            StaticLayout thirdCurrencyLayOut =new StaticLayout("\u200F"+currencyTypesList.get(2).getType()+  "\t\t\t\t\t"+aReportDetailsForThirdCurrency  , opiningReportStyle,
                    PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
            StaticLayout fourthCurrencyLayOut =new StaticLayout("\u200F"+currencyTypesList.get(3).getType() +  "\t\t\t\t\t"+aReportDetailsForForthCurrency  , opiningReportStyle,
                    PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
           currencyDetailsLayOut.add(firstCurrencyLayOut);
            currencyDetailsLayOut.add(secondCurrencyLayOut);
            currencyDetailsLayOut.add(thirdCurrencyLayOut);
            currencyDetailsLayOut.add(fourthCurrencyLayOut);
            layOutCurrencyHight+=firstCurrencyLayOut.getHeight();
            layOutCurrencyHight+=secondCurrencyLayOut.getHeight();
            layOutCurrencyHight+=thirdCurrencyLayOut.getHeight();
            layOutCurrencyHight+=fourthCurrencyLayOut.getHeight();


        }
        TextPaint invoiceHead = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceHead.setStyle(Paint.Style.FILL);
        invoiceHead.setColor(Color.BLACK);
        invoiceHead.setTypeface(bold);
        invoiceHead.setTextSize(38);
        StaticLayout sInvoiceHead = new StaticLayout(context.getString(R.string.z_number) + " " + String.format("%06d", zReport.getzReportId()) + "\n\r---------------------------", invoiceHead,
                PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);


        TextPaint invoiceD = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceD.setStyle(Paint.Style.FILL);
        invoiceD.setColor(Color.BLACK);
        invoiceD.setTypeface(bold);
        invoiceD.setTextSize(28);
        invoiceD.setLinearText(true);

        //invoiceD.setTextAlign(Paint.Align.LEFT);

        TextPaint invoiceCD = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceCD.setStyle(Paint.Style.FILL);
        invoiceCD.setColor(Color.BLACK);
        invoiceCD.setTypeface(bold);
        invoiceCD.setTextSize(28);
        invoiceCD.setLinearText(true);

        String names = "", in = "", out = "", total = "";

        StaticLayout sInvoiceD = new StaticLayout("\u200F"+context.getString(R.string.details) +  "\t\t\t\t\t\t\t\t\t"+context.getString(R.string.count) + "\t\t\t\t\t\t\t\t\t" + context.getString(R.string.total) , invoiceD,
                PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

        StaticLayout scInvoiceD =new StaticLayout("\u200F"+context.getString(R.string.currency) +  "\t\t\t\t\t\t\t\t\t"+context.getString(R.string.count) + "\t\t\t\t\t\t" + context.getString(R.string.total) , invoiceD,
                PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout checkHead =new StaticLayout("\u200F"+context.getString(R.string.checks) +  "\t\t\t\t\t\t\t\t\t\t" + context.getString(R.string.date)+  "\t\t\t\t\t\t" + context.getString(R.string.amount) , invoiceD,
                PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

        if (SETTINGS.company.name().equals("BO_EXEMPT_DEALER")){
            names +="\u200F"+ context.getString(R.string.invoice_recipte_company_status) +"\n" + "\u200F"+context.getString(R.string.invoice_company_status) + "\n" + "\u200F"+context.getString(R.string.credit_invoice_doc_company_status) + "\n"+"\u200F"+context.getString(R.string.total_sales);
        }
        else {
            names +="\u200F"+ context.getString(R.string.invoice_receipt) +"\n" + "\u200F"+context.getString(R.string.invoice) + "\n" + "\u200F"+context.getString(R.string.credit_invoice_doc) + "\n"+"\u200F"+context.getString(R.string.total_sales);
        }


        in +=  invoiceReceiptCount + "\n" +invoiceCount + "\n" +CreditInvoiceCount + "\n" +"~";
        out +=  " " + "\n" +" " + "\n" +"  ";
        total += dTS(zReport.getInvoiceReceiptAmount()) + "\n" + dTS(zReport.getInvoiceAmount()) + "\n" + dTS(zReport.getCreditInvoiceAmount()) + "\n" +dTS(zReport.getTotalSales());
        StaticLayout slNames = new StaticLayout(names, orderTP,
                (int) (PAGE_WIDTH * 0.30), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slIn = new StaticLayout(in, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slOut = new StaticLayout(out, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slTotal = new StaticLayout(total, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

        //orderTP.setTextSize(60);
        //orderTP.setTypeface(bold);


        StaticLayout sNewLine = new StaticLayout("-----------------------------", invoiceHead,
                PAGE_WIDTH, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 1.0f, true);



        String cIn = "", cOut = "", cTotal = "";
        if(SETTINGS.enableCurrencies) {
            names = "\u200F"+ context.getString(R.string._cash)+"\n"+"\u200F"+currencyTypesList.get(0).getType() + "\n"+"\u200F"+
                    currencyTypesList.get(1).getType()+ "\n" +"\u200F"+ currencyTypesList.get(2).getType() + "\n" +"\u200F"+
                    currencyTypesList.get(3).getType()+  "\n" +"\u200F"+ context.getString(R.string.checks)+ "\n" +"\u200F"+ context.getString(R.string.credit_card)+"\n" + "\u200F"+context.getString(R.string.total_with_a_report_amount);
            cIn =cashCount+"\n"+ firstTypeCount + "\n" + secondTypeCount + "\n" +thirdTypeCount + "\n" + fourthTypeCount + "\n" + checkCount+ "\n" +creditCardCount+ "\n" + "~";
            cOut =  " " + "\n"+ " " + "\n" +  " " + "\n" +  " " + "\n" +  " "+ "\n" + " " + "\n"+ " " + "\n" +  " ";
            cTotal = "\n" + Util.makePrice(cashAmount) +"\n" + Util.makePrice(zReport.getFirstTypeAmount()) + "\n" + Util.makePrice(zReport.getSecondTypeAmount()) + "\n" + Util.makePrice(zReport.getThirdTypeAmount()) + "\n" + Util.makePrice(zReport.getFourthTypeAmount())
                    + "\n" + Util.makePrice(zReport.getCheckTotal()) + "\n" + Util.makePrice(zReport.getCreditTotal()) + "\n" + Util.makePrice(zReport.getTotalAmount());
        }else {
            names = "\u200F"+context.getString(R.string._cash)+"\n"+"\u200F"+currencyTypesList.get(0).getType()+ "\n" +"\u200F"+ context.getString(R.string.checks)+ "\n" +"\u200F"+ context.getString(R.string.credit_card)+"\n" + "\u200F"+context.getString(R.string.total_with_a_report_amount);
            cIn =cashCount+"\n"+ firstTypeCount + "\n" + checkCount+"\n" + creditCardCount + "\n" + "~";
            cOut =  "\n"+  " "+ " " + "\n"+  " " + "\n" + " " + "\n" +  " ";
            cTotal = "\n" + Util.makePrice(cashAmount)+"\n" + Util.makePrice(zReport.getFirstTypeAmount())+ "\n" + Util.makePrice(zReport.getCheckTotal()) + "\n" + Util.makePrice(zReport.getCreditTotal()) + "\n" + Util.makePrice(zReport.getTotalAmount());
        }
        StaticLayout cSlNames = new StaticLayout(names, orderTP,
                (int) (PAGE_WIDTH * 0.30), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout cSlIn = new StaticLayout(cIn, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout cSlOut = new StaticLayout(cOut, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout cSlTotal = new StaticLayout(cTotal, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

        // invoiceD.setTextAlign(Paint.Align.CENTER);
        // Create bitmap and canvas to draw to
        //RGB_565
        List<StaticLayout>staticLayoutList=new ArrayList<>();
        if(checkList.size()>0){
            for(int i=0;i<checkList.size();i++){
                List<Check>list = checkList.get(i);

                for (int f =0;f<list.size();f++){
                    StaticLayout currentCheck =new StaticLayout("\u200F"+"    "+list.get(f).getCheckNum() +  "\t\t\t\t\t\t\t\t\t\t" + DateConverter.toDate(new Date(list.get(f).getCreatedAt().getTime()))+  "\t\t\t\t\t\t" + list.get(f).getAmount() , invoiceD,
                            PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
                    staticLayoutList.add(currentCheck);
                    layOutHight+=currentCheck.getHeight();

                }
            }
        }
        int Page_Height = 0;
        //  if (SETTINGS.enableCurrencies)
        Page_Height = sHead.getHeight() + sInvoiceHead.getHeight() + sInvoiceD.getHeight() + slNames.getHeight() + sNewLine.getHeight() + cSlNames.getHeight() + sNewLine.getHeight() + scInvoiceD.getHeight()+ sNewLine.getHeight()+checkHead.getHeight()+ sNewLine.getHeight()+layOutHight  + scInvoiceD.getHeight()+ sNewLine.getHeight()+opiningReport.getHeight()+ sNewLine.getHeight()+opiningReportAmountLayout.getHeight()+ sNewLine.getHeight()+opiningReportStyleLayoutCount.getHeight()+ sNewLine.getHeight()+layOutCurrencyHight+ sNewLine.getHeight()+posSales.getHeight();

        //   Page_Height = sHead.getHeight() + sInvoiceHead.getHeight() + sInvoiceD.getHeight() + slNames.getHeight() + sNewLine.getHeight();
        Bitmap b = Bitmap.createBitmap(PAGE_WIDTH, Page_Height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);


        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        sHead.draw(c);
        c.translate(0, sHead.getHeight());
        sInvoiceHead.draw(c);
        c.translate(0, sInvoiceHead.getHeight());
        sInvoiceD.draw(c);


        c.translate(0, sInvoiceD.getHeight());
        slTotal.draw(c);
        c.translate(slTotal.getWidth(), 0);
        slOut.draw(c);
        c.translate(slOut.getWidth(), 0);
        slIn.draw(c);
        c.translate(slIn.getWidth(), 0);
        slNames.draw(c);
        c.translate(-(int) (PAGE_WIDTH * 0.60), slNames.getHeight());
        sNewLine.draw(c);

        //   if (SETTINGS.enableCurrencies) {
        c.translate(0, sNewLine.getHeight());
        cSlTotal.draw(c);
        scInvoiceD.draw(c);

        c.translate(cSlTotal.getWidth(), 0);
        c.translate(0, scInvoiceD.getHeight());

        cSlOut.draw(c);
        c.translate(cSlOut.getWidth(), 0);
        cSlIn.draw(c);
        c.translate(cSlIn.getWidth(), 0);
        cSlNames.draw(c);

        c.translate(-(int) (PAGE_WIDTH * 0.60), cSlNames.getHeight());
        sNewLine.draw(c);
        //   }
        if(checkList.size()>0){
            c.translate(0, sNewLine.getHeight());
            ///check Region

            checkHead.draw(c);
            c.translate(0, checkHead.getHeight());
            for(int i=0;i<staticLayoutList.size();i++){
             StaticLayout s= staticLayoutList.get(i);
                    s.draw(c);
                    c.translate(0, s.getHeight());
            }

            sNewLine.draw(c);
        }
        sNewLine.draw(c);

        opiningReport.draw(c);
        c.translate(0, opiningReport.getHeight());
        opiningReportAmountLayout.draw(c);
        c.translate(0, opiningReportAmountLayout.getHeight());
        opiningReportStyleLayoutCount.draw(c);
        c.translate(0, opiningReportStyleLayoutCount.getHeight());
        if(SETTINGS.enableCurrencies){
           for(int i=0;i<currencyDetailsLayOut.size();i++){
               currencyDetailsLayOut.get(i).draw(c);
               c.translate(0, currencyDetailsLayOut.get(i).getHeight());
           }
            sNewLine.draw(c);
        }
        sNewLine.draw(c);
        posSales.draw(c);
        c.translate(0, posSales.getHeight());
        c.restore();
        return b;
    }

    public static void getCountForZReport(Context context, ZReport z) {
        List<CurrencyType> currencyTypesList = null;
        List<Currency> currencyList=new ArrayList<>();
        CurrencyTypeDBAdapter currencyTypeDBAdapter = new CurrencyTypeDBAdapter(context);
        currencyTypeDBAdapter.open();
        currencyTypesList = currencyTypeDBAdapter.getAllCurrencyType();
        Log.d("currencyTypesListooo",currencyTypesList.toString());

        for (int i=0;i<currencyTypesList.size();i++){
            CurrencyDBAdapter currencyDBAdapter =new CurrencyDBAdapter(context);
            currencyDBAdapter.open();
            currencyList.add(currencyDBAdapter.getCurrencyByCode(currencyTypesList.get(i).getType()));
            currencyDBAdapter.close();
        }
        Log.d("currencyKdd",currencyList.toString());
        Log.d("hyyg",currencyList.get(0).getId()+"");

        currencyTypeDBAdapter.close();

        aReportAmount=0;
        opiningReportList=new ArrayList<>();
         aReportDetailsForFirstCurrency=0;
         aReportDetailsForSecondCurrency=0;
         aReportDetailsForThirdCurrency=0;
         aReportDetailsForForthCurrency=0;

        OpiningReportDBAdapter opiningReportDBAdapter = new OpiningReportDBAdapter(context);
        opiningReportDBAdapter.open();
        opiningReportList = opiningReportDBAdapter.getListByLastZReport(z.getzReportId()-1);
        for (int i=0;i<opiningReportList.size();i++){
            aReportAmount+=opiningReportList.get(i).getAmount();
        }
        if (SETTINGS.enableCurrencies) {
            OpiningReportDetailsDBAdapter aReportDetailsDBAdapter=new OpiningReportDetailsDBAdapter(context);
            aReportDetailsDBAdapter.open();
            for (int a=0 ;a<opiningReportList.size();a++) {
                //aReportAmount+=opiningReportList.get(a).getAmount();
                OpiningReport opiningReport = opiningReportList.get(a);
                aReportDetailsForFirstCurrency+= aReportDetailsDBAdapter.getLastRow((int) currencyList.get(0).getId(), opiningReport.getOpiningReportId());
                aReportDetailsForSecondCurrency+= aReportDetailsDBAdapter.getLastRow((int) currencyList.get(1).getId(), opiningReport.getOpiningReportId());
                aReportDetailsForThirdCurrency+= aReportDetailsDBAdapter.getLastRow((int) currencyList.get(2).getId(), opiningReport.getOpiningReportId());
                aReportDetailsForForthCurrency+= aReportDetailsDBAdapter.getLastRow((int) currencyList.get(3).getId(), opiningReport.getOpiningReportId());
            }

        }
        else {
            OpiningReportDetailsDBAdapter aReportDetailsDBAdapter=new OpiningReportDetailsDBAdapter(context);
            aReportDetailsDBAdapter.open();
            for (int a=0 ;a<opiningReportList.size();a++) {
                OpiningReport opiningReport = opiningReportList.get(a);
                aReportDetailsForFirstCurrency += aReportDetailsDBAdapter.getLastRow((int) currencyList.get(0).getId(), opiningReport.getOpiningReportId());
            }
        }
        checkList=new ArrayList<>();
        cashAmount=0;
        invoiceReceiptCount=0 ;invoiceCount=0; CreditInvoiceCount=0 ; firstTypeCount=0 ;secondTypeCount=0 ;thirdTypeCount=0;
        fourthTypeCount=0 ;checkCount=0 ; creditCardCount=0 ;receiptInvoiceAmountCheck=0 ; cashCount=0;receiptInvoiceAmount=0;
        OrderDBAdapter orderDb = new OrderDBAdapter(context);
        orderDb.open();
        ZReportDBAdapter zReportDBAdapter = new ZReportDBAdapter(context);
        zReportDBAdapter.open();
        invoiceReceiptCount = orderDb.getBetween(z.getStartOrderId(),z.getEndOrderId()).size();
        if(zReportDBAdapter.getProfilesCount()==0){
            PosInvoiceDBAdapter posInvoiceDBAdapter =new PosInvoiceDBAdapter(context);
            posInvoiceDBAdapter.open();
            List<PosInvoice>posInvoiceList = posInvoiceDBAdapter.getPosInvoiceList(-1, InvoiceStatus.UNPAID.getValue());
                invoiceCount+=posInvoiceList.size();

            List<PosInvoice>posCreditInvoiceList = posInvoiceDBAdapter.getPosInvoiceListByType(-1, DocumentType.CREDIT_INVOICE.getValue(),CONSTANT.CASH);
                CreditInvoiceCount+=posCreditInvoiceList.size();

            List<PosInvoice>posReceiptList = posInvoiceDBAdapter.getPosInvoiceListByType(-1, DocumentType.RECEIPT.getValue(),CONSTANT.CHECKS);
                receiptInvoiceAmountCheck+=posReceiptList.size();

        }else {
            ZReport zReport1=null;
            try {
                zReport1 = zReportDBAdapter.getLastRow();
            } catch (Exception e) {
                e.printStackTrace();
            }
            PosInvoiceDBAdapter posInvoiceDBAdapter =new PosInvoiceDBAdapter(context);
            posInvoiceDBAdapter.open();
            List<PosInvoice>posInvoiceList = posInvoiceDBAdapter.getPosInvoiceList(zReport1.getzReportId(), InvoiceStatus.UNPAID.getValue());
                invoiceCount+=posInvoiceList.size();

            List<PosInvoice>posCreditInvoiceList = posInvoiceDBAdapter.getPosInvoiceListByType(zReport1.getzReportId(), DocumentType.CREDIT_INVOICE.getValue(),CONSTANT.CASH);
                CreditInvoiceCount+=posCreditInvoiceList.size();

            List<PosInvoice>posReceiptListCheck = posInvoiceDBAdapter.getPosInvoiceListByType(zReport1.getzReportId(), DocumentType.RECEIPT.getValue(),CONSTANT.CHECKS);
                receiptInvoiceAmountCheck+=posReceiptListCheck.size();
        }
        List<Long>orderIds= new ArrayList<>();
        CashPaymentDBAdapter cashPaymentDBAdapter = new CashPaymentDBAdapter(context);
        cashPaymentDBAdapter.open();
        ChecksDBAdapter checksDBAdapter =new ChecksDBAdapter(context);
        checksDBAdapter.open();
        CreditCardPaymentDBAdapter creditCardPaymentDBAdapter =new CreditCardPaymentDBAdapter(context);
        creditCardPaymentDBAdapter.open();
        List<Payment> payments = paymentList(orderDb.getBetween(z.getStartOrderId(),z.getEndOrderId()),context);
        for (Payment p : payments) {
            long orderId = p.getOrderId();
            List<CashPayment>cashPaymentList=cashPaymentDBAdapter.getPaymentBySaleID(orderId);
            for(int i=0;i<cashPaymentList.size();i++){
                cashCount+=1;
                cashAmount+=p.getAmount();
            }
            List<Check>checkList=checksDBAdapter.getPaymentBySaleID(orderId);
            for(int i=0;i<checkList.size();i++){
                checkCount+=1;
            }
            List<CreditCardPayment>creditCardPayments=creditCardPaymentDBAdapter.getPaymentByOrderID(orderId);
            for(int i=0;i<creditCardPayments.size();i++){
                creditCardCount+=1;            }
            /*int i = 0;
            switch (p.getPaymentWay()) {

                case CONSTANT.CASH:
                  cashCount+=1;
                    cashAmount+=p.getAmount();
                    break;
                case CONSTANT.CREDIT_CARD:
                 creditCardCount+=1;
                    break;
                case CONSTANT.CHECKS:
                  checkCount+=1;
                    orderIds.add(p.getOrderId());
                    break;
            }*/
        }
        if(orderIds.size()>0){
            for (int id = 0;id<orderIds.size();id++){
                ChecksDBAdapter checkDb= new ChecksDBAdapter(context);
                checkDb.open();
                List<Check> c = checkDb.getPaymentBySaleID(orderIds.get(id));
                checkList.add(c);
            }
        }
        //with Currency
        if (SETTINGS.enableCurrencies) {
            List<CurrencyType> currencyTypesList1 = null;
            CurrencyTypeDBAdapter currencyTypeDBAdapter1 = new CurrencyTypeDBAdapter(context);
            currencyTypeDBAdapter1.open();
            currencyTypesList1 = currencyTypeDBAdapter1.getAllCurrencyType();
            currencyTypeDBAdapter1.close();
            List<CurrencyOperation>currencyOperationList=currencyOperationPaymentList(orderDb.getBetween(z.getStartOrderId(),z.getEndOrderId()),context);
            for (CurrencyOperation cp : currencyOperationList) {
              /*  switch (cp.getCurrencyType()) {
                    case "ILS":
                        ShekelCount+=1;
                        break;
                    case "USD":
                        UsdCount+=1;
                        break;
                    case "EUR":
                        EurCount+=1;

                        break;
                    case "GBP":
                        GbpCount+=1;
                        break;
                }*/


                if (cp.getCurrencyType().equals(currencyTypesList1.get(0).getType())){

                    firstTypeCount+=1;
                }
                else   if (cp.getCurrencyType().equals(currencyTypesList1.get(1).getType())){
                    secondTypeCount+=1;
                }
                else   if (cp.getCurrencyType().equals(currencyTypesList1.get(2).getType())){
                    thirdTypeCount+=1;
                }
                else if (cp.getCurrencyType().equals(currencyTypesList1.get(3).getType())){
                    fourthTypeCount+=1;
                }
            }

        }
        if(zReportDBAdapter.getProfilesCount()==0){
            PosInvoiceDBAdapter posInvoiceDBAdapter =new PosInvoiceDBAdapter(context);
            posInvoiceDBAdapter.open();
            List<PosInvoice>posReceiptList = posInvoiceDBAdapter.getPosInvoiceListByType(-1, DocumentType.RECEIPT.getValue(),CONSTANT.CASH);
            receiptInvoiceAmount+=posReceiptList.size();

        }else {
            ZReport zReport1=null;
            try {
                zReport1 = zReportDBAdapter.getLastRow();
            } catch (Exception e) {
                e.printStackTrace();
            }
            PosInvoiceDBAdapter posInvoiceDBAdapter =new PosInvoiceDBAdapter(context);
            posInvoiceDBAdapter.open();
            List<PosInvoice>posReceiptList = posInvoiceDBAdapter.getPosInvoiceListByType(zReport1.getzReportId(), DocumentType.RECEIPT.getValue(),CONSTANT.CASH);
            receiptInvoiceAmount+=posReceiptList.size();

        }
        firstTypeCount+=receiptInvoiceAmount;
        checkCount+=receiptInvoiceAmountCheck;

    }

    public static Bitmap xPrint(Context context, XReport xReport , boolean isCopy) {
        List<CurrencyType> currencyTypesList = null;
        CurrencyTypeDBAdapter currencyTypeDBAdapter = new CurrencyTypeDBAdapter(context);
        currencyTypeDBAdapter.open();
        currencyTypesList = currencyTypeDBAdapter.getAllCurrencyType();
        currencyTypeDBAdapter.close();
        XReportDBAdapter xReportDBAdapter =new XReportDBAdapter(context);
        xReportDBAdapter.open();
        EmployeeDBAdapter employeeDBAdapter = new EmployeeDBAdapter(context);
        employeeDBAdapter.open();
        xReport.setUser( employeeDBAdapter.getEmployeeByID(xReport.getByUser()));
        //miriam_libre_bold.ttf
        //miriam_libre_regular.ttf
        //carmelitregular.ttf
        int PAGE_WIDTH = CONSTANT.PRINTER_PAGE_WIDTH;
        String status = context.getString(R.string.source_invoice);
        StaticLayout sHead;
        Typeface plain = Typeface.createFromAsset(context.getAssets(), "carmelitregular.ttf");
        Typeface normal = Typeface.create(plain, Typeface.NORMAL);
        Typeface bold = Typeface.create(plain, Typeface.BOLD);

        TextPaint head = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        head.setStyle(Paint.Style.FILL);
        head.setColor(Color.BLACK);
        head.setTypeface(normal);
        head.setTextSize(38);
        TextPaint posSaleStyle =new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        posSaleStyle.setStyle(Paint.Style.FILL);
        posSaleStyle.setColor(Color.BLACK);
        posSaleStyle.setTypeface(normal);
        posSaleStyle.setTextSize(28);
        posSaleStyle.setTextAlign(Paint.Align.LEFT);
        posSaleStyle.setLinearText(true);

        if (isCopy)
            status = context.getString(R.string.copy_invoice);
       if (SETTINGS.company.name().equals("BO_EXEMPT_DEALER")){
             sHead = new StaticLayout(context.getString(R.string.privet_company_status) + ":" + SETTINGS.companyID + "\n\r" + status + "\n\r" + DateConverter.dateToString(xReport.getCreatedAt().getTime()) + "\n\r" + "קןפאי : " + xReport.getUser().getFullName()+ status , head,
                    PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
        }
        else {
             sHead = new StaticLayout(context.getString(R.string.private_company) + ":" + SETTINGS.companyID + "\n\r" + status + "\n\r" + DateConverter.dateToString(xReport.getCreatedAt().getTime()) + "\n\r" + "קןפאי : " + xReport.getUser().getFullName()+ status , head,
                    PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
        }

    /**    StaticLayout posSales = new StaticLayout( "\n"  +context.getString(R.string.pos_sales)+" "+xReport.getTotalPosSales(), posSaleStyle,
                PAGE_WIDTH, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, true);**/


        TextPaint invoiceHead = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceHead.setStyle(Paint.Style.FILL);
        invoiceHead.setColor(Color.BLACK);
        invoiceHead.setTypeface(bold);
        invoiceHead.setTextSize(38);
        StaticLayout sInvoiceHead = new StaticLayout(context.getString(R.string.x) + " " + String.format("%06d", xReport.getxReportId()) + "\n\r---------------------------", invoiceHead,
                PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);


        TextPaint invoiceD = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceD.setStyle(Paint.Style.FILL);
        invoiceD.setColor(Color.BLACK);
        invoiceD.setTypeface(bold);
        invoiceD.setTextSize(28);
        invoiceD.setLinearText(true);

        //invoiceD.setTextAlign(Paint.Align.LEFT);

        TextPaint invoiceCD = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceCD.setStyle(Paint.Style.FILL);
        invoiceCD.setColor(Color.BLACK);
        invoiceCD.setTypeface(bold);
        invoiceCD.setTextSize(28);
        invoiceCD.setLinearText(true);

        TextPaint orderTP = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        orderTP.setStyle(Paint.Style.FILL);
        orderTP.setColor(Color.BLACK);
        orderTP.setTypeface(normal);
        orderTP.setTextSize(25);
        orderTP.setLinearText(true);

        StaticLayout sInvoiceD = new StaticLayout("\u200F"+context.getString(R.string.details) +  "\t\t\t\t\t\t\t\t\t"+context.getString(R.string.in_put)+ "\t\t"+ context.getString(R.string.out_put) + "\t\t\t" + context.getString(R.string.total) , invoiceD,
                PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

        StaticLayout scInvoiceD = new StaticLayout("\u200F"+context.getString(R.string.currency) +  "\t\t\t\t\t\t\t\t"+context.getString(R.string.in_put)+ "\t\t"+ context.getString(R.string.out_put) + "\t\t\t" + context.getString(R.string.total) , invoiceD,
                PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);


        String names = "", in = "", out = "", total = "";
        if (SETTINGS.company.name().equals("BO_EXEMPT_DEALER")){
            names +="\u200F"+ context.getString(R.string.invoice_recipte_company_status) +"\n" + "\u200F"+context.getString(R.string.invoice_company_status) + "\n" + "\u200F"+context.getString(R.string.credit_invoice_doc_company_status) + "\n"+"\u200F"+context.getString(R.string.total_sales);
        }
        else {
            names += "\u200F" + context.getString(R.string.invoice_receipt) + "\n" + "\u200F" + context.getString(R.string.invoice) + "\n" + "\u200F" + context.getString(R.string.credit_invoice_doc) + "\n" + "\u200F" + context.getString(R.string.total_sales);
        }
        in +=  "~" + "\n" + "~" + "\n" +"~" + "\n" +"~";
        out +=  "~" + "\n" + "~" + "\n" +"~" + "\n" +"~";
        total += dTS(xReport.getInvoiceReceiptAmount()) + "\n" + dTS(xReport.getInvoiceAmount()) + "\n" + dTS(xReport.getCreditInvoiceAmount()) + "\n" +dTS(xReport.getTotalSales());
        StaticLayout slNames = new StaticLayout(names, orderTP,
                (int) (PAGE_WIDTH * 0.30), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slIn = new StaticLayout(in, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slOut = new StaticLayout(out, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slTotal = new StaticLayout(total, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

        //orderTP.setTextSize(60);
        //orderTP.setTypeface(bold);


        StaticLayout sNewLine = new StaticLayout("-----------------------------", invoiceHead,
                PAGE_WIDTH, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 1.0f, true);



        String cIn = "", cOut = "", cTotal = "";
        if(SETTINGS.enableCurrencies) {
            names = "\u200F"+currencyTypesList.get(0).getType() + "\n"+"\u200F"+ currencyTypesList.get(1).getType()
                    + "\n" +"\u200F"+ currencyTypesList.get(2).getType() + "\n" +"\u200F"+
                    currencyTypesList.get(3).getType() +  "\n" +"\u200F"+ context.getString(R.string.checks)+ "\n" +"\u200F"+ context.getString(R.string.credit_card)+"\n" + "\u200F"+context.getString(R.string.total_with_a_report_amount);
            cIn = "~" + "\n" + "~" + "\n" + "~" + "\n" + "~" + "\n" + "~" + "\n" + "~"+ "\n" + "~";
            cOut = "~" + "\n" + "~" + "\n" + "~" + "\n" + "~" + "\n" + "~" + "\n"+ "~" + "\n" + "~";
            cTotal = "\n" + Util.makePrice(xReport.getFirstTypeAmount()) + "\n" + Util.makePrice(xReport.getSecondTypeAmount()) + "\n" + Util.makePrice(xReport.getThirdTypeAmount()) + "\n" + Util.makePrice(xReport.getFourthTypeAmount())
                    + "\n" + Util.makePrice(xReport.getCheckTotal()) + "\n" + Util.makePrice(xReport.getCreditTotal()) + "\n" + Util.makePrice(xReport.getTotalAmount());
        }else {
            names = "\u200F"+currencyTypesList.get(0).getType() + "\n" +"\u200F"+ context.getString(R.string.checks)+ "\n" +"\u200F"+ context.getString(R.string.credit_card)+"\n" + "\u200F"+context.getString(R.string.total_with_a_report_amount);
            cIn = "~" + "\n" + "~"+"\n" + "~" + "\n" + "~";
            cOut = "~" + "\n"+ "~" + "\n" +"~" + "\n" + "~";
            cTotal = "\n" + Util.makePrice(xReport.getFirstTypeAmount())+ "\n" + Util.makePrice(xReport.getCheckTotal()) + "\n" + Util.makePrice(xReport.getCreditTotal()) + "\n" + Util.makePrice(xReport.getTotalAmount());
        }
        StaticLayout cSlNames = new StaticLayout(names, orderTP,
                (int) (PAGE_WIDTH * 0.30), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout cSlIn = new StaticLayout(cIn, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout cSlOut = new StaticLayout(cOut, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout cSlTotal = new StaticLayout(cTotal, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);


        // invoiceD.setTextAlign(Paint.Align.CENTER);
        // Create bitmap and canvas to draw to
        //RGB_565
        int Page_Height = 0;
        //  if (SETTINGS.enableCurrencies)
        Page_Height = sHead.getHeight() + sInvoiceHead.getHeight() + sInvoiceD.getHeight() + slNames.getHeight() + sNewLine.getHeight() + cSlNames.getHeight() + sNewLine.getHeight() + scInvoiceD.getHeight()+ sNewLine.getHeight() ;

        //   Page_Height = sHead.getHeight() + sInvoiceHead.getHeight() + sInvoiceD.getHeight() + slNames.getHeight() + sNewLine.getHeight();
        Bitmap b = Bitmap.createBitmap(PAGE_WIDTH, Page_Height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);


        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        sHead.draw(c);
        c.translate(0, sHead.getHeight());
        sInvoiceHead.draw(c);
        c.translate(0, sInvoiceHead.getHeight());
        sInvoiceD.draw(c);


        c.translate(0, sInvoiceD.getHeight());
        slTotal.draw(c);
        c.translate(slTotal.getWidth(), 0);
        slOut.draw(c);
        c.translate(slOut.getWidth(), 0);
        slIn.draw(c);
        c.translate(slIn.getWidth(), 0);
        slNames.draw(c);
        c.translate(-(int) (PAGE_WIDTH * 0.60), slNames.getHeight());
        sNewLine.draw(c);

        //   if (SETTINGS.enableCurrencies) {
        c.translate(0, sNewLine.getHeight());
        cSlTotal.draw(c);
        scInvoiceD.draw(c);

        c.translate(cSlTotal.getWidth(), 0);
        c.translate(0, scInvoiceD.getHeight());

        cSlOut.draw(c);
        c.translate(cSlOut.getWidth(), 0);
        cSlIn.draw(c);
        c.translate(cSlIn.getWidth(), 0);
        cSlNames.draw(c);

        c.translate(-(int) (PAGE_WIDTH * 0.60), cSlNames.getHeight());
        sNewLine.draw(c);
        //   }
        /*posSales.draw(c);
        c.translate(0, posSales.getHeight());**/
        c.restore();
        return b;
    }

    private static String dTS(double d) {
        return String.format(new Locale("en"), "%.2f", d);
    }


    public static Bitmap testPrinter(Context context, int pagewidth) {
        int PAGE_WIDTH = pagewidth;

        Typeface plain = Typeface.createFromAsset(context.getAssets(), "carmelitregular.ttf");
        Typeface normal = Typeface.create(plain, Typeface.NORMAL);
        Typeface bold = Typeface.create(plain, Typeface.BOLD);

        TextPaint head = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        head.setStyle(Paint.Style.FILL);
        head.setColor(Color.BLACK);
        head.setTypeface(normal);
        head.setTextSize(38);
        StaticLayout sHead;
        if (SETTINGS.company.name().equals("BO_EXEMPT_DEALER")){
            sHead = new StaticLayout(context.getString(R.string.privet_company_status) + ":" + SETTINGS.companyID + "\n\r" + DateConverter.dateToString(new Date().getTime()) + "\n\r" + "קןפאי : " + "sadsd", head,
                    PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
        }
        else {
            sHead = new StaticLayout(context.getString(R.string.private_company) + ":" + SETTINGS.companyID + "\n\r" + DateConverter.dateToString(new Date().getTime()) + "\n\r" + "קןפאי : " + "sadsd", head,
                    PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
        }



        TextPaint invoiceHead = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceHead.setStyle(Paint.Style.FILL);
        invoiceHead.setColor(Color.BLACK);
        invoiceHead.setTypeface(bold);
        invoiceHead.setTextSize(38);
        StaticLayout sInvoiceHead = new StaticLayout("---------------------------", invoiceHead,
                PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);


        TextPaint invoiceD = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceD.setStyle(Paint.Style.FILL);
        invoiceD.setColor(Color.BLACK);
        invoiceD.setTypeface(bold);

        invoiceD.setTextSize(30);


        StaticLayout sInvoiceD = new StaticLayout("פעולה        \t\t\t\t\t\t\t\t\t נכנס  \t\t\t\t\t זיכוי \t\t\t סה''כ", invoiceD,
                PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);

        TextPaint orderTP = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        orderTP.setStyle(Paint.Style.FILL);
        orderTP.setColor(Color.BLACK);
        orderTP.setTypeface(normal);
        orderTP.setTextSize(30);
        orderTP.setTextAlign(Paint.Align.LEFT);
        orderTP.setLinearText(true);


        // invoiceD.setTextAlign(Paint.Align.CENTER);
        // Create bitmap and canvas to draw to
        //RGB_565
        int Page_Height = sHead.getHeight() + sInvoiceHead.getHeight() + sHead.getHeight();
        Bitmap b = Bitmap.createBitmap(PAGE_WIDTH, Page_Height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);


        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        sHead.draw(c);
        c.translate(0, sHead.getHeight());
        sInvoiceHead.draw(c);
        c.translate(0, sInvoiceHead.getHeight());
        sInvoiceD.draw(c);

        c.restore();
        return b;
    }

    public Bitmap returnInvoice() {
        List<Block> blocks = new ArrayList<>();

        Block b = new Block("Hello", 40f, Color.BLACK);
        //b = b.Center();
        blocks.add(b);

        Block line = new Block("-------------------------", 30f, Color.BLACK);
        // line.Center();
        blocks.add(line);


        // InvoiceImg invoiceImg = new InvoiceImg(blocks);

        return null;//invoiceImg.make();
    }
    public static Bitmap monthZPrint(Context context, ZReport zReport ,  boolean isCopy ,Date from , Date to) {
        List<CurrencyType> currencyTypesList = null;
        CurrencyTypeDBAdapter currencyTypeDBAdapter = new CurrencyTypeDBAdapter(context);
        currencyTypeDBAdapter.open();
        currencyTypesList = currencyTypeDBAdapter.getAllCurrencyType();
        currencyTypeDBAdapter.close();
        ZReportDBAdapter zReportDBAdapter =new ZReportDBAdapter(context);
        zReportDBAdapter.open();
        EmployeeDBAdapter employeeDBAdapter = new EmployeeDBAdapter(context);
        employeeDBAdapter.open();
        zReport.setUser( employeeDBAdapter.getEmployeeByID(zReport.getByUser()));
        //miriam_libre_bold.ttf
        //miriam_libre_regular.ttf
        //carmelitregular.ttf
        int PAGE_WIDTH = CONSTANT.PRINTER_PAGE_WIDTH;
        String status = context.getString(R.string.source_invoice);

        Typeface plain = Typeface.createFromAsset(context.getAssets(), "carmelitregular.ttf");
        Typeface normal = Typeface.create(plain, Typeface.NORMAL);
        Typeface bold = Typeface.create(plain, Typeface.BOLD);

        TextPaint head = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        head.setStyle(Paint.Style.FILL);
        head.setColor(Color.BLACK);
        head.setTypeface(normal);
        head.setTextSize(38);
        StaticLayout sHead;
        if (isCopy)
            status = context.getString(R.string.copy_invoice);
        if (SETTINGS.company.name().equals("BO_EXEMPT_DEALER")){
             sHead = new StaticLayout(context.getString(R.string.privet_company_status) + ":" + SETTINGS.companyID + "\n\r" + status + "\n\r" +context.getString(R.string.from)+"  "+DateConverter.geDate(from) +"\n\r" +context.getString(R.string.to)+"  "+DateConverter.geDate(to) + "\n\r" + "קןפאי : " + zReport.getUser().getFullName()+ status , head,
                    PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
        }
        else {
            sHead = new StaticLayout(context.getString(R.string.private_company) + ":" + SETTINGS.companyID + "\n\r" + status + "\n\r" +context.getString(R.string.from)+"  "+DateConverter.geDate(from) +"\n\r" +context.getString(R.string.to)+"  "+DateConverter.geDate(to) + "\n\r" + "קןפאי : " + zReport.getUser().getFullName()+ status , head,
                    PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);
        }


        TextPaint invoiceHead = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceHead.setStyle(Paint.Style.FILL);
        invoiceHead.setColor(Color.BLACK);
        invoiceHead.setTypeface(bold);
        invoiceHead.setTextSize(38);
        StaticLayout sInvoiceHead = new StaticLayout("\n\r---------------------------", invoiceHead,
                PAGE_WIDTH, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, true);


        TextPaint invoiceD = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceD.setStyle(Paint.Style.FILL);
        invoiceD.setColor(Color.BLACK);
        invoiceD.setTypeface(bold);
        invoiceD.setTextSize(28);
        invoiceD.setLinearText(true);

        //invoiceD.setTextAlign(Paint.Align.LEFT);

        TextPaint invoiceCD = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        invoiceCD.setStyle(Paint.Style.FILL);
        invoiceCD.setColor(Color.BLACK);
        invoiceCD.setTypeface(bold);
        invoiceCD.setTextSize(28);
        invoiceCD.setLinearText(true);

        TextPaint orderTP = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        orderTP.setStyle(Paint.Style.FILL);
        orderTP.setColor(Color.BLACK);
        orderTP.setTypeface(normal);
        orderTP.setTextSize(25);
        orderTP.setLinearText(true);

        StaticLayout sInvoiceD = new StaticLayout("\u200F"+context.getString(R.string.details) +  "\t\t\t\t\t\t\t\t\t"+context.getString(R.string.in_put)+ "\t\t"+ context.getString(R.string.out_put) + "\t\t\t" + context.getString(R.string.total) , invoiceD,
                PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

        StaticLayout scInvoiceD = new StaticLayout("\u200F"+context.getString(R.string.currency) +  "\t\t\t\t\t\t\t\t"+context.getString(R.string.in_put)+ "\t\t"+ context.getString(R.string.out_put) + "\t\t\t" + context.getString(R.string.total) , invoiceD,
                PAGE_WIDTH , Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);


        String names = "", in = "", out = "", total = "";
        if (SETTINGS.company.name().equals("BO_EXEMPT_DEALER")){
            names +="\u200F"+ context.getString(R.string.invoice_recipte_company_status) +"\n" + "\u200F"+context.getString(R.string.invoice_company_status) + "\n" + "\u200F"+context.getString(R.string.credit_invoice_doc_company_status) + "\n"+"\u200F"+context.getString(R.string.total_sales);
        }
        else {
            names += "\u200F" + context.getString(R.string.invoice_receipt) + "\n" + "\u200F" + context.getString(R.string.invoice) + "\n" + "\u200F" + context.getString(R.string.credit_invoice_doc) + "\n" + "\u200F" + context.getString(R.string.total_sales);
        }
        in +=  "~" + "\n" + "~" + "\n" +"~" + "\n" +"~";
        out +=  "~" + "\n" + "~" + "\n" +"~" + "\n" +"~";
        total += dTS(zReport.getInvoiceReceiptAmount()) + "\n" + dTS(zReport.getInvoiceAmount()) + "\n" + dTS(zReport.getCreditInvoiceAmount()) + "\n" +dTS(zReport.getTotalSales());
        StaticLayout slNames = new StaticLayout(names, orderTP,
                (int) (PAGE_WIDTH * 0.30), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slIn = new StaticLayout(in, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slOut = new StaticLayout(out, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout slTotal = new StaticLayout(total, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

        //orderTP.setTextSize(60);
        //orderTP.setTypeface(bold);


        StaticLayout sNewLine = new StaticLayout("-----------------------------", invoiceHead,
                PAGE_WIDTH, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 1.0f, true);



        String cIn = "", cOut = "", cTotal = "";
            if(SETTINGS.enableCurrencies) {
                names = "\u200F"+currencyTypesList.get(0).getType() + "\n"+"\u200F"+ currencyTypesList.get(1).getType()+ "\n" +"\u200F"+
                        currencyTypesList.get(2).getType() + "\n" +"\u200F"+ currencyTypesList.get(3).getType()+  "\n" +"\u200F"+ context.getString(R.string.checks)+ "\n" +"\u200F"+ context.getString(R.string.credit_card)+"\n" + "\u200F"+context.getString(R.string.total_with_a_report_amount);
                cIn = "~" + "\n" + "~" + "\n" + "~" + "\n" + "~" + "\n" + "~" + "\n" + "~"+ "\n" + "~";
                cOut = "~" + "\n" + "~" + "\n" + "~" + "\n" + "~" + "\n" + "~" + "\n"+ "~" + "\n" + "~";
                cTotal = "\n" + Util.makePrice(zReport.getFirstTypeAmount()) + "\n" + Util.makePrice(zReport.getSecondTypeAmount()) + "\n" + Util.makePrice(zReport.getThirdTypeAmount()) + "\n" + Util.makePrice(zReport.getFourthTypeAmount())
                        + "\n" + Util.makePrice(zReport.getCheckTotal()) + "\n" + Util.makePrice(zReport.getCreditTotal()) + "\n" + Util.makePrice(zReport.getTotalAmount());
            }else {
                names = "\u200F"+currencyTypesList.get(0).getType()+ "\n" +"\u200F"+ context.getString(R.string.checks)+ "\n" +"\u200F"+ context.getString(R.string.credit_card)+"\n" + "\u200F"+context.getString(R.string.total_with_a_report_amount);
                cIn = "~" + "\n" + "~"+"\n" + "~" + "\n" + "~";
                cOut = "~" + "\n"+ "~" + "\n" +"~" + "\n" + "~";
                cTotal = "\n" + Util.makePrice(zReport.getFirstTypeAmount())+ "\n" + Util.makePrice(zReport.getCheckTotal()) + "\n" + Util.makePrice(zReport.getCreditTotal()) + "\n" + Util.makePrice(zReport.getTotalAmount());
            }
        StaticLayout cSlNames = new StaticLayout(names, orderTP,
                (int) (PAGE_WIDTH * 0.30), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout cSlIn = new StaticLayout(cIn, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout cSlOut = new StaticLayout(cOut, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        StaticLayout cSlTotal = new StaticLayout(cTotal, orderTP,
                (int) (PAGE_WIDTH * 0.2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);


        // invoiceD.setTextAlign(Paint.Align.CENTER);
        // Create bitmap and canvas to draw to
        //RGB_565
        int Page_Height = 0;
            Page_Height = sHead.getHeight() + sInvoiceHead.getHeight() + sInvoiceD.getHeight() + slNames.getHeight() + sNewLine.getHeight() + cSlNames.getHeight() + sNewLine.getHeight() + scInvoiceD.getHeight()+ sNewLine.getHeight();
     /*   else
            Page_Height = sHead.getHeight() + sInvoiceHead.getHeight() + sInvoiceD.getHeight() + slNames.getHeight() + sNewLine.getHeight()+posSales.getHeight();**/
        Bitmap b = Bitmap.createBitmap(PAGE_WIDTH, Page_Height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);


        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        sHead.draw(c);
        c.translate(0, sHead.getHeight());
        sInvoiceHead.draw(c);
        c.translate(0, sInvoiceHead.getHeight());
        sInvoiceD.draw(c);


        c.translate(0, sInvoiceD.getHeight());
        slTotal.draw(c);
        c.translate(slTotal.getWidth(), 0);
        slOut.draw(c);
        c.translate(slOut.getWidth(), 0);
        slIn.draw(c);
        c.translate(slIn.getWidth(), 0);
        slNames.draw(c);
        c.translate(-(int) (PAGE_WIDTH * 0.60), slNames.getHeight());
        sNewLine.draw(c);

        //   if (SETTINGS.enableCurrencies) {
        c.translate(0, sNewLine.getHeight());
        cSlTotal.draw(c);
        scInvoiceD.draw(c);

        c.translate(cSlTotal.getWidth(), 0);
        c.translate(0, scInvoiceD.getHeight());

        cSlOut.draw(c);
        c.translate(cSlOut.getWidth(), 0);
        cSlIn.draw(c);
        c.translate(cSlIn.getWidth(), 0);
        cSlNames.draw(c);

        c.translate(-(int) (PAGE_WIDTH * 0.60), cSlNames.getHeight());
        sNewLine.draw(c);
        //   }

        c.restore();
        return b;
    }

}


