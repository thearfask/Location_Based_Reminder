package ajayverma26.com.ajaylbrfordelete;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private AdapterRecycler mAdapterRecycler;
    private ArrayList<Reminder> mReminderArrayList;

    private DBHelper mDBHelper;

    FloatingActionButton fabAddReminder;
    Intent intent1,intentAfterEdit;
    Reminder reminder,reminderModify,reminderAfterEdit,reminderAfterDelete;


    ImageView editImage;
    String url;
    int mPosition;

    MapsActivity mapsActivity = new MapsActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent1 = getIntent();
        intentAfterEdit = getIntent();

        fabAddReminder = (FloatingActionButton) findViewById(R.id.fabAddReminder);


        fabAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,MapsActivity.class);
                //startActivity(i);

                startActivityForResult(i,1);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mReminderArrayList = new ArrayList<>();

        mDBHelper = new DBHelper( MainActivity.this);



        mReminderArrayList = mDBHelper.getAllReminders();
        mAdapterRecycler = new AdapterRecycler(MainActivity.this,mReminderArrayList);
        mRecyclerView.setAdapter(mAdapterRecycler);
        mAdapterRecycler.notifyDataSetChanged();
        mAdapterRecycler.setOnItemClickListener(new OnItemClick());
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);


    }

    class OnItemClick implements AdapterRecycler.OnItemClickListener{

        @Override
        public void OnItemClick(final int position) {

            mPosition = position;

            reminderModify = mReminderArrayList.get(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater factory = LayoutInflater.from(MainActivity.this);
            View view = factory.inflate(R.layout.layout_modify_dialog,null);

            url = "http://maps.google.com/maps/api/staticmap?center=" + reminderModify.getLat() + "," + reminderModify.getLng() + "&zoom=15&size=200x200&sensor=false";

            System.out.println("URL:-"+url.toString());

            System.out.println("Lat:-" +reminderModify.getLat()+"Long:-"+reminderModify.getLng());


            builder.setView(view);
            editImage = (ImageView) view.findViewById(R.id.modifyImgView);
            Glide.with(MainActivity.this)
                    .load(url)
                    .into(editImage);

            builder.setMessage(reminderModify.getTitle().toString()+"-"+reminderModify.getId())
                    .setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intentEdit = new Intent(MainActivity.this,MapsActivity.class);
                            intentEdit.putExtra("Flow","EditReminder");
                            intentEdit.putExtra("EditReminder",reminderModify);
                            startActivityForResult(intentEdit,2);


                        }
                    })
                    .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intentEdit = new Intent(MainActivity.this,MapsActivity.class);
                            intentEdit.putExtra("Flow","DeleteReminder");
                            intentEdit.putExtra("DeleteReminder",reminderModify);
                            startActivityForResult(intentEdit,3);





                            /*Reminder reminderDelete = new Reminder(reminderModify.getId(),reminderModify.getTitle(),reminderModify.getType(),reminderModify.getLat(),reminderModify.getLng(),reminderModify.getRadius());

                            //mapsActivity.getGeocoderString(reminderModify.getTitle());
                            mapsActivity.clearGeofence(reminderModify.getTitle());
                            System.out.println("to delete title:-"+reminderModify.getTitle());

                            mDBHelper.deleteReminder(reminderDelete);
                            mReminderArrayList.remove(position);
                            mAdapterRecycler.notifyDataSetChanged();*/

                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {
            reminder = (Reminder) data.getSerializableExtra("data");

            Toast.makeText(this, "Data->" + reminder.getTitle(), Toast.LENGTH_SHORT).show();

            mDBHelper.addReminder(reminder);
            mReminderArrayList.add(reminder);

            mReminderArrayList = mDBHelper.getAllReminders();
            mAdapterRecycler = new AdapterRecycler(MainActivity.this, mReminderArrayList);
            mRecyclerView.setAdapter(mAdapterRecycler);
            mAdapterRecycler.notifyDataSetChanged();
            mAdapterRecycler.setOnItemClickListener(new OnItemClick());


        }

        if (requestCode == 2){

            reminderAfterEdit = (Reminder) data.getSerializableExtra("modify");

            mDBHelper.updateReminder(reminderAfterEdit);
            mReminderArrayList.set(mPosition,reminderAfterEdit);

            mReminderArrayList = mDBHelper.getAllReminders();
            mAdapterRecycler = new AdapterRecycler(MainActivity.this, mReminderArrayList);
            mRecyclerView.setAdapter(mAdapterRecycler);
            mAdapterRecycler.notifyDataSetChanged();
            mAdapterRecycler.setOnItemClickListener(new OnItemClick());

        }
        if (requestCode == 3){

            reminderAfterDelete = (Reminder) data.getSerializableExtra("delete");

            Reminder reminderDelete = new Reminder(reminderAfterDelete.getId(),reminderAfterDelete.getTitle(),reminderAfterDelete.getType(),reminderAfterDelete.getLat(),reminderAfterDelete.getLng(),reminderAfterDelete.getRadius());

            mDBHelper.deleteReminder(reminderDelete);
            mReminderArrayList.remove(mPosition);
            mAdapterRecycler.notifyDataSetChanged();

        }
    }
}

