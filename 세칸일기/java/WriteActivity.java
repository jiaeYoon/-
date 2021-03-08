package com.example.mydiary_ver6;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WriteActivity extends AppCompatActivity {

    ImageButton save;
    ImageButton delete;
    EditText emoji1, emoji2, emoji3;
    //EditText backupemj1, backupemj2, backupemj3;
    public EditText d_write;
    CalendarView calendarView;
    LinearLayout linearLayout;

    private DiaryDB diaryDB;
    private String fileName;

    public ImageView imageView;
    ImageButton btImage;
    private final int GALLERY_CODE=1112;
    public String imagePath;
    Context context;

    ImageButton mPlay;
    ImageButton mPause;

    ActionBar actionBar;

    //Group no.
    public static final int ID_GROUP_FONT_FAMILY = 1;
    public static final int ID_GROUP_BACKGROUND = 2;
    public static final int ID_GROUP_SNOW = 3;
    public static final int ID_GROUP_PARENTS = 4;
    public static final int ID_GROUP_ME = 5;

    //Text Color Item No.
    public static final int ID_FONT_POETIC = 11;
    public static final int ID_FONT_MALGUN = 12;
    public static final int ID_FONT_DOKRIP = 13;
    public static final int ID_FONT_JUA = 14;
    public static final int ID_FONT_MOVIE = 15;

    public static final int ID_BG_BASIC = 21;
    public static final int ID_BG_LINE = 22;
    public static final int ID_BG_GRID = 23;
    public static final int ID_BG_SKY1 = 24;
    public static final int ID_BG_SKY2 = 25;
    public static final int ID_BG_SKY3 = 26;

    public static final int ID_ANIM_SNOW = 31;
    public static final int ID_ANIM_FLOWER = 32;
    public static final int ID_ANIM_NO = 33;

    MusicService mService;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MusicService.MusicServiceBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        setTitle("");

        diaryDB = new DiaryDB(this);

        save = (ImageButton)findViewById(R.id.save);
        delete = (ImageButton)findViewById(R.id.delete);
        emoji1 = (EditText)findViewById(R.id.emoji1);
        emoji2 = (EditText)findViewById(R.id.emoji2);
        emoji3 = (EditText)findViewById(R.id.emoji3);
        //backupemj1 = emoji1; backupemj2 = emoji2; backupemj3 = emoji3;
        d_write = (EditText) findViewById(R.id.d_write);
        calendarView = (CalendarView) findViewById(R.id.Dcalendar);
        linearLayout = (LinearLayout)findViewById(R.id.writelayout);
        context = WriteActivity.this;

        ImageButton mStart = (ImageButton)findViewById(R.id.startm);
        mPlay = (ImageButton)findViewById(R.id.play);
        mPause = (ImageButton)findViewById(R.id.pause);

        actionBar = getActionBar();

        getActionBar();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiary();
                GoToMain();
                Toast.makeText(v.getContext(), "일기 저장 완료", Toast.LENGTH_SHORT).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDiary();
                GoToMain();
                Toast.makeText(v.getContext(), "일기 삭제 완료", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");

        loadDiary();


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month+=1;
                d_write.setText(year+"년 "+month+"월 "+dayOfMonth+"일" + "\n\n\n\n\n");
                fileName = year+"년 "+month+"월 "+dayOfMonth+"일의 일기.txt";
//                emoji1 = backupemj1;
//                emoji2 = backupemj2;
//                emoji3 = backupemj3;  //이모티콘 있는 날짜 한 번 선택하면 다른 날짜에도 계속 그 이모티콘이 적용되는 걸 해결
                loadDiary();
            }
        });

        imageView = (ImageView)findViewById(R.id.imageGallery);
        btImage = (ImageButton)findViewById(R.id.btImage);

        btImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(WriteActivity.this, MusicService.class);
                startService(service);
                bindService(service, conn, BIND_AUTO_CREATE);
            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.play();
            }

        });


        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.pause();
            }
        });
    }

    private void saveDiary(){
        String c_emoji1 = emoji1.getText().toString();
        String c_emoji2 = emoji2.getText().toString();
        String c_emoji3 = emoji3.getText().toString();
        String content = d_write.getText().toString();

        if(content.length() > 0 || c_emoji1.length() > 0){
           File diaryFile = getDiaryFile();

           boolean newFile = true;
           if(diaryFile.exists()){
               newFile = false;
           }

               FileOutputStream fos;

            try {
                fos = new FileOutputStream(getDiaryFile());
                fos.write(c_emoji1.getBytes());
                fos.write(c_emoji2.getBytes());
                fos.write(c_emoji3.getBytes());
                fos.write(content.getBytes());
                fos.close();

                if (newFile){
                    diaryDB.saveDiary(diaryFile);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private File getDiaryFile(){
        if (fileName == null){
            //fileName = "diary-" + System.currentTimeMillis() + ".txt";
            fileName = getDate() + ".txt";
        }

        File diaryFile = new File(Environment.getExternalStorageDirectory(), fileName); //외부 저장소의 루트 dir 안에 filename의 경로 가진 File 객체
        return diaryFile;
    }

    public String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일의 일기", Locale.KOREA);
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    private void loadDiary(){
        File diaryFile = getDiaryFile();

        if(diaryFile.exists()){
            FileInputStream fis;

            try{
                fis = new FileInputStream(diaryFile);
            } catch (FileNotFoundException e){
                e.printStackTrace();
                return;
            }

            byte[] diaryData = null;        //FileInputStream은 파일의 데이터를 바이트로 읽어들임

            try{
                diaryData = new byte[fis.available()];      //available() : 이 파일 스트림의 파일의 전체 바이트 수 반환
                fis.read(diaryData);                        //파일 내용 저장
            } catch (IOException e){
                e.printStackTrace();
                return;
            }

            if(diaryData != null){
                String diaryString = new String(diaryData);
                if (diaryString.substring(0,2) != "20") {
                    String femoji1 = diaryString.substring(0,2); //final emoji
                    String femoji2 = diaryString.substring(2,4);
                    String femoji3 = diaryString.substring(4,6);
                    emoji1.setText(femoji1);
                    emoji2.setText(femoji2);
                    emoji3.setText(femoji3);

                    String fcontent = diaryString.substring(6);
                    d_write.setText(fcontent);
                }
            }
        }
    }

    private void deleteDiary(){
        File diaryFile = getDiaryFile();

        if (diaryFile.exists()){
            diaryDB.removeDiary(diaryFile);
            diaryFile.delete();
        }
    }

    private void GoToMain(){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture(data.getData()); //갤러리에서 가져오기
                    break;
                default:
                    break;
            }
        }
    }

    private void sendPicture(Uri imgUri) {
        imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);        //경로를 통해 비트맵으로 전환
        int height=bitmap.getHeight();
        int width=bitmap.getWidth();
        bitmap = bitmap.createScaledBitmap(bitmap,width/2, height/2, true);
        imageView.setImageBitmap(bitmap);
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }


    //3rd 3개의 메소드 추가 for MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        SubMenu mnuTextColor = menu.addSubMenu("글씨체설정");
        mnuTextColor.add(ID_GROUP_FONT_FAMILY, ID_FONT_POETIC, 1, "시인과 나");
        mnuTextColor.add(ID_GROUP_FONT_FAMILY, ID_FONT_MALGUN, 2, "맑은 고딕");
        mnuTextColor.add(ID_GROUP_FONT_FAMILY, ID_FONT_DOKRIP, 3, "YOON 독립");
        mnuTextColor.add(ID_GROUP_FONT_FAMILY, ID_FONT_JUA, 4, "주아");
        mnuTextColor.add(ID_GROUP_FONT_FAMILY, ID_FONT_MOVIE, 5, "영화자막체");

        SubMenu mnuTextStyle = menu.addSubMenu("배경설정");
        MenuItem basic = mnuTextStyle.add(ID_GROUP_BACKGROUND, ID_BG_BASIC, 1, "기본");
        mnuTextStyle.add(ID_GROUP_BACKGROUND, ID_BG_LINE, 2, "좁은 줄");
        mnuTextStyle.add(ID_GROUP_BACKGROUND, ID_BG_GRID, 3, "모눈종이");
        mnuTextStyle.add(ID_GROUP_BACKGROUND, ID_BG_SKY1, 4, "구름1");
        mnuTextStyle.add(ID_GROUP_BACKGROUND, ID_BG_SKY2, 5, "구름2");
        mnuTextStyle.add(ID_GROUP_BACKGROUND, ID_BG_SKY3, 6, "구름3");

        SubMenu mnuTextSize = menu.addSubMenu("애니메이션");
//        mnuTextSize.add(ID_GROUP_SNOW, ID_ANIM_NO, 1, "효과 없음");
        mnuTextSize.add(ID_GROUP_SNOW, ID_ANIM_SNOW, 2, "눈 내리기");
        mnuTextSize.add(ID_GROUP_SNOW, ID_ANIM_FLOWER, 3, "꽃잎");

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ImageView snowImg1 = (ImageView)findViewById(R.id.snowImage1);
        ImageView snowImg2 = (ImageView)findViewById(R.id.snowImage2);
        ImageView snowImg3 = (ImageView)findViewById(R.id.snowImage3);
        ImageView snowImg4 = (ImageView)findViewById(R.id.snowImage4);
        ImageView snowImg5 = (ImageView)findViewById(R.id.snowImage5);
        ImageView snowImg6 = (ImageView)findViewById(R.id.snowImage6);
        ImageView snowImg7 = (ImageView)findViewById(R.id.snowImage7);

        switch (item.getItemId()){

            case ID_FONT_POETIC:
                Typeface typeface1 = getResources().getFont(R.font.poetic);
                d_write.setTypeface(typeface1);
                return true;

            case ID_FONT_MALGUN:
                Typeface typeface2 = getResources().getFont(R.font.malgun);
                d_write.setTypeface(typeface2);
                return true;

            case ID_FONT_DOKRIP:
                Typeface typeface3 = getResources().getFont(R.font.dokrip);
                d_write.setTypeface(typeface3);
                return true;

            case ID_FONT_JUA:
                Typeface typeface4 = getResources().getFont(R.font.bmjua);
                d_write.setTypeface(typeface4);
                return true;

            case ID_FONT_MOVIE:
                Typeface typeface5 = getResources().getFont(R.font.movie);
                d_write.setTypeface(typeface5);
                return true;

            case ID_BG_BASIC:
                linearLayout.setBackground(getResources().getDrawable(R.drawable.memobg));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFF4F4F4));
                return true;

            case ID_BG_LINE:
                linearLayout.setBackgroundResource(R.drawable.linebg);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFDFDFE));
                return true;

            case ID_BG_GRID:
                linearLayout.setBackgroundResource(R.drawable.gridbg);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFDFDFE));
                return true;

            case ID_BG_SKY1:
                linearLayout.setBackgroundResource(R.drawable.sky);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF84A1DA));
                return true;

            case ID_BG_SKY2:
                linearLayout.setBackgroundResource(R.drawable.sky2);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF7692B0));
//                d_write.setTextColor(getColor(R.color.white));
//                calendarView.setBackgroundColor(Color.WHITE);
//                mPlay.setBackgroundColor(Color.WHITE);
                //save.setTextColor(Co)
                return true;

            case ID_BG_SKY3:
                linearLayout.setBackgroundResource(R.drawable.sky3);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF9487CE));
//                d_write.setTextColor(getColor(R.color.white));
//                calendarView.setBackgroundColor(Color.WHITE);
//                mPlay.setBackgroundColor(Color.WHITE);
                //save.setTextColor(Co)
                return true;

//            case ID_ANIM_NO:
//                snowImg1.clearAnimation(); snowImg2.clearAnimation(); snowImg3.clearAnimation();
//                snowImg4.clearAnimation(); snowImg5.clearAnimation(); snowImg6.clearAnimation();
//                snowImg7.clearAnimation();
//                return true;

            case ID_ANIM_SNOW:
                snowImg1.setImageResource(R.drawable.snow5);
                snowImg5.setImageResource(R.drawable.snowman);
                snowImg5.setRotation(25.0f);
                snowImg7.setImageResource(R.drawable.snow2);
                snowImg2.setImageResource(R.drawable.snowman2);
                snowImg2.setRotation(-28.0f);
                snowImg3.setImageResource(R.drawable.snow3);
                snowImg6.setImageResource(R.drawable.snowman2);
                snowImg6.setRotation(200.0f);
                snowImg4.setImageResource(R.drawable.snow5);

                Animation let_snow1 = AnimationUtils.loadAnimation(context, R.anim.snow1);
                Animation let_snow5 = AnimationUtils.loadAnimation(context, R.anim.snow5);
                Animation let_snow7 = AnimationUtils.loadAnimation(context, R.anim.snow7);
                Animation let_snow2 = AnimationUtils.loadAnimation(context, R.anim.snow2);
                Animation let_snow3 = AnimationUtils.loadAnimation(context, R.anim.snow3);
                Animation let_snow6 = AnimationUtils.loadAnimation(context, R.anim.snow6);
                Animation let_snow4 = AnimationUtils.loadAnimation(context, R.anim.snow4);
                snowImg1.startAnimation(let_snow2); snowImg5.startAnimation(let_snow5); snowImg7.startAnimation(let_snow7);
                snowImg2.startAnimation(let_snow1); snowImg3.startAnimation(let_snow3); snowImg6.startAnimation(let_snow6);
                snowImg4.startAnimation(let_snow4);
                return true;

            case ID_ANIM_FLOWER:
                snowImg1.setImageResource(R.drawable.flower1);
                snowImg5.setImageResource(R.drawable.flower1);
                snowImg7.setImageResource(R.drawable.flower1);
                snowImg2.setImageResource(R.drawable.flower2);
                snowImg3.setImageResource(R.drawable.flower2);
                snowImg6.setImageResource(R.drawable.flower3);
                snowImg4.setImageResource(R.drawable.flower3);

                Animation falling1 = AnimationUtils.loadAnimation(context, R.anim.snow1);
                Animation falling5 = AnimationUtils.loadAnimation(context, R.anim.snow5);
                Animation falling7 = AnimationUtils.loadAnimation(context, R.anim.snow7);
                Animation falling2 = AnimationUtils.loadAnimation(context, R.anim.snow2);
                Animation falling3 = AnimationUtils.loadAnimation(context, R.anim.snow3);
                Animation falling6 = AnimationUtils.loadAnimation(context, R.anim.snow6);
                Animation falling4 = AnimationUtils.loadAnimation(context, R.anim.snow4);
                snowImg1.startAnimation(falling1); snowImg5.startAnimation(falling5); snowImg7.startAnimation(falling7);
                snowImg2.startAnimation(falling2); snowImg3.startAnimation(falling3); snowImg6.startAnimation(falling6);
                snowImg4.startAnimation(falling4);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
