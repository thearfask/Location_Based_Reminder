package ajayverma26.com.ajaylbrfordelete;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ajay on 24/6/17.
 */

public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.MyHolder> {

    private Context mContext;
    private ArrayList<Reminder> mReminderArrayList;

    public AdapterRecycler(Context mContext, ArrayList<Reminder> mReminderArrayList) {
        this.mContext = mContext;
        this.mReminderArrayList = mReminderArrayList;
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        public void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_adapter,null);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        Reminder reminder = mReminderArrayList.get(position);

        holder.info.setText(
                "Title:- "+reminder.getTitle()+"\n"+
                        "ID:- "+reminder.getId()+"\n"+
                        "Type:- "+reminder.getType()+"\n"+
                        "Radius:- "+reminder.getRadius()+"\n");
        // mReminderArrayList.get(position).getAddress()+"\n"+
    }

    @Override
    public int getItemCount() {
        return mReminderArrayList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView info;
        ImageView imageIcon;

        public MyHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            info = (TextView) itemView.findViewById(R.id.adapterEdtInfo);
            imageIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);

        }

        @Override
        public void onClick(View v) {

            mOnItemClickListener.OnItemClick(getPosition());

        }
    }
}

