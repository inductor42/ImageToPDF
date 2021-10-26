package com.image2pdf.proeditor.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import com.image2pdf.proeditor.MainActivity;


import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PDFOCRAsync extends AsyncTask<Void, Integer, Boolean> {

    private int pageIndex;
    private PdfRenderer mPdfRenderer;
    private PdfRenderer.Page mCurrentPage;
    private ParcelFileDescriptor mFileDescriptor;
    private MainActivity activity;
    private File pdfFile;
    private int mPageIndex;
    PdfReader pdfReader;
    BaseFont base_font;
    PdfStamper stamper;
    File tempfile;
    boolean isOCRCompleted;

    public PDFOCRAsync(File file, MainActivity activity) {
        this.pdfFile = file;
        this.activity = activity;
        mPageIndex = 0;

    }

    public void openRenderer() throws IOException {
        Context context = activity.getApplicationContext();
        // In this sample, we read a PDF from the assets directory.
        try {
            if (pdfFile.exists()) {
                mFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
                // This is the PdfRenderer we use to render the PDF.
                if (mFileDescriptor != null) {
                    mPdfRenderer = new PdfRenderer(mFileDescriptor);
                }
            }
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showBottomSheet(getPageCount());
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        int pageCount = getPageCount();
        if (pageCount > 0) {
            try {
                base_font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false);
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/PDFOCR");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                tempfile = new File(myDir.getAbsolutePath(), pdfFile.getName());
                copyFileUsingStream(pdfFile, tempfile);
                pdfReader = new PdfReader(new FileInputStream(tempfile));
                stamper = new PdfStamper(pdfReader, new FileOutputStream(tempfile));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    protected void onProgressUpdate(Integer... values) {
        activity.setProgress(values[0], getPageCount());
    }

    public void onPostExecute(Boolean bool) {
        Dispose();
        activity.runPostExecution(tempfile);
    }

    public int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    void Dispose() {
        if (stamper != null) {
            try {
                stamper.close();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (pdfReader != null) {
            pdfReader.close();
        }
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeRenderer() throws IOException {
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        mPdfRenderer.close();
        mFileDescriptor.close();
    }

    private Bitmap showPage(int index) {
        if (mPdfRenderer.getPageCount() <= index) {
            return null;
        }
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        return bitmap;
    }




    private void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }


}
