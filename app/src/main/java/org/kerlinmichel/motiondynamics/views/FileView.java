package org.kerlinmichel.motiondynamics.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.kerlinmichel.motiondynamics.R;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileView extends AppCompatActivity {

    private ListView fileListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);
        File dir = new File(this.getApplicationContext().getFilesDir().getAbsolutePath());
        FileAdapter arrayAdapter = new FileAdapter(this.getApplicationContext(), R.layout.file_item, Arrays.asList(dir.listFiles()));
        fileListView = (ListView)findViewById(R.id.fileList);
        fileListView.setAdapter(arrayAdapter);
    }

    public class FileAdapter extends ArrayAdapter<File> {

        public FileAdapter(Context context, int resource, List<File> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(getContext(), R.layout.file_item, null);
            }
            ((TextView)view.findViewById(R.id.name)).setText(this.getItem(position).getName());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FileView.this, FileInfo.class);
                    Bundle extras = new Bundle();
                    extras.putString("file_abs_path", FileAdapter.this.getItem(position).getAbsolutePath());
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}
