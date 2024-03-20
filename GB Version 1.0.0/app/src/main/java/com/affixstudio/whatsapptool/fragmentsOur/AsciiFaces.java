package com.affixstudio.whatsapptool.fragmentsOur;

import static com.affixstudio.whatsapptool.fragmentsOur.AsciiFaces.asciiFaces;
import static com.affixstudio.whatsapptool.fragmentsOur.DecorationText.Adepter.isInstalled;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affixstudio.whatsapptool.R;
import com.affixstudio.whatsapptool.activityOur.schedule_sms;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;


public class AsciiFaces extends Fragment {


    public static String[] asciiFaces=new String []{"( .-. )", "( .o.)",
            "( `·´ )",
            "( ° ͜ ʖ °)",
            "( ͡° ͜ʖ ͡°)",
            "( ⚆ _ ⚆ )",
            "( ︶︿︶)",
            "( ﾟヮﾟ)",
            "(\\/)(°,,,°)(\\/)",
            "(¬_¬)",
            "(¬º-°)¬",
            "(¬‿¬)",
            "(°ロ°)☝",
            "(´・ω・)っ",
            "(ó ì_í)",
            "(ʘᗩʘ')",
            "(ʘ‿ʘ)",
            "(̿▀̿ ̿Ĺ̯̿̿▀̿ ̿)̄",
            "(͡° ͜ʖ ͡°)",
            "(ಠ_ಠ)",
            "(ಠ‿ಠ)",
            "(ಠ⌣ಠ)",
            "(ಥ_ಥ)",
            "(ಥ﹏ಥ)",
            "(ง ͠° ͟ل͜ ͡°)ง",
            "(ง ͡ʘ ͜ʖ ͡ʘ)ง",
            "(ง •̀_•́)ง",
            "(ง'̀-'́)ง",
            "(ง°ل͜°)ง",
            "(ง⌐□ل͜□)ง",
            "(ღ˘⌣˘ღ)",
            "(ᵔᴥᵔ)",
            "(•ω•)",
            "(•◡•)/",
            "(⊙ω⊙)",
            "(⌐■_■)",
            "(─‿‿─)",
            "(╯°□°）╯",
            "(◕‿◕)",
            "(☞ﾟ∀ﾟ)☞",
            "(❍ᴥ❍ʋ)",
            "(っ◕‿◕)っ",
            "(づ｡◕‿‿◕｡)づ",
            "(ノಠ益ಠ)ノ",
            "(ノ・∀・)ノ",
            "(；一_一)",
            "(｀◔ ω ◔´)",
            "(｡◕‿‿◕｡)",
            "(ﾉ◕ヮ◕)ﾉ",
            "*<{:¬{D}}}",
            "=^.^=",
            "t(-.-t)",
            "| (• ◡•)|",
            "~(˘▾˘~)",
            "¬_¬",
            "¯(°_o)/¯",
            "¯\\_(ツ)_/¯",
            "°Д°",
            "ɳ༼ຈل͜ຈ༽ɲ",
            "ʅʕ•ᴥ•ʔʃ",
            "ʕ´•ᴥ•`ʔ",
            "ʕ•ᴥ•ʔ",
            "ʕ◉.◉ʔ",
            "ʕㅇ호ㅇʔ",
            "ʕ；•`ᴥ•´ʔ",
            "ʘ‿ʘ",
            "͡° ͜ʖ ͡°",
            "ζ༼Ɵ͆ل͜Ɵ͆༽ᶘ",
            "Ѱζ༼ᴼل͜ᴼ༽ᶘѰ",
            "ب_ب",
            "٩◔̯◔۶",
            "ಠ_ಠ",
            "ಠoಠ",
            "ಠ~ಠ",
            "ಠ‿ಠ",
            "ಠ⌣ಠ",
            "ಠ╭╮ಠ",
            "ರ_ರ",
            "ง ͠° ل͜ °)ง",
            "๏̯͡๏﴿",
            "༼ ºººººل͟ººººº ༽",
            "༼ ºل͟º ༽",
            "༼ ºل͟º༼",
            "༼ ºل͟º༽",
            "༼ ͡■ل͜ ͡■༽",
            "༼ つ ◕_◕ ༽つ",
            "༼ʘ̚ل͜ʘ̚༽",
            "ლ(´ڡ`ლ)",
            "ლ(́◉◞౪◟◉‵ლ)",
            "ლ(ಠ益ಠლ)",
            "ᄽὁȍ ̪őὀᄿ",
            "ᔑ•ﺪ͟͠•ᔐ",
            "ᕕ( ᐛ )ᕗ",
            "ᕙ(⇀‸↼‶)ᕗ",
            "ᕙ༼ຈل͜ຈ༽ᕗ",
            "ᶘ ᵒᴥᵒᶅ",
            "‎‎(ﾉಥ益ಥ）ﾉ",
            "≧☉_☉≦",
            "⊙▃⊙",
            "⊙﹏⊙",
            "┌( ಠ_ಠ)┘",
            "╚(ಠ_ಠ)=┐",
            "◉_◉",
            "◔ ⌣ ◔",
            "◔̯◔",
            "◕‿↼",
            "◕‿◕",
            "☉_☉",
            "☜(⌒▽⌒)☞",
            "☼.☼",
            "♥‿♥",
            "⚆ _ ⚆",
            "✌(-‿-)✌",
            "〆(・∀・＠)",
            "ノ( º _ ºノ)",
            "ノ( ゜-゜ノ)",
            "ヽ( ͝° ͜ʖ͡°)ﾉ",
            "ヽ(`Д´)ﾉ",
            "ヽ༼° ͟ل͜ ͡°༽ﾉ",
            "ヽ༼ʘ̚ل͜ʘ̚༽ﾉ",
            "ヽ༼ຈل͜ຈ༽ง",
            "ヽ༼ຈل͜ຈ༽ﾉ",
            "ヽ༼Ὸل͜ຈ༽ﾉ",
            "ヾ(⌐■_■)ノ",
            "꒰･◡･๑꒱",
            "﴾͡๏̯͡๏﴿",
            "｡◕‿◕｡",
            "ʕノ◔ϖ◔ʔノ",
            "꒰•̥̥̥̥̥̥̥ ﹏ •̥̥̥̥̥̥̥̥๑꒱",
            "ಠ_ರೃ",
            "(ू˃̣̣̣̣̣̣︿˂̣̣̣̣̣̣ ू)",
            "(ꈨຶꎁꈨຶ)۶”",
            "(ꐦ°᷄д°᷅)",
            "(۶ૈ ۜ ᵒ̌▱๋ᵒ̌ )۶ૈ=͟͟͞͞ ⌨",
            "₍˄·͈༝·͈˄₎◞ ̑̑ෆ⃛",
            "(*ﾟ⚙͠ ∀ ⚙͠)ﾉ❣",
            "٩꒰･ัε･ั ꒱۶",
            "ヘ（。□°）ヘ",
            "˓˓(ृ　 ु ॑꒳’)ु(ृ’꒳ ॑ ृ　)ु˒˒˒",
            "꒰✘Д✘◍꒱",
            "૮( ᵒ̌ૢཪᵒ̌ૢ )ა",
            "“ψ(｀∇´)ψ",
            "ಠﭛಠ",
            "(๑>ᴗ<๑)",
            "(۶ꈨຶꎁꈨຶ )۶ʸᵉᵃʰᵎ",
            "٩(•̤̀ᵕ•̤́๑)ᵒᵏᵎᵎᵎᵎ",
            "(oT-T)尸",
            "(✌ﾟ∀ﾟ)☞",
            "ಥ‿ಥ",
            "ॱ॰⋆(˶ॢ‾᷄﹃‾᷅˵ॢ)",
            "┬┴┬┴┤  (ಠ├┬┴┬┴",
            "( ˘ ³˘)♥",
            "Σ (੭ु ຶਊ ຶ)੭ु⁾⁾",
            "(⑅ ॣ•͈ᴗ•͈ ॣ)",
            "ヾ(´￢｀)ﾉ",
            "(•̀o•́)ง",
            "(๑•॒̀ ູ॒•́๑)",
            "⚈้̤͡ ˌ̫̮ ⚈้̤͡",
            "=͟͟͞͞ =͟͟͞͞ ﾍ( ´Д`)ﾉ",
            "(((╹д╹;)))",
            "•̀.̫•́✧",
            "(ᵒ̤̑ ₀̑ ᵒ̤̑)",
            "\\_(ʘ_ʘ)_/"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_ascii_faces, container, false);
        LinearLayout asciiFaceLayout=v.findViewById(R.id.asciiFaceLayout);
        RecyclerView recyclerView=v.findViewById(R.id.asciiRecler);

        adapter adapter=new adapter(getContext(),asciiFaceLayout);

        recyclerView.setAdapter(adapter);





        return v;
    }

}

class adapter extends RecyclerView.Adapter<adapter.viewHolder>{

    Context context;
    LinearLayout layout;

    public adapter(Context context, LinearLayout asciiFaceLayout) {
        this.context = context;
        this.layout=asciiFaceLayout;
    }

    @NonNull
    @Override
    public adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.decoration_text_recycle,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull adapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.asciiText.setText(asciiFaces[position]);
//        animation
        holder.linearLayout.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.recycle));


        holder.decorationCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboardManager=(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clipData= ClipData.newPlainText("AsciiFace",asciiFaces[position]);
                clipboardManager.setPrimaryClip(clipData);

                Snackbar.make(layout,"Copied", Snackbar.LENGTH_SHORT)
                        .setTextColor(ContextCompat.getColor(context,R.color.white))
                        .setBackgroundTint(ContextCompat.getColor(context, R.color.snackbarBg)).show();
            }
        });

        holder.decorationShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_SEND);

                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Share using");
                intent.putExtra(Intent.EXTRA_TEXT,asciiFaces[position]);
                context.startActivity(intent);

            }
        });
        SharedPreferences sp= context.getSharedPreferences("affix",Context.MODE_PRIVATE);
        if (sp.getInt(context.getString(R.string.isScheduleONTag),1)==0) {
            holder.schOpen.setVisibility(View.GONE);
        }
        holder.schOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "Opening Schedule Messages", Toast.LENGTH_LONG).show();



                Intent i=new Intent(context, schedule_sms.class);

                i.putExtra("Directmessage",asciiFaces[position]);

                i.putExtra("fromChat",1);
                context.startActivity(i);

            }
        });
        holder.decorationWA.setOnClickListener(view -> {
            if (isInstalled(context,"com.whatsapp"))
            {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_TEXT,asciiFaces[position]);
                context.startActivity(intent);
            }else
            {
                Snackbar.make(layout,"Whatsapp is not installed in your phone.", BaseTransientBottomBar.LENGTH_SHORT)
                        .setTextColor(ContextCompat.getColor(context,R.color.white))
                        .setBackgroundTint(ContextCompat.getColor(context, R.color.red)).show();
            }
            });

        holder.decorationWB.setOnClickListener(view -> {
            if (isInstalled(context,"com.whatsapp.w4b"))
            {

            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("com.whatsapp.w4b");
            intent.putExtra(Intent.EXTRA_TEXT,asciiFaces[position]);
            context.startActivity(intent);

        }else
        {
            Snackbar.make(layout,"Whatsapp business is not installed in your phone.", BaseTransientBottomBar.LENGTH_SHORT)
                    .setTextColor(ContextCompat.getColor(context,R.color.white))
                    .setBackgroundTint(ContextCompat.getColor(context, R.color.red)).show();
        }

        });


    }

    @Override
    public int getItemCount() {
        return asciiFaces.length;
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView asciiText;
        ImageView decorationCopy,decorationShare,decorationWA,decorationWB,schOpen;
        LinearLayout linearLayout;

        public viewHolder(@NonNull View itemView)

        {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linerView);

             asciiText=itemView.findViewById(R.id.decorationTB);
            decorationCopy=itemView.findViewById(R.id.decorationCopy);
            decorationShare=itemView.findViewById(R.id.decorationShare);
            decorationWA=itemView.findViewById(R.id.decorationWA);
            decorationWB=itemView.findViewById(R.id.decorationWB);
            schOpen=itemView.findViewById(R.id.schOpen);

        }


    }
}