package com.duy.compile;

import android.content.Context;
import android.os.AsyncTask;

import com.duy.compile.builder.AndroidProjectBuilder;
import com.duy.compile.builder.model.BuildType;
import com.duy.project.file.android.AndroidProject;

import java.io.File;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;

public class BuildAndroid extends AsyncTask<AndroidProject, Object, File> {
    private static final String TAG = "BuildApkTask";
    private Context context;
    private BuildAndroid.CompileListener listener;
    private DiagnosticCollector mDiagnosticCollector;
    private Exception error;

    public BuildAndroid(Context context, BuildAndroid.CompileListener listener) {
        this.context = context;
        this.listener = listener;
        mDiagnosticCollector = new DiagnosticCollector();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onStart();
    }

    @Override
    protected File doInBackground(AndroidProject... params) {
        AndroidProject projectFile = params[0];
        if (params[0] == null) return null;
        AndroidProjectBuilder builder = new AndroidProjectBuilder(context, projectFile, mDiagnosticCollector);
        if (builder.build(BuildType.DEBUG)) {
            return projectFile.getApkSigned();
        } else {
            return null;
        }
    }


    @Override
    protected void onPostExecute(final File result) {
        super.onPostExecute(result);
        if (result == null) {
            listener.onError(error, mDiagnosticCollector.getDiagnostics());
        } else {
            listener.onComplete(result, mDiagnosticCollector.getDiagnostics());
        }
    }

    public interface CompileListener {
        void onStart();

        void onError(Exception e, List<Diagnostic> diagnostics);

        void onComplete(File apk, List<Diagnostic> diagnostics);

    }
}