package com.example.vegeproject.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.vegeproject.MainActivity;
import com.example.vegeproject.R;
import com.example.vegeproject.barcode.result_fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchActivity extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("list");
    private FirebaseList firebaseList = new FirebaseList();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);

        ImageView searchButton = findViewById(R.id.searchButton) ;

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView recentSearchItem = findViewById(R.id.recentSearchItem);

                // 검색할 키워드 입력
                EditText editText = findViewById(R.id.editText);

                // 입력받은 키워드를 최근검색목록에 반영 → 아직은 1 아이템만 가능, 2학기에 누적되도록 구현 예정
                recentSearchItem.setText(editText.getText());

                // 입력값을 데이터베이스에서 검색하기 위한 함수 호출
                readFirebaseList(editText.getText().toString());
            }
        }) ;

    }

    private void readFirebaseList(String searchItem){

        // 입력값과 데이터베이스의 prdlstNm값이 일치하는 경우
        databaseReference.orderByChild("prdlstNm").equalTo(searchItem).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){

                    // firebaseList 클래스에 prdlstNm, allergy, barcode 값을 저장
                    firebaseList.prdlstNm = data.child("prdlstNm").getValue(String.class);
                    firebaseList.allergy = data.child("allergy").getValue(String.class);
                    firebaseList.barcode = data.child("barcode").getValue(String.class);

                    // 저장한 값을 로그로 확인
                    Log.w("FirebaseData", firebaseList.prdlstNm + ", " + firebaseList.allergy + ", " + firebaseList.barcode);

                    // 알러지값 가공
                    firebaseList.allergy = firebaseList.allergy.replaceAll(" ","").replaceAll("함유","").replaceAll("유래원재료","").replaceAll("\\*","").replaceAll("소고기", "쇠고기");;
                    String[] allergies = firebaseList.allergy.split(",");
                    for (int i=0; i<allergies.length; i++)
                        Log.w("ArrayData", allergies[i]);

                    //단계별 구분
                    String[] nutrients = {"돼지고기","소고기","닭고기","고등어","새우","게","조개류","난류","우유"};
                    for(String s : allergies) {
                        if(s.equals(nutrients[0])||s.equals(nutrients[1])) {//돼지고기,소고기
                            System.out.println("플렉시테리언");
                            Toast.makeText(getApplicationContext(),"플렉시테리언",Toast.LENGTH_LONG).show();
                            break;
                        }
                        else if(s.equals(nutrients[2])) {//닭고기
                            System.out.println("세미");
                            Toast.makeText(getApplicationContext(),"세미",Toast.LENGTH_LONG).show();
                            break;
                        }
                        else if(s.equals(nutrients[3])||s.equals(nutrients[4])||s.equals(nutrients[5])||s.equals(nutrients[6])) {//고등어,새우,게,조개류
                            Toast.makeText(getApplicationContext(),"페스코",Toast.LENGTH_LONG).show();
                            System.out.println("페스코");
                            break;
                        }
                        else if(s.equals(nutrients[7])) {//난류
                            Toast.makeText(getApplicationContext(),"락토오보",Toast.LENGTH_LONG).show();
                            System.out.println("락토오보");
                            break;
                        }
                        else if(s.equals(nutrients[8])) {//우유
                            Toast.makeText(getApplicationContext(),"락토",Toast.LENGTH_LONG).show();
                            System.out.println("락토");
                            break;
                        }
                        else  {
                            Toast.makeText(getApplicationContext(),"비건",Toast.LENGTH_LONG).show();
                            System.out.println("비건");
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
