package org.kerlinmichel.motiondynamics.views;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.kerlinmichel.motiondynamics.R;

import java.io.File;

public class FileInfo extends AppCompatActivity {

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = new File((String)getIntent().getExtras().get("file_abs_path"));
        setContentView(R.layout.activity_file_info);
        ((TextView)findViewById(R.id.file_info_name)).setText(file.getName());
        findViewById(R.id.delete_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file.delete();
                Intent intent = new Intent(FileInfo.this, FileView.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.share_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                Uri fileURI = FileProvider.getUriForFile(FileInfo.this,
                        "org.kerlinmichel.motiondynamics.MyFileProvider",
                        file);
                sendIntent.putExtra(Intent.EXTRA_STREAM, fileURI);
                sendIntent.setType("text/*");
                startActivity(sendIntent);
            }
        });
    }
}
