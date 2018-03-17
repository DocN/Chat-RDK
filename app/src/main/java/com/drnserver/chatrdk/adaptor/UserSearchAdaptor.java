package com.drnserver.chatrdk.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drnserver.chatrdk.R;
import com.drnserver.chatrdk.model.UserIndex;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserSearchAdaptor extends RecyclerView.Adapter<UserSearchAdaptor.UserSearchViewHolder> {
    private Context sContext;
    private ArrayList<UserIndex> sList;
    private OnItemClickListener sListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        sListener = listener;
    }

    public UserSearchAdaptor(Context context, ArrayList<UserIndex> userList) {
        sContext = context;
        sList = userList;
    }

    @Override
    public UserSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(sContext).inflate(R.layout.list_layout, parent, false);
        return new UserSearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserSearchViewHolder holder, int position) {
        UserIndex currentItem = sList.get(position);
        String userSearchName = currentItem.getUserSearchName();
        String userSearchImage = currentItem.getUserSearchImage();
        String userSearchStatus = currentItem.getUserSearchStatus();
        //for the future if needed
        String userSearchEmail = currentItem.getUserSearchEmail();
        String userSearchPhone = currentItem.getUserSearchPhone();
        holder.textViewName.setText(userSearchName);
        holder.textViewStatus.setText(userSearchStatus);
        Glide.with(sContext).load(userSearchImage).into(holder.sImageView);

    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public class UserSearchViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView sImageView;
        public TextView textViewName;
        public TextView textViewStatus;

        public UserSearchViewHolder(View itemView) {
            super(itemView);
            sImageView = itemView.findViewById(R.id.profile_image);
            textViewName = itemView.findViewById(R.id.name_text);
            textViewStatus = itemView.findViewById(R.id.status_text);

            //Catch the click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public  void onClick(View v) {
                    if(sListener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            sListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
