package com.example.anil.aplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Anil on 2/26/2018.
 */

class MySongsAdapter extends RecyclerView.Adapter<MySongsAdapter.Viewholder> {
    ArrayList<HashMap<String,String>> songlist;
    Context ct;
    public MySongsAdapter(MainActivity mainActivity, ArrayList<HashMap<String, String>> songsList) {
        this.songlist=songsList;
        this.ct=mainActivity;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false));
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        holder.title.setText(songlist.get(position).get("songTitle"));
        //holder.path.setText(songlist.get(position).get("songPath"));
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art = null;
        BitmapFactory.Options bfo=new BitmapFactory.Options();

        mmr.setDataSource(ct, Uri.parse(songlist.get(position).get("songPath")));
        rawArt = mmr.getEmbeddedPicture();

// if rawArt is null then no cover art is embedded in the file or is not 
// recognized as such.
        if (null != rawArt)
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        holder.iv.setImageBitmap(art);
        //Picasso.with(ct).load().into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return songlist.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        TextView title,path;
        ImageView iv;
        public Viewholder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.song_title);
            //path=(TextView)itemView.findViewById(R.id.song_path);
            iv=(ImageView)itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion=getAdapterPosition();
                    Intent i=new Intent(ct,SongPlayer.class);
                    i.putExtra("pos",postion);
                    i.putExtra("songslist",songlist);
                    //i.putExtra("title",songlist.get(postion).get("songTitle"));
                    //i.putExtra("path",songlist.get(postion).get("songPath"));
                    ct.startActivity(i);
                    //new SongPlayer(songlist);
                }
            });
        }
    }
}
