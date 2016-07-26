package com.example.httpclientget;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MainActivity extends ActionBarActivity {
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			Toast.makeText(MainActivity.this, (String)msg.obj, 0).show();
		}
	};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	public void get(View v ){
		EditText et_name = (EditText) findViewById(R.id.et_name);
		EditText et_pass = (EditText) findViewById(R.id.et_pass);
		
		final String name = et_name.getText().toString();
		final String pass = et_pass.getText().toString();
		
		//使用HttpClient框架做get方式提交
		Thread t1 = new Thread(){
			@Override
			public void run() {
				//指定path路径(get提交的服务器路径)
				//String path = "http://localhost:8080/AndroidServer/login?username=xiaojiang&password=123";
				String path = "http://10.100.39.113:8080/AndroidServer/login?username="+URLEncoder.encode(name)+"&password="+pass;
				//创建HttpClient对象
				HttpClient hc = new DefaultHttpClient();
				//创建HttpGet对象,构造方法的参数为要提交的网址
				HttpGet hg = new HttpGet(path);
				//使用客户端对象,把get请求对象发送出去
				try {
					HttpResponse hr = hc.execute(hg);
					//拿到相应头中的状态行
					StatusLine sl = hr.getStatusLine();
					//判断相应码是否为200
					if(sl.getStatusCode() == 200){
						//拿到相应头实体
						HttpEntity he = hr.getEntity();
						//拿到实体中的内容，即服务器返回的输入流
						InputStream is = he.getContent();
						String text = Utils.getTextFromStream(is);
						System.out.println(text+"hhhh");
						
						//发送消息让主线程刷新ＵＩ
						Message msg = handler.obtainMessage();
						msg.obj = text;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		};
		t1.start();
	}
	public void post(View v){
		EditText et_name = (EditText) findViewById(R.id.et_name);
		EditText et_pass = (EditText) findViewById(R.id.et_pass);
		
		final String name = et_name.getText().toString();
		final String pass = et_pass.getText().toString();
		Thread t = new Thread(){
			@Override
			public void run() {
				String path = "http://10.100.39.113:8080/AndroidServer/login";
				//创建客户端对象
				HttpClient hc = new DefaultHttpClient();
				//创建post请求对象
				HttpPost hp = new HttpPost(path);
				//封装form表单提交的数据
				BasicNameValuePair bnvp  = new BasicNameValuePair("username", name);
				BasicNameValuePair bnvp2 = new BasicNameValuePair("password", pass);
				List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
				//把BasicNameValuePair放入集合中
				parameters.add(bnvp);
				parameters.add(bnvp2);
				try{
					//数据已经封装在集合中,将集合传给实体对象
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,"utf-8");
					//设置post请求对象的实体,即将提交的数据封装到post请求的输出流中
					hp.setEntity(entity);
					//使用客户端发送post请求
					HttpResponse hr = hc.execute(hp);
					if(hr.getStatusLine().getStatusCode() == 200){
						InputStream is = hr.getEntity().getContent();
						String text = Utils.getTextFromStream(is);
						//向主线程发送消息
						Message msg = new Message();
						msg.obj = text;
						handler.sendMessage(msg);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
}
