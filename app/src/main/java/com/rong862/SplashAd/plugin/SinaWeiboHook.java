package com.rong862.SplashAd.plugin;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabHost;

import com.rong862.SplashAd.Hook;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static com.rong862.utils.ClassFilter.findClassIfExists;
import static com.rong862.utils.LogUtil.debug;
import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;
import static com.rong862.utils.XposedUtil.CL;
import static com.rong862.utils.XposedUtil.getIdByName;

public class SinaWeiboHook extends BaseHook{

    private static final String TAG = "【新浪微博】";

    public SinaWeiboHook(){}

    @Override
    public void startHook() {

        log(TAG,"新浪微博启动...");

        //去除开屏广告
        HookByMatchName(TAG,
                "com.sina.weibo.SplashActivity",
                null,"onCreate", android.os.Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Activity AdActivity = (Activity)param.thisObject;
                        Intent intent = new Intent(AdActivity, findClassIfExists("com.sina.weibo.MainTabActivity", CL));
                        AdActivity.startActivity(intent);
                        debug(TAG,"SplashActivity finish...");
                        AdActivity.finish();
                    }
        });


        HookByMatchName(TAG,
                TabHost.class,
                null,"addTab", TabHost.TabSpec.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        TabHost.TabSpec mTabSpec = (TabHost.TabSpec)param.args[0];
                        if(mTabSpec.getTag() == null)
                            return;

                        if(mTabSpec.getTag().equals("search_tab")){

                            TabHost mTabHost = (TabHost)param.thisObject;

                            ViewGroup tabViewGroup = mTabHost.findViewById(getIdByName("main_radio"));
                            //0:关注 1：超话 2：广场 3：消息 4：我
                            FrameLayout tabView = (FrameLayout)tabViewGroup.getChildAt(1);
                            tabView.setVisibility(View.GONE);
                        }
                    }
        });


        HookByMatchName(TAG,
                "com.sina.weibo.streamservice.adapter.RecyclerViewAdapter",
                null,"setData", List.class, boolean.class,
                new Hook() {
                    @Override
                    protected void before(MethodHookParam param){

                        List<Object> dataList = (List<Object>)param.args[0];
                        List<Object> newList = new ArrayList<>();

                        for(Object item : dataList){

                            int type = (int)XposedHelpers.callMethod(item, "getItemTypeForAdapter");

                            boolean removeType = false;

                            for(int mtype : cardType.noDisplay){
                                if(mtype == type){
                                    removeType = true;
                                    break;
                                }
                            }

                            if(!removeType){
                                if(type == cardType.blogCard){
                                    Object status = XposedHelpers.callMethod(item,"getData");
                                    int getMblogType = (int)XposedHelpers.callMethod(status, "getMblogType");
                                    if(getMblogType != 1)newList.add(item);
                                }else newList.add(item);
                            }
                        }
                        param.args[0] = newList;
                    }
        });
    }

    private interface cardType{
        //微博正文卡片
        int blogCard = 10030;
        //其它微博卡片
        int otherCard = 30;
        //关注：精选推荐横向卡片
        int horToMeCard = 22;
        //关注：顶部空白
        int topBlank = 10122;
        //关注：顶部提示语
        int topHint = 10160;
        //推荐：默认位置信息
        int defLocation = 10142;
        //推荐：tab标签--最新 最热 附近
        int recomTab = 10155;
        //搜索框
        int searchCard = 159;
        //看帖：活动卡片
        int hdCard = 343;
        int chCard = 308;
        int titleCard = 229;
        int bigCard = 150;
        int meBigCard = 39;
        int meBigCard1 = 38;
        int meTjCad = 43;
        int meTjTitle = 7;
        int adCard = 256;
        int hotLiao = 346;
        int hotliaoTab = 157;
        int shiKuang = 320;

        int[] noDisplay = {horToMeCard, topBlank, topHint, defLocation,
                recomTab, searchCard, hdCard, chCard,
                titleCard, bigCard, meBigCard, meBigCard1,
                meTjCad, adCard, hotliaoTab, hotLiao, shiKuang,
                meTjTitle, 36, };
    }
}
