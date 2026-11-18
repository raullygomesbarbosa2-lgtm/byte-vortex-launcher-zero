package com.bytevortex.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(8, 11, 9), neon = Color.rgb(57, 255, 20), text = Color.WHITE, muted = Color.rgb(153, 165, 156);
    private PackageManager pm;
    private final ArrayList<ResolveInfo> all = new ArrayList<>(), shown = new ArrayList<>();
    private GridView grid;
    private AppsAdapter adapter;
    private TextView clock, date;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM", new Locale("pt", "BR"));

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(0);
        pm = getPackageManager();
        buildScreen(); loadApps(); updateTime();
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(22), dp(26), dp(22), dp(12)); root.setBackgroundColor(bg);
        LinearLayout brand = new LinearLayout(this); brand.setGravity(Gravity.CENTER_VERTICAL); brand.setOrientation(LinearLayout.HORIZONTAL);
        TextView mark = new TextView(this); mark.setText("BV"); mark.setTextColor(bg); mark.setTextSize(18); mark.setTypeface(Typeface.DEFAULT, Typeface.BOLD); mark.setGravity(Gravity.CENTER);
        GradientDrawable badge = new GradientDrawable(); badge.setColor(neon); badge.setCornerRadius(dp(14)); mark.setBackground(badge); brand.addView(mark, new LinearLayout.LayoutParams(dp(48), dp(48)));
        LinearLayout names = new LinearLayout(this); names.setOrientation(LinearLayout.VERTICAL); names.setPadding(dp(12),0,0,0);
        TextView title = new TextView(this); title.setText("BYTE VORTEX"); title.setTextColor(text); title.setTextSize(19); title.setTypeface(Typeface.DEFAULT, Typeface.BOLD); names.addView(title);
        TextView sub = new TextView(this); sub.setText("LAUNCHER  •  GAMING MODE"); sub.setTextColor(neon); sub.setTextSize(10); sub.setLetterSpacing(.12f); names.addView(sub);
        brand.addView(names, new LinearLayout.LayoutParams(0, -2, 1));
        TextView settings = new TextView(this); settings.setText("⚙"); settings.setTextColor(neon); settings.setTextSize(24); settings.setGravity(Gravity.CENTER); settings.setOnClickListener(v -> { try { startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); } catch(Exception ignored){} }); brand.addView(settings, new LinearLayout.LayoutParams(dp(44), dp(48)));
        root.addView(brand);
        clock = new TextView(this); clock.setTextColor(text); clock.setTextSize(48); clock.setTypeface(Typeface.DEFAULT, Typeface.BOLD); clock.setPadding(0, dp(34),0,0); root.addView(clock);
        date = new TextView(this); date.setTextColor(muted); date.setTextSize(14); date.setPadding(0,0,0,dp(20)); root.addView(date);
        EditText search = new EditText(this); search.setSingleLine(true); search.setTextColor(text); search.setHintTextColor(muted); search.setHint("⌕   Buscar aplicativos"); search.setTextSize(15); search.setPadding(dp(16),0,dp(16),0); search.setBackground(round(Color.rgb(22,28,23), dp(18))); root.addView(search, new LinearLayout.LayoutParams(-1, dp(52)));
        TextView section = new TextView(this); section.setText("SEUS APLICATIVOS"); section.setTextColor(muted); section.setTextSize(11); section.setLetterSpacing(.14f); section.setPadding(dp(4), dp(22), 0, dp(10)); root.addView(section);
        grid = new GridView(this); grid.setNumColumns(4); grid.setVerticalSpacing(dp(8)); grid.setHorizontalSpacing(dp(4)); grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH); grid.setSelector(android.R.color.transparent); grid.setPadding(0,dp(4),0,0); root.addView(grid, new LinearLayout.LayoutParams(-1,0,1));
        adapter = new AppsAdapter(); grid.setAdapter(adapter);
        TextView footer = new TextView(this); footer.setText("BYTE VORTEX  •  SEU CELULAR, SEU UNIVERSO"); footer.setGravity(Gravity.CENTER); footer.setTextColor(Color.rgb(83,98,86)); footer.setTextSize(9); footer.setLetterSpacing(.08f); footer.setPadding(0,dp(8),0,dp(4)); root.addView(footer);
        search.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int before,int count){ filter(s.toString()); } public void afterTextChanged(Editable e){} });
        setContentView(root);
    }

    private void loadApps() {
        all.clear(); Intent intent = new Intent(Intent.ACTION_MAIN, null); intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo r: apps) if (!r.activityInfo.packageName.equals(getPackageName())) all.add(r);
        Collections.sort(all, (a,b) -> a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString()));
        shown.clear(); shown.addAll(all); if (adapter != null) adapter.notifyDataSetChanged();
    }
    private void filter(String q) { shown.clear(); for (ResolveInfo r: all) if (r.loadLabel(pm).toString().toLowerCase(new Locale("pt","BR")).contains(q.toLowerCase(new Locale("pt","BR")))) shown.add(r); adapter.notifyDataSetChanged(); }
    private void updateTime() { clock.setText(timeFormat.format(new Date())); date.setText(dateFormat.format(new Date()).toUpperCase(new Locale("pt","BR"))); clock.postDelayed(new Runnable(){public void run(){updateTime();}}, 30000); }
    private GradientDrawable round(int color,int radius) { GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d; }
    private int dp(float v) { return (int)(v*getResources().getDisplayMetrics().density+.5f); }

    private class AppsAdapter extends BaseAdapter {
        public int getCount(){return shown.size();} public Object getItem(int p){return shown.get(p);} public long getItemId(int p){return p;}
        public View getView(int p, View recycled, ViewGroup parent) {
            LinearLayout cell = new LinearLayout(MainActivity.this); cell.setOrientation(LinearLayout.VERTICAL); cell.setGravity(Gravity.CENTER); cell.setPadding(dp(3),dp(8),dp(3),dp(8));
            ImageView icon = new ImageView(MainActivity.this); try { icon.setImageDrawable(shown.get(p).loadIcon(pm)); } catch(Exception ignored){}; cell.addView(icon,new LinearLayout.LayoutParams(dp(48),dp(48)));
            TextView label = new TextView(MainActivity.this); label.setText(shown.get(p).loadLabel(pm)); label.setTextColor(text); label.setTextSize(11); label.setGravity(Gravity.CENTER); label.setMaxLines(2); label.setPadding(0,dp(6),0,0); cell.addView(label,new LinearLayout.LayoutParams(-1,dp(38)));
            ResolveInfo app=shown.get(p); cell.setOnClickListener(v->{Intent launch=pm.getLaunchIntentForPackage(app.activityInfo.packageName); if(launch!=null) try {startActivity(launch);}catch(Exception ignored){} });
            return cell;
        }
    }
    @Override protected void onResume(){ super.onResume(); if(pm!=null) loadApps(); }
}
