package com.nimo.fb_effect.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBMakeupEnum;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.adapter.MakeUpItemViewBinder;
import com.nimo.fb_effect.base.FBBaseLazyFragment;
import com.nimo.fb_effect.model.BlushConfig;
import com.nimo.fb_effect.model.EyebrowConfig;
import com.nimo.fb_effect.model.EyelashConfig;
import com.nimo.fb_effect.model.EyelineConfig;
import com.nimo.fb_effect.model.EyeshadowConfig;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.LipstickConfig;
import com.nimo.fb_effect.model.Makeup;
import com.nimo.fb_effect.model.PupilsConfig;
import com.nimo.fb_effect.utils.DpUtils;
import com.nimo.fb_effect.utils.FBConfigCallBack;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.fb_effect.utils.MyItemDecoration;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * 美妆子类型列表
 */
public class MakeUpItemFragment extends FBBaseLazyFragment {
    private OnMakeUpItemBackListener backListener;
    private View container;

    private RecyclerView rvMakeup;
    private AppCompatTextView tvTitle;
    private AppCompatImageView ivReturn;
    private RelativeLayout rlTitle;
    private View line;
    private TextView tvCenter;
    private ImageView[] lipstickImageViews, eyebrowImageViews, blushImageViews;
    private ImageView[] lipstickSelImageViews, eyebrowSelImageViews, blushSelImageViews;
    private Toast currentToast;

    private final List<Makeup> items = new ArrayList<>();

    private int makeType;

    @Override protected int getLayoutId() {
        return R.layout.fragment_lipstick;
    }

    private final MultiTypeAdapter listAdapter = new MultiTypeAdapter();

    private void showCustomToast(String message) {
        // 如果当前有显示的 Toast，取消它
        if (currentToast != null) {
            currentToast.cancel();
        }

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
            findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        currentToast = new Toast(getApplicationContext());
        currentToast.setGravity(Gravity.CENTER, 0, 0);
        currentToast.setDuration(Toast.LENGTH_SHORT);
        currentToast.setView(layout);
        currentToast.show();

        // 设置1秒后自动消失
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentToast != null) {
                    currentToast.cancel();
                    currentToast = null;
                }
            }
        }, 1000);
    }

    @Override protected void initView(View view, Bundle savedInstanceState) {

        lipstickImageViews = new ImageView[] {
            findViewById(R.id.iv_lip_softpink),
            findViewById(R.id.iv_lip_cameobrown),
            findViewById(R.id.iv_lip_yuanqiorange),
            findViewById(R.id.iv_lip_red),
            findViewById(R.id.iv_lip_crimson),
            findViewById(R.id.iv_lip_caramel)
        };

        lipstickSelImageViews = new ImageView[] {
            findViewById(R.id.iv_sel_softpink),
            findViewById(R.id.iv_sel_camebrown),
            findViewById(R.id.iv_sel_yuanqiorange),
            findViewById(R.id.iv_sel_red),
            findViewById(R.id.iv_sel_crimson),
            findViewById(R.id.iv_sel_caramel)
        };

        eyebrowImageViews = new ImageView[] {
            findViewById(R.id.iv_eyebrow_softpinkbrown),
            findViewById(R.id.iv_eyebrow_gentleblack),
            findViewById(R.id.iv_eyebrow_softbrown),
            findViewById(R.id.iv_eyebrow_darkbrown)
        };

        eyebrowSelImageViews = new ImageView[] {
            findViewById(R.id.iv_sel_softpinkbrown),
            findViewById(R.id.iv_sel_gentleblack),
            findViewById(R.id.iv_sel_softbrown),
            findViewById(R.id.iv_sel_darkbrown)
        };
        blushImageViews = new ImageView[] {
            findViewById(R.id.iv_blush_richang),
            findViewById(R.id.iv_blush_yuanqi),
            findViewById(R.id.iv_blush_jianling),
            findViewById(R.id.iv_blush_fenwei),
            findViewById(R.id.iv_blush_rixi),
            findViewById(R.id.iv_blush_mitao)
        };

        blushSelImageViews = new ImageView[] {
            findViewById(R.id.iv_sel_richang),
            findViewById(R.id.iv_sel_yuanqi),
            findViewById(R.id.iv_sel_jianling),
            findViewById(R.id.iv_sel_fenwei),
            findViewById(R.id.iv_sel_rixi),
            findViewById(R.id.iv_sel_mitao)
        };

        String type = "";
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            type = bundle.getString("switch");
        }
        tvTitle = findViewById(R.id.tv_title);
        switch (type) {
            case "lipstick":
                FBState.currentThirdViewState = FBViewState.MAKEUP_LIPSTICK;
                makeType = 0;
                tvTitle.setText("口红");
                showMakeupViews(lipstickImageViews);
                int lipstickPosition = FBUICacheUtils.getMakeupItemColorPositionCache(makeType);
                showSelectedColor(lipstickSelImageViews, lipstickPosition);
                FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupLipstick.getValue(), "color", FBUICacheUtils.getMakeupItemColorCache(makeType));
                break;
            case "eyebrow":
                FBState.currentThirdViewState = FBViewState.MAKEUP_EYEBROW;
                makeType = 1;
                tvTitle.setText("眉毛");
                showMakeupViews(eyebrowImageViews);
                int eyebrowPosition = FBUICacheUtils.getMakeupItemColorPositionCache(makeType);
                showSelectedColor(eyebrowSelImageViews, eyebrowPosition);
                FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupEyebrow.getValue(), "color", FBUICacheUtils.getMakeupItemColorCache(makeType));
                break;
            case "blush":
                FBState.currentThirdViewState = FBViewState.MAKEUP_BLUSH;
                makeType = 2;
                tvTitle.setText("腮红");
                showMakeupViews(blushImageViews);
                int blushPosition = FBUICacheUtils.getMakeupItemColorPositionCache(makeType);
                showSelectedColor(blushSelImageViews, blushPosition);
                FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupBlush.getValue(), "color", FBUICacheUtils.getMakeupItemColorCache(makeType));
                break;
            case "eyeshadow":
                makeType = 3;
                tvTitle.setText("眼影");
                FBState.currentThirdViewState = FBViewState.MAKEUP_EYESHADOW;
                break;
            case "eyeline":
                makeType = 4;
                tvTitle.setText("眼线");
                FBState.currentThirdViewState = FBViewState.MAKEUP_EYELINE;
                break;
            case "eyelash":
                makeType = 5;
                tvTitle.setText("睫毛");
                FBState.currentThirdViewState = FBViewState.MAKEUP_EYELASH;
                break;
            case "pupils":
                makeType = 6;
                tvTitle.setText("美瞳");
                FBState.currentThirdViewState = FBViewState.MAKEUP_BEAUTYPUPILS;
                break;
            default:
                makeType = -1;
        }

        container = findViewById(R.id.container);
        rvMakeup = findViewById(R.id.rv_filter);
        rlTitle = findViewById(R.id.rl_title);

        line = findViewById(R.id.line);
        ivReturn = findViewById(R.id.return_iv);
        // ivReturn.setVisibility(View.VISIBLE);

        tvTitle.setVisibility(View.VISIBLE);
        rlTitle.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);

        rvMakeup.addItemDecoration(new
                MyItemDecoration(5)
        );
        setClickListeners(lipstickImageViews, lipstickSelImageViews);
        setClickListeners(eyebrowImageViews, eyebrowSelImageViews);
        setClickListeners(blushImageViews, blushSelImageViews);
        ivReturn.setOnClickListener(v -> {
            if (backListener != null) {
                backListener.onMakeUpItemBack();
            }
            requireParentFragment()
                    .getChildFragmentManager()
                    .popBackStack();
           FBState.currentViewState = FBViewState.HIDE;
        });
        changeTheme(null);
        items.clear();
        Makeup NoMakeup = Makeup.NO_MAKEUP;
        NoMakeup.setIdCard(makeType);
        items.add(NoMakeup);

        switch (makeType) {
            case 0:
                LipstickConfig lipstickList = FBConfigTools.getInstance().getLipstickList();
                if (lipstickList == null) {
                    FBConfigTools.getInstance().getLipsticksConfig(new FBConfigCallBack<List<Makeup>>() {
                        @Override public void success(List<Makeup> list) {
                            items.addAll(list);
                            initRecyclerView();
                        }

                        @Override public void fail(Exception error) {
                            error.printStackTrace();
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    items.addAll(lipstickList.getLipsticks());
                    initRecyclerView();
                }
                break;
            case 1:

                EyebrowConfig eyebrowList = FBConfigTools.getInstance().getEyebrowList();
                if (eyebrowList == null) {
                    FBConfigTools.getInstance().getEyebrowsConfig(new FBConfigCallBack<List<Makeup>>() {
                        @Override public void success(List<Makeup> list) {
                            items.addAll(list);
                            initRecyclerView();
                        }

                        @Override public void fail(Exception error) {
                            error.printStackTrace();
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    items.addAll(eyebrowList.getEyebrows());
                    initRecyclerView();
                }
                break;
            case 2:
                BlushConfig blushList = FBConfigTools.getInstance().getBlushList();
                if (blushList == null) {
                    FBConfigTools.getInstance().getBlushsConfig(new FBConfigCallBack<List<Makeup>>() {
                        @Override public void success(List<Makeup> list) {
                            items.addAll(list);
                            initRecyclerView();
                        }

                        @Override public void fail(Exception error) {
                            error.printStackTrace();
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    items.addAll(blushList.getBlushes());
                    initRecyclerView();
                }
                break;
            case 3:
                EyeshadowConfig shadowList = FBConfigTools.getInstance().getEyeshadowList();
                if (shadowList == null) {
                    FBConfigTools.getInstance().getEyeshadowsConfig(new FBConfigCallBack<List<Makeup>>() {
                        @Override public void success(List<Makeup> list) {
                            items.addAll(list);
                            initRecyclerView();
                        }

                        @Override public void fail(Exception error) {
                            error.printStackTrace();
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    items.addAll(shadowList.getEyeshadows());
                    initRecyclerView();
                }
                break;
            case 4:
                EyelineConfig eyelineList = FBConfigTools.getInstance().getEyelineList();
                if (eyelineList == null) {
                    FBConfigTools.getInstance().getEyelinesConfig(new FBConfigCallBack<List<Makeup>>() {
                        @Override public void success(List<Makeup> list) {
                            items.addAll(list);
                            initRecyclerView();
                        }

                        @Override public void fail(Exception error) {
                            error.printStackTrace();
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    items.addAll(eyelineList.getEyeliners());
                    initRecyclerView();
                }
                break;
            case 5:
                EyelashConfig eyelashList = FBConfigTools.getInstance().getEyelashList();
                if (eyelashList == null) {
                    FBConfigTools.getInstance().getEyelashsConfig(new FBConfigCallBack<List<Makeup>>() {
                        @Override public void success(List<Makeup> list) {
                            items.addAll(list);
                            initRecyclerView();
                        }

                        @Override public void fail(Exception error) {
                            error.printStackTrace();
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    items.addAll(eyelashList.getEyelashes());
                    initRecyclerView();
                }
                break;
            case 6:
                PupilsConfig pupilsList = FBConfigTools.getInstance().getPupilsList();
                if (pupilsList == null) {
                    FBConfigTools.getInstance().getPupilsConfig(new FBConfigCallBack<List<Makeup>>() {
                        @Override public void success(List<Makeup> list) {
                            items.addAll(list);
                            initRecyclerView();
                        }

                        @Override public void fail(Exception error) {
                            error.printStackTrace();
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    items.addAll(pupilsList.getPupils());
                    initRecyclerView();
                }
                break;

        }

    }

    private void showMakeupViews(ImageView[] views) {
        for (ImageView view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void showSelectedColor(ImageView[] views, int position) {
        for (int i = 0; i < views.length; i++) {
            if (i == position) {
                views[i].setVisibility(View.VISIBLE);
            } else {
                views[i].setVisibility(View.GONE);
            }
        }
    }

    private void setClickListeners(ImageView[] makeupViews, ImageView[] selViews) {
        for (int i = 0; i < makeupViews.length; i++) {
            final int position = i;
            makeupViews[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelectedColor(selViews, position);
                    cacheAndSetMakeupColor(position);
                }
            });
        }
    }

    private void cacheAndSetMakeupColor(int position) {
        switch (makeType) {
            case 0: // lipstick
                FBUICacheUtils.setMakeupItemColorPositionCache(makeType, position);
                FBUICacheUtils.setMakeupItemColorCache(makeType, getLipstickColorCode(position));
                showCustomToast(getLipstickColorName(position));
                FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupLipstick.getValue(), "color", getLipstickColorCode(position));
                break;
            case 1: // eyebrow
                FBUICacheUtils.setMakeupItemColorPositionCache(makeType, position);
                FBUICacheUtils.setMakeupItemColorCache(makeType, getEyebrowColorCode(position));
                showCustomToast(getEyebrowColorName(position));
                FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupEyebrow.getValue(), "color", getEyebrowColorCode(position));
                break;
            case 2: // blush
                FBUICacheUtils.setMakeupItemColorPositionCache(makeType, position);
                FBUICacheUtils.setMakeupItemColorCache(makeType, getBlushColorCode(position));
                showCustomToast(getBlushColorName(position));
                FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupBlush.getValue(), "color", getBlushColorCode(position));
                break;
            // 省略其他case...
            default:
                break;
        }
    }

    private String getLipstickColorName(int position) {
        switch (position) {
            case 0:
                return getString(R.string.lip_softpink);
            case 1:
                return getString(R.string.lip_cameobrown);
            case 2:
                return getString(R.string.lip_yuanqiorange);
            case 3:
                return getString(R.string.lip_red);
            case 4:
                return getString(R.string.lip_crimson);
            case 5:
                return getString(R.string.lip_caramel);
            default:
                return "";
        }
    }

    private String getEyebrowColorName(int position) {
        switch (position) {
            case 0:
                return getString(R.string.eyebrow_softpinkbrown);
            case 1:
                return getString(R.string.eyebrow_gentleblack);
            case 2:
                return getString(R.string.eyebrow_softbrown);
            case 3:
                return getString(R.string.eyebrow_darkbrown);
            default:
                return "";
        }
    }

    private String getBlushColorName(int position) {
        switch (position) {
            case 0:
                return getString(R.string.blush_richang);
            case 1:
                return getString(R.string.blush_yuanqi);
            case 2:
                return getString(R.string.blush_jianling);
            case 3:
                return getString(R.string.blush_fenwei);
            case 4:
                return getString(R.string.blush_rixi);
            case 5:
                return getString(R.string.blush_mitao);
            default:
                return "";
        }
    }

    public String getLipstickColorCode(int position) {
        switch (position) {
            case 0:
                return "rouhefen";
            case 1:
                return "dousha";
            case 2:
                return "yuanqiju";
            case 3:
                return "zhenghong";
            case 4:
                return "fuguhong";
            case 5:
                return "jiaotang";
            default:
                return "rouhefen";
        }
    }

    private String getEyebrowColorCode(int position) {
        switch (position) {
            case 0:
                return "roufenzong";
            case 1:
                return "wenrouhei";
            case 2:
                return "rouwuzong";
            case 3:
                return "shenzong";
            default:
                return "roufenzong";
        }
    }

    private String getBlushColorCode(int position) {
        switch (position) {
            case 0:
                return "richang";
            case 1:
                return "yuanqi";
            case 2:
                return "jianling";
            case 3:
                return "fenwei";
            case 4:
                return "rixi";
            case 5:
                return "mitao";
            default:
                return "richang";
        }
    }

    public void initRecyclerView() {
        listAdapter.register(Makeup.class, new MakeUpItemViewBinder());
        listAdapter.setItems(items);
        rvMakeup.setAdapter(listAdapter);
        if (FBUICacheUtils.getMakeupItemPositionCache(makeType) >= 0) {
            rvMakeup.smoothScrollToPosition(FBUICacheUtils.getMakeupItemPositionCache(makeType));
        }
        //sync progress
        RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");
    }

    /**
     * 更换主题
     */
    @Subscribe(thread = EventThread.MAIN_THREAD,
               tags = { @Tag(FBEventAction.ACTION_CHANGE_THEME) })
    public void changeTheme(@Nullable Object o) {
        tvTitle.setTextColor(
                ContextCompat.getColorStateList(getContext(),
                        R.color.color_selector_tab_dark));
        line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_line));
    }

    @Override protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        //更新ui状态
        // FBState.currentThirdViewState = FBViewState.MAKEUP_LIPSTICK;
        //同步滑动条
        RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");
        listAdapter.notifyDataSetChanged();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Fragment parent = getParentFragment();
        if (parent instanceof OnMakeUpItemBackListener) {
            backListener = (OnMakeUpItemBackListener) parent;
        }
    }

    public interface OnMakeUpItemBackListener {
        void onMakeUpItemBack();
    }

}
