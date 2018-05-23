package com.example.seung.visioncollege;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class LoggedActivity extends AppCompatActivity {

    private Button logOutButton, postPageButton;
    private Session session;
    private RecyclerView mPostList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);

        logOutButton = findViewById(R.id.logoutButton);
        postPageButton = findViewById(R.id.postButton);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("post");

        mPostList = findViewById(R.id.post_list);
        mPostList.setHasFixedSize(true);
        mPostList.setLayoutManager(new LinearLayoutManager(this));

        // 로그인화면 액티비티에서 입력된 유저 닉네임을 획득하여 텍스트뷰에 뿌리기 위한 코드
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");

        session = new Session(this);
        if(!session.loggedIn()){
            logout();
        }
        //로그아웃 버튼 클릭시 로그아웃 메서드 실행

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // 게시판 버튼 클릭 시, 작동
        postPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggedActivity.this, PostActivity.class);
                startActivity(intent);
                intent.putExtra("username", username);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // FirebaseRecyClerAdapter 클래스 쓰려면 앱 레벨의 그래들 디펜던시에 firebase-ui 추가해야함~!
        FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(

                Post.class,
                R.layout.post_row,
                PostViewHolder.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
            }
        };

        mPostList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String title){
            TextView post_title = mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc){

            TextView post_desc = mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image){
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.get().load(image).into(post_image);
        }

    }

    // 로그아웃 실행시, 로그인 액티비티로 돌아가고 로그인 세션 종료를 설정하는 메소드
    private void logout(){
        FirebaseAuth.getInstance().signOut();
        session.setLoggedIn(false);
        startActivity(new Intent(LoggedActivity.this, MainActivity.class));
        finish();
    }
}