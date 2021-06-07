

package time.stamp.com.t.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;


import android.os.Environment;
import android.transition.Fade;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.FileOutputStream;


import time.stamp.com.t.GlideApp;
import time.stamp.com.t.R;
import time.stamp.com.t.data.AppItem;
import time.stamp.com.t.data.DataManager;
import time.stamp.com.t.db.DbIgnoreExecutor;
import time.stamp.com.t.service.AlarmService;
import time.stamp.com.t.service.AppService;
import time.stamp.com.t.util.AppUtil;
import time.stamp.com.t.util.PreferenceManager;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "Shreyas";

    /**
     * ToDo:
     * Whenever this application is opened, after all the data has been collected from
     * android API's create the file and write the contents.
     *Steps :
     * 1. Create a file with name as follows :
     *      a. Filename should start with stats_DATE_TIME.csv
     *          ex : stats_20-04-2021_8-13.csv
     * 2. Get all the data available.
     *      Line : 351
     *      private TextView mName;
     *      private TextView mUsage;
     *      private TextView mTime;
     *      private TextView mDataUsage;
     *      save them in file above like this,
     *      ex : mName, mUsage, mTime, mDataUsage
     * 3. Save the file in any location that android allows your application to save (Task)
     */



    private LinearLayout mSort;
    private Switch mSwitch;
    private TextView mSwitchText;
    private RecyclerView mList;
    private MyAdapter mAdapter;
    private AlertDialog mDialog;
    private SwipeRefreshLayout mSwipe;
    private TextView mSortName;
    private long mTotal;
    private int mDay;
    private PackageManager mPackageManager;

   String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE};

    public String getFileName() {
        Calendar cc = Calendar.getInstance();
        int year = cc.get(Calendar.YEAR);
        int month = cc.get(Calendar.MONTH);
        int mDay = cc.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "Date " + year + ":" + month + ":" + mDay);
        int mHour = cc.get(Calendar.HOUR_OF_DAY);
        int mMinute = cc.get(Calendar.MINUTE);
        Log.d(TAG, "time_format" + String.format("%02d:%02d", mHour , mMinute));
        //stats_DATE_TIME.csv
        return "stats_" + year + "-" + month + "-" + mDay + "-" + mHour + "-" + mMinute + ".csv";
    }

    public void writeAFile(){
        String fileName = "stats.txt";
        String textToWrite = "This is some text!";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(fileName , Context.MODE_PRIVATE);
            outputStream.write(textToWrite.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    public void writeToFile() {
            Context context = getApplicationContext();
            StringBuffer sb = new StringBuffer();
            try {
                if(appData != null) {
                    for(AppItem ap : appData) {
                        sb.append(ap.mName + "," + ap.mPackageName + "," + ap.mCount + "," + ap.mEventTime + "," + ap.mEventType + "," + ap.mUsageTime + "," + ap.mMobile + "\n");
                    }

                    FileWriter out = new FileWriter(new File(Environment.getExternalStorageDirectory() + "/", getFileName()));
                    Log.d(TAG, "FILE PATH " + Environment.getExternalStorageDirectory()); //context.getFilesDir()
                    out.write(sb.toString());
                    out.close();
                }
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
    }

    private static final int REQUEST = 112;




    Button checkAddiction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called ");
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

            requestPermissions(PERMISSIONS, 1000);
            if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST);
            } else {
                //do here
                Toast.makeText(getApplicationContext(), "Written", Toast.LENGTH_SHORT).show();
            }
        } else {
            //nothing
        }


        // https://guides.codepath.com/android/Shared-Element-Activity-Transition
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Fade(Fade.OUT));
        setContentView(R.layout.activity_main);
        mPackageManager = getPackageManager();



        mSort = findViewById(R.id.sort_group);
        mSortName = findViewById(R.id.sort_name);
        mSwitch = findViewById(R.id.enable_switch);
        mSwitchText = findViewById(R.id.enable_text);
        mAdapter = new MyAdapter();

        mList = findViewById(R.id.list);
        mList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mList.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, getTheme()));
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(mAdapter);

        initLayout();
        initEvents();
        initSpinner();
        initSort();

        if (DataManager.getInstance().hasPermission(getApplicationContext())) {
            process();
            startService(new Intent(this, AlarmService.class));
        }

        checkAddiction = (Button) findViewById(R.id.checkAddiction);
        checkAddiction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long timeUsage = 0L;
                if(appData != null) {
                    for (AppItem ap : appData) {
                        timeUsage += ap.mUsageTime;
                        Log.d("vinay", "Musage " + ap.mUsageTime);
                    }
                }
                if(timeUsage != 0) {
                    long totalTimeSpent = timeUsage / (1000 * 3600);
                    new AsyncTaskRunner(totalTimeSpent).execute();
                }
            }
        });
    }

    class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;
        long usageHours = 0;
        public AsyncTaskRunner(long val) {
            this.usageHours = val;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            HttpURLConnection urlConnection = null;
            try {

                URL url = new URL("http:// 192.168.109.43:9001?val="+usageHours);
                 urlConnection = (HttpURLConnection) url.openConnection();
                int code = urlConnection.getResponseCode();
                if(code==200){
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null)
                            result += line;
                    }
                    in.close();
                    Log.d("vinay", result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("messege")
                    .setPositiveButton("ok", null)
                    .setMessage( result.equals("[\'Addicted\']") ? "You are Addicted to smartphones" : "You are not addicted to smartphones" )
                    .show();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "ProgressDialog",
                    "Please Wait while we check the addiction state");
        }


        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
  public void writeFileOnInternalStorage(Context context,String filename,String filebody ){
     filename = getFileName();
      try {
          Log.d("Shreyas", Environment.getRootDirectory().getCanonicalPath() + " abs path");
      }catch(Exception e) { }

      File dir = new File( "/storage/self/primary", "mydir");
      if(!dir.exists()){
          dir.mkdir();
      }

      try {
          File gpxfile = new File(dir, filename);
          FileWriter writer = new FileWriter(gpxfile);
          writer.append(filebody);
          writer.flush();
          writer.close();
      } catch (Exception e){
          e.printStackTrace();
      }
  }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initLayout() {
        mSwipe = findViewById(R.id.swipe_refresh);
        if (DataManager.getInstance().hasPermission(getApplicationContext())) {
            mSwitchText.setText(R.string.enable_apps_monitoring);
            mSwitch.setVisibility(View.GONE);
            mSort.setVisibility(View.VISIBLE);
            mSwipe.setEnabled(true);
        } else {
            mSwitchText.setText(R.string.enable_apps_monitor);
            mSwitch.setVisibility(View.VISIBLE);
            mSort.setVisibility(View.GONE);
            mSwitch.setChecked(false);
            mSwipe.setEnabled(false);
        }
    }

    private void initSort() {
        if (DataManager.getInstance().hasPermission(getApplicationContext())) {
            mSort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    triggerSort();
                }
            });
        }
    }

    private void triggerSort() {
        mDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.sort)
                .setSingleChoiceItems(R.array.sort, PreferenceManager.getInstance().getInt(PreferenceManager.PREF_LIST_SORT), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PreferenceManager.getInstance().putInt(PreferenceManager.PREF_LIST_SORT, i);
                        process();
                        mDialog.dismiss();
                    }
                })
                .create();
        mDialog.show();
    }

    private void initSpinner() {
        if (DataManager.getInstance().hasPermission(getApplicationContext())) {
            Spinner spinner = findViewById(R.id.spinner);
            spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.duration, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (mDay != i) {
                        int[] values = getResources().getIntArray(R.array.duration_int);
                        mDay = values[i];
                        process();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }

    private void initEvents() {
        if (!DataManager.getInstance().hasPermission(getApplicationContext())) {
            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        Intent intent = new Intent(MainActivity.this, AppService.class);
                        intent.putExtra(AppService.SERVICE_ACTION, AppService.SERVICE_ACTION_CHECK);
                        startService(intent);
                    }
                }
            });
        }
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                process();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!DataManager.getInstance().hasPermission(getApplicationContext())) {
            mSwitch.setChecked(false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent is called");
        super.onNewIntent(intent);
        if (DataManager.getInstance().hasPermission(this)) {
            mSwipe.setEnabled(true);
            mSort.setVisibility(View.VISIBLE);
            mSwitch.setVisibility(View.GONE);
            initSpinner();
            initSort();
            process();
        }
    }

    private void process() {
        if (DataManager.getInstance().hasPermission(getApplicationContext())) {
            mList.setVisibility(View.INVISIBLE);
            int sortInt = PreferenceManager.getInstance().getInt(PreferenceManager.PREF_LIST_SORT);
            mSortName.setText(getSortName(sortInt));
            new MyAsyncTask().execute(sortInt, mDay);
        }
    }

    private String getSortName(int sortInt) {
//        return getResources().getStringArray(R.array.sort)[sortInt];
        return getResources().getString(R.string.sort_by);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AppItem info = mAdapter.getItemInfoByPosition(item.getOrder());
        switch (item.getItemId()) {
            case R.id.ignore:
                DbIgnoreExecutor.getInstance().insertItem(info);
                process();
                Toast.makeText(this, R.string.ignore_success, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.open:
                startActivity(mPackageManager.getLaunchIntentForPackage(info.mPackageName));
                return true;
            case R.id.more:
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + info.mPackageName));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), 1);
                return true;
            case R.id.sort:
                triggerSort();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(">>>>>>>>", "result code " + requestCode + " " + resultCode);
        if (resultCode > 0) process();
    }
private List<AppItem> appData = null;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null) mDialog.dismiss();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private List<AppItem> mData;

        MyAdapter() {
            super();
            mData = new ArrayList<>();
        }

        void updateData(List<AppItem> data) {
            mData = data;
            appData = data;
            writeToFile();
            notifyDataSetChanged();
        }

        AppItem getItemInfoByPosition(int position) {
            if (mData.size() > position) {
                return mData.get(position);
            }
            return null;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            AppItem item = getItemInfoByPosition(position);
            holder.mName.setText(item.mName);
            holder.mUsage.setText(AppUtil.formatMilliSeconds(item.mUsageTime));
            holder.mTime.setText(String.format(Locale.getDefault(),
                    "%s · %d %s",
                    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(item.mEventTime)),
                    item.mCount,
                    getResources().getString(R.string.times_only)));
            holder.mDataUsage.setText(String.format(Locale.getDefault(), "%s", AppUtil.humanReadableByteCount(item.mMobile)));
            if (mTotal > 0) {
                holder.mProgress.setProgress((int) (item.mUsageTime * 100 / mTotal));
            } else {
                holder.mProgress.setProgress(0);
            }
            GlideApp.with(MainActivity.this)
                    .load(AppUtil.getPackageIcon(MainActivity.this, item.mPackageName))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(holder.mIcon);
            holder.setOnClickListener(item);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

            private final TextView mName;
            private final TextView mUsage;
            private final TextView mTime;
            private final TextView mDataUsage;
            private final ImageView mIcon;
            private final ProgressBar mProgress;

            MyViewHolder(View itemView) {
                super(itemView);
                mName = itemView.findViewById(R.id.app_name);
                mUsage = itemView.findViewById(R.id.app_usage);
                mTime = itemView.findViewById(R.id.app_time);
                mDataUsage = itemView.findViewById(R.id.app_data_usage);
                mIcon = itemView.findViewById(R.id.app_image);
                mProgress = itemView.findViewById(R.id.progressBar);
                itemView.setOnCreateContextMenuListener(this);
            }

            @SuppressLint("RestrictedApi")
            void setOnClickListener(final AppItem item) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra(DetailActivity.EXTRA_PACKAGE_NAME, item.mPackageName);
                        intent.putExtra(DetailActivity.EXTRA_DAY, mDay);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(MainActivity.this, mIcon, "profile");
                        startActivityForResult(intent, 1, options.toBundle());
                    }
                });
            }

            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                int position = getAdapterPosition();
                AppItem item = getItemInfoByPosition(position);
                contextMenu.setHeaderTitle(item.mName);
                contextMenu.add(Menu.NONE, R.id.open, position, getResources().getString(R.string.open));
                if (item.mCanOpen) {
                    contextMenu.add(Menu.NONE, R.id.more, position, getResources().getString(R.string.app_info));
                }
                contextMenu.add(Menu.NONE, R.id.ignore, position, getResources().getString(R.string.ignore));
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class MyAsyncTask extends AsyncTask<Integer, Void, List<AppItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipe.setRefreshing(true);
        }

        @Override
        protected List<AppItem> doInBackground(Integer... integers) {
            return DataManager.getInstance().getApps(getApplicationContext(), integers[0], integers[1]);
        }

        @Override
        protected void onPostExecute(List<AppItem> appItems) {
            mList.setVisibility(View.VISIBLE);
            mTotal = 0;
            for (AppItem item : appItems) {
                if (item.mUsageTime <= 0) continue;
                mTotal += item.mUsageTime;
                item.mCanOpen = mPackageManager.getLaunchIntentForPackage(item.mPackageName) != null;
            }
            mSwitchText.setText(String.format(getResources().getString(R.string.total), AppUtil.formatMilliSeconds(mTotal)));
            mSwipe.setRefreshing(false);
            mAdapter.updateData(appItems);
        }
    }
}
