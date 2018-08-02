package com.isansc.pdfviewerpoc;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.io.File;
import java.net.URI;

/**
 * Load Pdf file using https://github.com/barteksc/AndroidPdfViewer library
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_CODE_WRITE_PERMISSION = 211;

    private String url;
    private boolean isSavedOnDevice;
    private PDFView pdfView;
    private ProgressBar progressBarModules;
    private ImageView imagePlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarModules = (ProgressBar) findViewById(R.id.progressBarModules);
        imagePlaceholder = (ImageView) findViewById(R.id.image_placeholder);
        pdfView = (PDFView) findViewById(R.id.pdf_view);

        this.url = "http://gahp.net/wp-content/uploads/2017/09/sample.pdf";
        this.isSavedOnDevice = false;

        loadModule(url, isSavedOnDevice);

    }



    /**
     * Receives a url to load the webview. If the boolean isSavedOnDevice
     * is true, the url is the path of the file on the device.
     *
     * @param url             a url to load in webview
     * @param isSavedOnDevice a boolean if the module is saved on device
     */
    public void loadModule(String url, boolean isSavedOnDevice) {
        this.url = url;
        this.isSavedOnDevice = isSavedOnDevice;
        if (verifyWritePermission(this, null)) {
            loadModule();
        }
    }
    /**
     * Receives a url to load the pdf file. If the boolean isSavedOnDevice
     * is true, the url is the path of the file on the device.
     */
    public void loadModule() {
        if(url != null){
            Log.d(TAG, "Loading PDF Module. url: " + url);
            Log.d(TAG, "Loading PDF Module. isSavedOnDevice: " + isSavedOnDevice);

            progressBarModules.setVisibility(View.VISIBLE);
            imagePlaceholder.setVisibility(View.GONE);
            pdfView.setVisibility(View.INVISIBLE);

            try {
                if (isSavedOnDevice) {
                    ;
                    setupAndLoadPdfView(pdfView.fromFile(new File(new URI("file://" + url))));
                    pdfView.setVisibility(View.VISIBLE);
//                    pdfView.fromUri(Uri.parse(Util.urlValidator(url)));
                    progressBarModules.setVisibility(View.GONE);

                } else {

                    Ion.with(this)
                            .load(url)
                            .write(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "reader_pdf"))
                            .withResponse() // response will encapsulate the result file
                            .setCallback(new FutureCallback<Response<File>>() {

                                @Override
                                public void onCompleted(Exception e, Response<File> response) {
                                    if (e == null && response.getHeaders().code() == 200) { // check response headers
                                        // Success
                                        Log.d(TAG, "Loading PDF Module. SUCCESS: " + url);

                                        File file = response.getResult();

                                        setupAndLoadPdfView(pdfView.fromFile(file));

                                        pdfView.setVisibility(View.VISIBLE);
                                        progressBarModules.setVisibility(View.GONE);
                                    }
                                    else{
                                        // Error
                                        showError("Impossível fazer download");

                                        Log.d(TAG, "Loading PDF Module. FAILED on Download: Response object: " + response);
                                        if(e != null){
                                            Log.e(TAG, "Loading PDF Module. FAILED on Download: Exception: ", e);
                                        }

                                    }
                                }
                            });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Loading PDF Module. FAILED on setup, Exception: ", e);
                showError("Problemas na conex&#227;o com a internet. Tente novamente.");
            }
        }
        else{
            Log.d(TAG, "Loading PDF Module. FAILED on setup, URL NULL: ");
            showError("Url inválida.");
        }
    }

    private void showError(@StringRes int messageId){
        showError(getString(messageId));
    }
    private void showError(String message){
        imagePlaceholder.setVisibility(View.VISIBLE);
        progressBarModules.setVisibility(View.GONE);
        pdfView.setVisibility(View.INVISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method called after:
     *         pdfView.fromUri(Uri)
     *         or
     *         pdfView.fromFile(File)
     *         or
     *         pdfView.fromBytes(byte[])
     *         or
     *         pdfView.fromStream(InputStream) // stream is written to bytearray - native code cannot use Java Streams
     *         or
     *         pdfView.fromSource(DocumentSource)
     *         or
     *         pdfView.fromAsset(String)

     * @param pdfConfig object result from the above methods, to configure the pdf file.
     */
    private void setupAndLoadPdfView(PDFView.Configurator pdfConfig){
        pdfConfig
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(10)
//                .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
//                .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
//                .enableSwipe(true) // allows to block changing pages using swipe
//                .swipeHorizontal(false)
//                .enableDoubletap(true)
//                .defaultPage(0)
                // allows to draw something on the current page, usually visible in the middle of the screen
//                .onDraw(onDrawListener)
                // allows to draw something on all pages, separately for every page. Called only for visible pages
//                .onDrawAll(onDrawListener)
//                .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
//                .onPageChange(onPageChangeListener)
//                .onPageScroll(onPageScrollListener)
//                .onError(onErrorListener)
//                .onPageError(onPageErrorListener)
//                .onRender(onRenderListener) // called after document is rendered for the first time
                // called on single tap, return true if handled, false to toggle scroll handle visibility
//                .onTap(onTapListener)
//                .onLongPress(onLongPressListener)
//                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//                .password(null)
//                .scrollHandle(null)
//                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
//                .linkHandler(DefaultLinkHandler)
//                .pageFitPolicy(FitPolicy.WIDTH)
//                .pageSnap(true) // snap pages to screen boundaries
//                .pageFling(false) // make a fling change only a single page like ViewPager
//                .nightMode(false) // toggle night mode
                .load();
    }


    public static boolean verifyWritePermission(final Activity activity, final Fragment fragment) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
//                alertDialog.setMessage(R.string.esta_permissao_necessaria);
                alertDialog.setMessage("Esta permissao é necessaria");
                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (fragment == null || fragment.isAdded()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_PERMISSION);
                            }
                        }
                    }
                });
//                alertDialog.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (activity != null) {
                            activity.finish();
                        }
                    }
                });
                alertDialog.show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_PERMISSION);
                }
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadModule();
                } else if (verifyWritePermission(this, null)) {
                    loadModule();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // PDF PERMISSION
        if (requestCode == REQUEST_CODE_WRITE_PERMISSION) {
            loadModule();
        }
    }

}
