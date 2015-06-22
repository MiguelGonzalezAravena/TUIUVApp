package b4a.example;


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

public class horario extends Activity implements B4AActivity{
	public static horario mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.horario");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (horario).");
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
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.horario");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.horario", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (horario) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (horario) Resume **");
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
		return horario.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
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
        BA.LogInfo("** Activity (horario) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (horario) Resume **");
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

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.http.HttpClientWrapper _hc = null;
public static anywheresoftware.b4a.objects.SocketWrapper.ServerSocketWrapper _x = null;
public static int _i = 0;
public static String _nombre = "";
public static String _apellido = "";
public static String _run = "";
public static String _passwd = "";
public static String _id = "";
public static String _rol = "";
public static String _error = "";
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button4 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button5 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button6 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button7 = null;
public anywheresoftware.b4a.objects.collections.JSONParser _json = null;
public static String[] _weekdaysstr = null;
public static String _diahoy = "";
public b4a.example.main _main = null;
public b4a.example.menu _menu = null;
public b4a.example.inicio _inicio = null;
public b4a.example.login _login = null;
public b4a.example.lunes _lunes = null;
public b4a.example.martes _martes = null;
public b4a.example.miercoles _miercoles = null;
public b4a.example.jueves _jueves = null;
public b4a.example.viernes _viernes = null;
public b4a.example.sabado _sabado = null;
public b4a.example.calendario _calendario = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 88;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 91;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 92;BA.debugLine="hc.Initialize(\"hc\")";
_hc.Initialize("hc");
 };
 //BA.debugLineNum = 94;BA.debugLine="Activity.LoadLayout(\"Layout_Horario\")";
mostCurrent._activity.LoadLayout("Layout_Horario",mostCurrent.activityBA);
 //BA.debugLineNum = 95;BA.debugLine="Button1.Color = Colors.RGB(0, 82, 155)";
mostCurrent._button1.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (82),(int) (155)));
 //BA.debugLineNum = 96;BA.debugLine="Button2.Color = Colors.RGB(0, 82, 155)";
mostCurrent._button2.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (82),(int) (155)));
 //BA.debugLineNum = 97;BA.debugLine="Button3.Color = Colors.RGB(0, 82, 155)";
mostCurrent._button3.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (82),(int) (155)));
 //BA.debugLineNum = 98;BA.debugLine="Button4.Color = Colors.RGB(0, 82, 155)";
mostCurrent._button4.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (82),(int) (155)));
 //BA.debugLineNum = 99;BA.debugLine="Button5.Color = Colors.RGB(0, 82, 155)";
mostCurrent._button5.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (82),(int) (155)));
 //BA.debugLineNum = 100;BA.debugLine="Button6.Color = Colors.RGB(0, 82, 155)";
mostCurrent._button6.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (82),(int) (155)));
 //BA.debugLineNum = 101;BA.debugLine="Button7.Color = Colors.RGB(253, 183, 39)";
mostCurrent._button7.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (253),(int) (183),(int) (39)));
 //BA.debugLineNum = 102;BA.debugLine="Select Case DiaHoy";
switch (BA.switchObjectToInt(mostCurrent._diahoy,"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo")) {
case 0:
 //BA.debugLineNum = 104;BA.debugLine="Button1.Color = Colors.RGB(253, 183, 39)";
mostCurrent._button1.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (253),(int) (183),(int) (39)));
 break;
case 1:
 //BA.debugLineNum = 106;BA.debugLine="Button2.Color = Colors.RGB(253, 183, 39)";
mostCurrent._button2.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (253),(int) (183),(int) (39)));
 break;
case 2:
 //BA.debugLineNum = 108;BA.debugLine="Button3.Color = Colors.RGB(253, 183, 39)";
mostCurrent._button3.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (253),(int) (183),(int) (39)));
 break;
case 3:
 //BA.debugLineNum = 110;BA.debugLine="Button4.Color = Colors.RGB(253, 183, 39)";
mostCurrent._button4.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (253),(int) (183),(int) (39)));
 break;
case 4:
 //BA.debugLineNum = 112;BA.debugLine="Button5.Color = Colors.RGB(253, 183, 39)";
mostCurrent._button5.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (253),(int) (183),(int) (39)));
 break;
case 5:
 //BA.debugLineNum = 114;BA.debugLine="Button6.Color = Colors.RGB(253, 183, 39)";
mostCurrent._button6.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (253),(int) (183),(int) (39)));
 break;
case 6:
 //BA.debugLineNum = 116;BA.debugLine="Button7.Color = Colors.RGB(253, 183, 39)";
mostCurrent._button7.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (253),(int) (183),(int) (39)));
 break;
}
;
 //BA.debugLineNum = 121;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 130;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 132;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 123;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 124;BA.debugLine="If Not(Connected) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_connected())) { 
 //BA.debugLineNum = 125;BA.debugLine="i = Msgbox2(\"Para poder utilizar TUI UV App nece";
_i = anywheresoftware.b4a.keywords.Common.Msgbox2("Para poder utilizar TUI UV App necesita tener una conexión a Internet.","¡Atención!","OK","","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 126;BA.debugLine="If DialogResponse.POSITIVE Then ExitApplication";
if (BA.ObjectToBoolean(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE)) { 
anywheresoftware.b4a.keywords.Common.ExitApplication();};
 };
 //BA.debugLineNum = 128;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 39;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 40;BA.debugLine="StartActivity(Lunes)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._lunes.getObject()));
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 44;BA.debugLine="StartActivity(Martes)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._martes.getObject()));
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _button3_click() throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub Button3_Click";
 //BA.debugLineNum = 48;BA.debugLine="StartActivity(Miercoles)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._miercoles.getObject()));
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _button4_click() throws Exception{
 //BA.debugLineNum = 51;BA.debugLine="Sub Button4_Click";
 //BA.debugLineNum = 52;BA.debugLine="StartActivity(Jueves)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._jueves.getObject()));
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _button5_click() throws Exception{
 //BA.debugLineNum = 55;BA.debugLine="Sub Button5_Click";
 //BA.debugLineNum = 56;BA.debugLine="StartActivity(Viernes)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._viernes.getObject()));
 //BA.debugLineNum = 57;BA.debugLine="End Sub";
return "";
}
public static String  _button6_click() throws Exception{
 //BA.debugLineNum = 59;BA.debugLine="Sub Button6_Click";
 //BA.debugLineNum = 60;BA.debugLine="StartActivity(Sabado)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._sabado.getObject()));
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}
public static String  _button7_click() throws Exception{
 //BA.debugLineNum = 63;BA.debugLine="Sub Button7_Click";
 //BA.debugLineNum = 64;BA.debugLine="MsgBox(\"Falta implementar esta función\", \"¡Atenci";
anywheresoftware.b4a.keywords.Common.Msgbox("Falta implementar esta función","¡Atención!",mostCurrent.activityBA);
 //BA.debugLineNum = 65;BA.debugLine="End Sub";
return "";
}
public static boolean  _connected() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Connected As Boolean";
 //BA.debugLineNum = 21;BA.debugLine="x.Initialize(0,\"\")";
_x.Initialize(processBA,(int) (0),"");
 //BA.debugLineNum = 22;BA.debugLine="If x.GetMyIP = \"127.0.0.1\" Then";
if ((_x.GetMyIP()).equals("127.0.0.1")) { 
 //BA.debugLineNum = 23;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 25;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return false;
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 32;BA.debugLine="Dim Button1, Button2, Button3, Button4, Button5,";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._button3 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._button4 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._button5 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._button6 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._button7 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim JSON As JSONParser";
mostCurrent._json = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 34;BA.debugLine="Dim WeekDaysStr(), DiaHoy As String";
mostCurrent._weekdaysstr = new String[(int) (0)];
java.util.Arrays.fill(mostCurrent._weekdaysstr,"");
mostCurrent._diahoy = "";
 //BA.debugLineNum = 35;BA.debugLine="WeekDaysStr = Array As String(\"Domingo\", \"Lunes\",";
mostCurrent._weekdaysstr = new String[]{"Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado"};
 //BA.debugLineNum = 36;BA.debugLine="DiaHoy = WeekDaysStr(DateTime.GetDayOfWeek(DateTi";
mostCurrent._diahoy = mostCurrent._weekdaysstr[(int) (anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfWeek(anywheresoftware.b4a.keywords.Common.DateTime.getNow())-1)];
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _hc_responseerror(String _reason,int _statuscode,int _taskid) throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub hc_ResponseError(Reason As String, StatusCode";
 //BA.debugLineNum = 67;BA.debugLine="Msgbox(Reason, \"¡Atención!\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_reason,"¡Atención!",mostCurrent.activityBA);
 //BA.debugLineNum = 68;BA.debugLine="End Sub";
return "";
}
public static String  _hc_responsesuccess(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,int _taskid) throws Exception{
String _res = "";
String _bloque = "";
String _codigo = "";
String _sala = "";
anywheresoftware.b4a.objects.collections.List _map1 = null;
anywheresoftware.b4a.objects.collections.Map _map2 = null;
 //BA.debugLineNum = 70;BA.debugLine="Sub hc_ResponseSuccess(Response As HttpResponse, T";
 //BA.debugLineNum = 71;BA.debugLine="Dim res As String";
_res = "";
 //BA.debugLineNum = 72;BA.debugLine="Dim bloque, codigo, sala As String";
_bloque = "";
_codigo = "";
_sala = "";
 //BA.debugLineNum = 73;BA.debugLine="Dim Map1 As List";
_map1 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 74;BA.debugLine="Dim Map2 As Map";
_map2 = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 75;BA.debugLine="res = Response.GetString(\"UTF8\")";
_res = _response.GetString("UTF8");
 //BA.debugLineNum = 76;BA.debugLine="JSON.Initialize(res)";
mostCurrent._json.Initialize(_res);
 //BA.debugLineNum = 77;BA.debugLine="Map1 = JSON.NextArray";
_map1 = mostCurrent._json.NextArray();
 //BA.debugLineNum = 78;BA.debugLine="For i = 0 To Map1.Size-1";
{
final int step57 = 1;
final int limit57 = (int) (_map1.getSize()-1);
for (_i = (int) (0); (step57 > 0 && _i <= limit57) || (step57 < 0 && _i >= limit57); _i = ((int)(0 + _i + step57))) {
 //BA.debugLineNum = 79;BA.debugLine="Map2 = Map1.Get(i)";
_map2.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_map1.Get(_i)));
 //BA.debugLineNum = 80;BA.debugLine="bloque = Map2.Get(\"bloque\")";
_bloque = BA.ObjectToString(_map2.Get((Object)("bloque")));
 //BA.debugLineNum = 81;BA.debugLine="codigo = Map2.Get(\"codigo\")";
_codigo = BA.ObjectToString(_map2.Get((Object)("codigo")));
 //BA.debugLineNum = 82;BA.debugLine="sala = Map2.Get(\"sala\")";
_sala = BA.ObjectToString(_map2.Get((Object)("sala")));
 //BA.debugLineNum = 83;BA.debugLine="Msgbox(\"Hola \" & bloque & \" \" & codigo & \" \" &";
anywheresoftware.b4a.keywords.Common.Msgbox("Hola "+_bloque+" "+_codigo+" "+_sala,"¡Atención!",mostCurrent.activityBA);
 }
};
 //BA.debugLineNum = 86;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim hc As HttpClient";
_hc = new anywheresoftware.b4a.http.HttpClientWrapper();
 //BA.debugLineNum = 10;BA.debugLine="Dim x As ServerSocket";
_x = new anywheresoftware.b4a.objects.SocketWrapper.ServerSocketWrapper();
 //BA.debugLineNum = 11;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 12;BA.debugLine="Dim nombre, apellido, run, passwd, id, rol, error";
_nombre = "";
_apellido = "";
_run = "";
_passwd = "";
_id = "";
_rol = "";
_error = "";
 //BA.debugLineNum = 14;BA.debugLine="If Not(Connected) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_connected())) { 
 //BA.debugLineNum = 15;BA.debugLine="i = Msgbox2(\"Para poder utilizar TUI UV App nece";
_i = anywheresoftware.b4a.keywords.Common.Msgbox2("Para poder utilizar TUI UV App necesita tener una conexión a Internet.","¡Atención!","OK","","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 16;BA.debugLine="If DialogResponse.POSITIVE Then ExitApplication";
if (BA.ObjectToBoolean(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE)) { 
anywheresoftware.b4a.keywords.Common.ExitApplication();};
 };
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
}
