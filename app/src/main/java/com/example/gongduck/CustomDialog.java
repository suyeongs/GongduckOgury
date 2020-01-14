package com.example.gongduck;

import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class CustomDialog {

    private TextToSpeech tts;
    Intent intent = null;
    private Context context;
    public String text;
    TextView messageInfomation;

    public CustomDialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String title) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        TextView titleTextView = (TextView)dlg.findViewById(R.id.title);
        titleTextView.setText(title);

        TextView message = (TextView)dlg.findViewById(R.id.messageInfo);

        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);
        messageInfomation = (TextView)dlg.findViewById(R.id.messageInfo);
        final String site = title;

        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });


        if(site.equals("폭염")){
            messageInfomation.setText("가장 더운 오후 2시~5시에는 야외활동이나 작업을 되도록 하지 않습니다.\n" +
                    "냉방기기 사용 시, 실내외 온도차를 5℃ 내외로 유지하여 냉방병을 예방합니다.");
            intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/contents/prevent/prevent07.html?menuSeq=126"));
        }
        else if(site.equals("한파")){
            messageInfomation.setText("동상에 걸리면, 비비지 말고 따뜻한 물에 30분가량 담그고, 온도를 유지하며 즉시 병원으로 갑니다.\n" +
                    "빙판길 낙상사고를 줄이기 위해서는 보폭을 줄이고 굽이 낮고 미끄럼이 방지된 신발을 신는 등 주의해야 합니다.");
            intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/contents/prevent/prevent06.html?menuSeq=126"));;
        }
        else if(site.equals("화재")){
            messageInfomation.setText("자고 있을 때 화재 경보가 울리면 불이 났는지 확인하려 하기보다는 소리를 질러 모든 사람들을 깨우고 모이게 한 후 대처방안에 따라 밖으로 대피합니다." +
                    " 불이 난 것을 발견하면 “불이야!”라고 소리치거나 비상벨을 눌러 주변에 알리도록 합니다.");
            intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/contents/prevent/SDIJKM5116.html?menuSeq=127"));
        }
        else{
            messageInfomation.setText("가스와 전깃불을 차단하고 문을 열어 출구를 확보합니다. 지진으로 흔들릴 때는 튼튼한 탁자 아래에서 몸을 보호합니다.\n그 후 낙하물이 없는 넓은 공간으로 대피합니다.");
            intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/contents/prevent/prevent09.html?menuSeq=126"));
        }

        tts=new TextToSpeech(this.context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.KOREA);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }
                    else{
                        ConvertTextToSpeech();
                    }
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 항목에 맞는 사이트로 이동한다.
                context.startActivity(intent);
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
                if(tts != null){
                    tts.stop();
                    tts.shutdown();
                    tts = null;
                }
            }
        });
    }






    protected void onPause() {
        // TODO Auto-generated method stub

        if(tts != null){

            tts.stop();
            tts.shutdown();
        }
    }

    public void onClick(View v){

        ConvertTextToSpeech();

    }

    private void ConvertTextToSpeech() {
        // TODO Auto-generated method stub
        text = messageInfomation.getText().toString();
        if(text==null||"".equals(text))
        {
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
