package b4a.acccompendium;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.acccompendium", "b4a.acccompendium.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.acccompendium", "b4a.acccompendium.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.acccompendium.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.phone.Phone.PhoneAccelerometer _capteuracc = null;
public static anywheresoftware.b4a.phone.Phone.PhoneSensors _capteurgyro = null;
public static float _xsave = 0f;
public static float _ysave = 0f;
public static anywheresoftware.b4a.objects.Timer _montimer = null;
public anywheresoftware.b4a.objects.LabelWrapper _x_label = null;
public anywheresoftware.b4a.objects.LabelWrapper _y_label = null;
public anywheresoftware.b4a.objects.LabelWrapper _z_label = null;
public anywheresoftware.b4a.objects.LabelWrapper _acc = null;
public anywheresoftware.b4a.objects.LabelWrapper _acc_x = null;
public anywheresoftware.b4a.objects.LabelWrapper _acc_y = null;
public anywheresoftware.b4a.objects.LabelWrapper _acc_z = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _projet = null;
public anywheresoftware.b4a.objects.ButtonWrapper _acce = null;
public anywheresoftware.b4a.objects.LabelWrapper _titre = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _compendium = null;
public anywheresoftware.b4a.objects.ButtonWrapper _retour = null;
public anywheresoftware.b4a.objects.LabelWrapper _titreprojet = null;
public anywheresoftware.b4a.objects.LabelWrapper _x_pos = null;
public anywheresoftware.b4a.objects.LabelWrapper _y_pos = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _pos_x = null;
public anywheresoftware.b4a.objects.LabelWrapper _pos_y = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _acce_click() throws Exception{
 //BA.debugLineNum = 92;BA.debugLine="Sub Acce_Click";
 //BA.debugLineNum = 93;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 94;BA.debugLine="Activity.LoadLayout(\"testacc\")";
mostCurrent._activity.LoadLayout("testacc",mostCurrent.activityBA);
 //BA.debugLineNum = 95;BA.debugLine="capteurACC.StartListening(\"inclinaison1\")";
_capteuracc.StartListening(processBA,"inclinaison1");
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 46;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 48;BA.debugLine="Activity.LoadLayout(\"Accueil\")";
mostCurrent._activity.LoadLayout("Accueil",mostCurrent.activityBA);
 //BA.debugLineNum = 49;BA.debugLine="capteurACC.StartListening(\"inclinaison\")";
_capteuracc.StartListening(processBA,"inclinaison");
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 56;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 52;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 25;BA.debugLine="Dim X_Label As Label";
mostCurrent._x_label = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim Y_Label As Label";
mostCurrent._y_label = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim Z_Label As Label";
mostCurrent._z_label = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private Acc As Label";
mostCurrent._acc = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private acc_X As Label";
mostCurrent._acc_x = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private acc_Y As Label";
mostCurrent._acc_y = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private acc_Z As Label";
mostCurrent._acc_z = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private Projet As Button";
mostCurrent._projet = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private Acce As Button";
mostCurrent._acce = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private Titre As Label";
mostCurrent._titre = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private Compendium As ImageView";
mostCurrent._compendium = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private Retour As Button";
mostCurrent._retour = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private TitreProjet As Label";
mostCurrent._titreprojet = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private X_pos As Label";
mostCurrent._x_pos = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private Y_pos As Label";
mostCurrent._y_pos = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private Panel2 As Panel";
mostCurrent._panel2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Private Pos_X As Label";
mostCurrent._pos_x = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Private Pos_Y As Label";
mostCurrent._pos_y = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
public static String  _inclinaison1_accelerometerchanged(float _x,float _y,float _z) throws Exception{
 //BA.debugLineNum = 60;BA.debugLine="Sub inclinaison1_accelerometerchanged(X As Float,Y";
 //BA.debugLineNum = 61;BA.debugLine="X_Label.Text=NumberFormat(X,1,2)";
mostCurrent._x_label.setText((Object)(anywheresoftware.b4a.keywords.Common.NumberFormat(_x,(int) (1),(int) (2))));
 //BA.debugLineNum = 62;BA.debugLine="Y_Label.Text=NumberFormat(Y,2,3)";
mostCurrent._y_label.setText((Object)(anywheresoftware.b4a.keywords.Common.NumberFormat(_y,(int) (2),(int) (3))));
 //BA.debugLineNum = 63;BA.debugLine="Z_Label.Text=NumberFormat(Z,2,0)";
mostCurrent._z_label.setText((Object)(anywheresoftware.b4a.keywords.Common.NumberFormat(_z,(int) (2),(int) (0))));
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _inclinaison2_accelerometerchanged(float _x,float _y,float _z) throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub inclinaison2_accelerometerchanged(X As Float,Y";
 //BA.debugLineNum = 67;BA.debugLine="xsave=X";
_xsave = _x;
 //BA.debugLineNum = 68;BA.debugLine="ysave=Y";
_ysave = _y;
 //BA.debugLineNum = 69;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim capteurACC As PhoneAccelerometer";
_capteuracc = new anywheresoftware.b4a.phone.Phone.PhoneAccelerometer();
 //BA.debugLineNum = 16;BA.debugLine="Dim capteurGYRO As PhoneSensors";
_capteurgyro = new anywheresoftware.b4a.phone.Phone.PhoneSensors();
 //BA.debugLineNum = 17;BA.debugLine="Dim xsave As Float";
_xsave = 0f;
 //BA.debugLineNum = 18;BA.debugLine="Dim ysave As Float";
_ysave = 0f;
 //BA.debugLineNum = 19;BA.debugLine="Dim Montimer As Timer";
_montimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _projet_click() throws Exception{
 //BA.debugLineNum = 84;BA.debugLine="Sub Projet_Click";
 //BA.debugLineNum = 85;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 86;BA.debugLine="Activity.LoadLayout(\"projetAcc\")";
mostCurrent._activity.LoadLayout("projetAcc",mostCurrent.activityBA);
 //BA.debugLineNum = 87;BA.debugLine="capteurACC.StartListening(\"inclinaison2\")";
_capteuracc.StartListening(processBA,"inclinaison2");
 //BA.debugLineNum = 88;BA.debugLine="Montimer.Initialize(\"top\",10)";
_montimer.Initialize(processBA,"top",(long) (10));
 //BA.debugLineNum = 89;BA.debugLine="Montimer.Enabled=True";
_montimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 90;BA.debugLine="End Sub";
return "";
}
public static String  _retour_click() throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Sub Retour_Click";
 //BA.debugLineNum = 99;BA.debugLine="Activity.RemoveAllViews";
mostCurrent._activity.RemoveAllViews();
 //BA.debugLineNum = 100;BA.debugLine="Activity.LoadLayout(\"Accueil\")";
mostCurrent._activity.LoadLayout("Accueil",mostCurrent.activityBA);
 //BA.debugLineNum = 101;BA.debugLine="End Sub";
return "";
}
public static String  _top_tick() throws Exception{
 //BA.debugLineNum = 71;BA.debugLine="Sub top_tick";
 //BA.debugLineNum = 72;BA.debugLine="If Compendium.Left=10 Then Compendium.Left=Compen";
if (mostCurrent._compendium.getLeft()==10) { 
mostCurrent._compendium.setLeft((int) (mostCurrent._compendium.getLeft()+1));};
 //BA.debugLineNum = 73;BA.debugLine="If Compendium.Left=600 Then Compendium.Left=Compe";
if (mostCurrent._compendium.getLeft()==600) { 
mostCurrent._compendium.setLeft((int) (mostCurrent._compendium.getLeft()-1));};
 //BA.debugLineNum = 74;BA.debugLine="If xsave>2 Then Compendium.Left=Compendium.Left-1";
if (_xsave>2) { 
mostCurrent._compendium.setLeft((int) (mostCurrent._compendium.getLeft()-1));};
 //BA.debugLineNum = 75;BA.debugLine="If xsave<-2 Then Compendium.Left=Compendium.Left+";
if (_xsave<-2) { 
mostCurrent._compendium.setLeft((int) (mostCurrent._compendium.getLeft()+1));};
 //BA.debugLineNum = 76;BA.debugLine="X_pos.Text=Compendium.Left";
mostCurrent._x_pos.setText((Object)(mostCurrent._compendium.getLeft()));
 //BA.debugLineNum = 77;BA.debugLine="If Compendium.Top=0 Then Compendium.Top=Compendiu";
if (mostCurrent._compendium.getTop()==0) { 
mostCurrent._compendium.setTop((int) (mostCurrent._compendium.getTop()+1));};
 //BA.debugLineNum = 78;BA.debugLine="If Compendium.Top=600 Then Compendium.Top=Compend";
if (mostCurrent._compendium.getTop()==600) { 
mostCurrent._compendium.setTop((int) (mostCurrent._compendium.getTop()-1));};
 //BA.debugLineNum = 79;BA.debugLine="If ysave>2 Then Compendium.Top=Compendium.Top-1";
if (_ysave>2) { 
mostCurrent._compendium.setTop((int) (mostCurrent._compendium.getTop()-1));};
 //BA.debugLineNum = 80;BA.debugLine="If ysave<-2 Then Compendium.Top=Compendium.Top+1";
if (_ysave<-2) { 
mostCurrent._compendium.setTop((int) (mostCurrent._compendium.getTop()+1));};
 //BA.debugLineNum = 81;BA.debugLine="Y_pos.Text=Compendium.Top";
mostCurrent._y_pos.setText((Object)(mostCurrent._compendium.getTop()));
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
}
