package com.imeshperera.ecomapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.imeshperera.ecomapp.models.OrderModel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class InvoiceGenerator {

    public static void generateAndShare(Activity activity, OrderModel order) {
        if (order == null) return;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Header
        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        paint.setFakeBoldText(true);
        canvas.drawText("Invoice", 250, 50, paint);
        
        paint.setTextSize(16);
        canvas.drawText("EComShop", 50, 90, paint);

        // Order Info
        paint.setFakeBoldText(false);
        paint.setTextSize(12);
        canvas.drawText("Order ID: " + order.getOrderId(), 50, 130, paint);
        canvas.drawText("Date: " + order.getOrderDate() + " " + order.getOrderTime(), 50, 150, paint);
        canvas.drawText("Status: " + order.getStatus(), 50, 170, paint);

        // Customer Info
        canvas.drawText("Customer Name: " + order.getCustomerName(), 350, 130, paint);
        canvas.drawText("Phone: " + order.getCustomerPhone(), 350, 150, paint);
        canvas.drawText("Address: " + order.getShippingAddress(), 350, 170, paint);

        // Table Header
        paint.setFakeBoldText(true);
        canvas.drawText("Product", 50, 220, paint);
        canvas.drawText("Qty", 350, 220, paint);
        canvas.drawText("Price", 420, 220, paint);
        canvas.drawText("Total", 500, 220, paint);
        canvas.drawLine(50, 230, 550, 230, paint);

        // Items
        paint.setFakeBoldText(false);
        int y = 250;
        if (order.getItems() != null) {
            for (Map<String, Object> item : order.getItems()) {
                String name = String.valueOf(item.get("productName"));
                String qty = String.valueOf(item.get("quantity"));
                
                double price = 0.0;
                try {
                    price = Double.parseDouble(String.valueOf(item.get("productPrice")).replaceAll("[^\\d.]", ""));
                } catch (Exception e) {}
                
                double total = 0.0;
                try {
                    total = Double.parseDouble(String.valueOf(item.get("totalPrice")));
                } catch (Exception e) {}

                if (name.length() > 30) name = name.substring(0, 27) + "...";

                canvas.drawText(name, 50, y, paint);
                canvas.drawText(qty, 350, y, paint);
                canvas.drawText("Rs." + String.format("%.2f", price), 420, y, paint);
                canvas.drawText("Rs." + String.format("%.2f", total), 500, y, paint);
                y += 20;
            }
        }

        // Summary
        canvas.drawLine(50, y + 10, 550, y + 10, paint);
        y += 30;
        paint.setFakeBoldText(true);
        
        double shipping = order.getShippingFee();
        double grandTotal = order.getTotalAmount();
        double subtotal = grandTotal - shipping;

        canvas.drawText("Subtotal:", 400, y, paint);
        canvas.drawText("Rs." + String.format("%.2f", subtotal), 500, y, paint);
        y += 20;
        
        canvas.drawText("Shipping:", 400, y, paint);
        canvas.drawText("Rs." + String.format("%.2f", shipping), 500, y, paint);
        y += 20;
        
        canvas.drawText("Grand Total:", 400, y, paint);
        canvas.drawText("Rs." + String.format("%.2f", grandTotal), 500, y, paint);

        // Footer
        y += 50;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        paint.setTextSize(16);
        canvas.drawText("Thank you for shopping with us!", 297, y, paint);

        document.finishPage(page);

        try {
            File dir = new File(activity.getExternalFilesDir(null), "invoices");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, "Invoice_" + order.getOrderId() + ".pdf");
            document.writeTo(new FileOutputStream(file));
            document.close();
            sharePdf(activity, file);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error generating PDF: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            try {
                document.close();
            } catch (Exception ignored) {}
        }
    }

    private static void sharePdf(Activity activity, File file) {
        Uri uri = FileProvider.getUriForFile(activity, "com.imeshperera.ecomapp.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(Intent.createChooser(intent, "Share Invoice"));
    }
}
