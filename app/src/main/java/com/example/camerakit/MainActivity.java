package com.example.camerakit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.listener.OnCheckedListener;
import com.zhihu.matisse.listener.OnSelectedListener;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 *  本地图片、视频选择；
 *  一个为Android精心设计的本地图像和视频选择器
 * https://github.com/zhihu/Matisse
 *
 *  图片压缩
 *
 *
 * Android中调用CemeraView实现拍照和录像
 *  compile 'com.flurgle:camerakit:0.9.17'
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_CHOOSE = 23;

    private UriAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.zhihu).setOnClickListener(this);
        findViewById(R.id.dracula).setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter = new UriAdapter());


    }

    @Override
    public void onClick(final View v) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            switch (v.getId()) {
                                case R.id.zhihu:
                                    // library
                                    Matisse.from(MainActivity.this)
                                            .choose(MimeType.ofAll())
                                            .countable(true)
                                            .capture(true)
                                            .maxSelectable(9)
                                            .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                            .thumbnailScale(0.85f)
                                            .imageEngine(new Glide4Engine())
                                            .forResult(REQUEST_CODE_CHOOSE);
                                    // module
//                                    Matisse.from(MainActivity.this)
//                                            .choose(MimeType.ofAll(), false)
//                                            .countable(true)
//                                            .capture(true)
////                                            .captureStrategy(
////                                                    new CaptureStrategy(true, "com.example.camerakit.fileprovider","test"))  //    // module
//                                            .captureStrategy(
//                                                    new CaptureStrategy(true, "com.example.camerakit.fileprovider"))
//                                            .maxSelectable(9)
//                                            .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                                            .gridExpectedSize(
//                                                    getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
//                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                                            .thumbnailScale(0.85f)
////                                            .imageEngine(new GlideEngine())  // for glide-V3
//                                            .imageEngine(new Glide4Engine())    // for glide-V4
//                                            .setOnSelectedListener(new OnSelectedListener() {
//                                                @Override
//                                                public void onSelected(
//                                                        @NonNull List<Uri> uriList, @NonNull List<String> pathList) {
//                                                    // DO SOMETHING IMMEDIATELY HERE
//                                                    Log.e("onSelected", "onSelected: pathList=" + pathList);
//
//                                                }
//                                            })
//                                            .originalEnable(true)
//                                            .maxOriginalSize(10)
////                                            .autoHideToolbarOnSingleTap(true)
//                                            .setOnCheckedListener(new OnCheckedListener() {
//                                                @Override
//                                                public void onCheck(boolean isChecked) {
//                                                    // DO SOMETHING IMMEDIATELY HERE
//                                                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);
//                                                }
//                                            })
//                                            .forResult(REQUEST_CODE_CHOOSE);
                                    break;
                                case R.id.dracula:
                                    Matisse.from(MainActivity.this)
                                            .choose(MimeType.ofImage())
                                            .theme(R.style.Matisse_Dracula)
                                            .countable(false)
                                            .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                            .maxSelectable(9)
                                            .originalEnable(true)
                                            .maxOriginalSize(10)
                                            .imageEngine(new PicassoEngine())
                                            .forResult(REQUEST_CODE_CHOOSE);
                                    break;
                                default:
                                    break;
                            }
                            mAdapter.setData(null, null);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.permission_request_denied, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {

        private List<Uri> mUris;
        private List<String> mPaths;

        void setData(List<Uri> uris, List<String> paths) {
            mUris = uris;
            mPaths = paths;
            notifyDataSetChanged();
        }

        @Override
        public UriViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UriViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.uri_item, parent, false));
        }

        @Override
        public void onBindViewHolder(UriViewHolder holder, int position) {
            holder.mUri.setText(mUris.get(position).toString());
            holder.mPath.setText(mPaths.get(position));

            holder.mUri.setAlpha(position % 2 == 0 ? 1.0f : 0.54f);
            holder.mPath.setAlpha(position % 2 == 0 ? 1.0f : 0.54f);
        }

        @Override
        public int getItemCount() {
            return mUris == null ? 0 : mUris.size();
        }

        static class UriViewHolder extends RecyclerView.ViewHolder {

            private TextView mUri;
            private TextView mPath;

            UriViewHolder(View contentView) {
                super(contentView);
                mUri = (TextView) contentView.findViewById(R.id.uri);
                mPath = (TextView) contentView.findViewById(R.id.path);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mAdapter.setData(Matisse.obtainResult(data), Matisse.obtainPathResult(data));
            Log.e("OnActivityResult ", String.valueOf(Matisse.obtainOriginalState(data)));
        }
    }
}
