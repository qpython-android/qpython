package org.qpython.qpysdk.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {
	   public static String converTime(long timestamp, String[] timeLabels){
           long currentSeconds = System.currentTimeMillis()/1000;
           long timeGap = currentSeconds-timestamp;//与现在时间相差秒数
           //Log.d(TAG, "converTime:"+currentSeconds+"-"+timestamp);
           String timeStr = null;
           if (timeGap>24*60*60) {//1天以上
                   timeStr = timeGap/(24*60*60)+timeLabels[0];
           }else if(timeGap>60*60){//1小时-24小时
                   timeStr = timeGap/(60*60)+timeLabels[1];
           }else if(timeGap>60){//1分钟-59分钟
                   timeStr = timeGap/60+timeLabels[2];
           }else{//1秒钟-59秒钟
                   timeStr = timeLabels[3];
           }
           return timeStr;
   }
   
   public static String getStandardTime(long timestamp){
           SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
           Date date = new Date(timestamp*1000);
           sdf.format(date);
           return sdf.format(date);
   }
	
   public static final String getDateAsDirName(){
       Calendar cal = Calendar.getInstance();
       java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       String cdate = sdf.format(cal.getTime());
       return cdate;
    }

   public static final String getDateAss(){
       Calendar cal = Calendar.getInstance();
       java.text.SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-HH-mm");
       String cdate = sdf.format(cal.getTime());
       return cdate;
    }

    public static final String getDate(){
       Calendar cal = Calendar.getInstance();
       java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String cdate = sdf.format(cal.getTime());
       return cdate;
    }

    public static final String getDateMin(){
        Calendar cal = Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String cdate = sdf.format(cal.getTime());
        return cdate;
     }

    public static final String getTodayFull() {
        Calendar cal = Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String cdate = sdf.format(cal.getTime());
        //Log.d(TAG, "getTodayFull:"+cdate);
        return cdate;
    }

    public static final int getTimeAsInt(){
        Calendar cal = Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        String cdate = sdf.format(cal.getTime());
        return Integer.parseInt(cdate);
     }
    public static final String getDateAsF(){
        Calendar cal = Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String cdate = sdf.format(cal.getTime());
        return cdate;
     }
	private static final String TAG = "DateTimeHelper";

	// Wed Dec 15 02:53:36 +0000 2010
	public static final DateFormat TWITTER_DATE_FORMATTER = new SimpleDateFormat(
			"E MMM d HH:mm:ss Z yyyy", Locale.US);

	public static final DateFormat TWITTER_SEARCH_API_DATE_FORMATTER = new SimpleDateFormat(
			"E, d MMM yyyy HH:mm:ss Z", Locale.US);

	public static final Date parseDateTime(String dateString) {
		try {
			Log.v(TAG, String.format("in parseDateTime, dateString=%s",
					dateString));
			return TWITTER_DATE_FORMATTER.parse(dateString);
		} catch (ParseException e) {
			Log.w(TAG, "Could not parse Twitter date string: " + dateString);
			return null;
		}
	}


	public static final Date parseSearchApiDateTime(String dateString) {
		try {
			return TWITTER_SEARCH_API_DATE_FORMATTER.parse(dateString);
		} catch (ParseException e) {
			Log.w(TAG, "Could not parse Twitter search date string: "
					+ dateString);
			return null;
		}
	}

	public static final DateFormat AGO_FULL_DATE_FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	/*public static String getRelativeDate(Date date) {
		Date now = new Date();

		String prefix = PassingerApp.mContext
				.getString(R.string.tweet_created_at_beautify_prefix);
		String sec = PassingerApp.mContext
				.getString(R.string.tweet_created_at_beautify_sec);
		String min = PassingerApp.mContext
				.getString(R.string.tweet_created_at_beautify_min);
		String hour = PassingerApp.mContext
				.getString(R.string.tweet_created_at_beautify_hour);
		String day = PassingerApp.mContext
				.getString(R.string.tweet_created_at_beautify_day);
		String suffix = PassingerApp.mContext
				.getString(R.string.tweet_created_at_beautify_suffix);

		// Seconds.
		long diff = (now.getTime() - date.getTime()) / 1000;

		if (diff < 0) {
			diff = 0;
		}

		if (diff < 60) {
			return diff + sec + suffix;
		}

		// Minutes.
		diff /= 60;

		if (diff < 60) {
			return prefix + diff + min + suffix;
		}

		// Hours.
		diff /= 60;

		if (diff < 24) {
			return prefix + diff + hour + suffix;
		}

		return AGO_FULL_DATE_FORMATTER.format(date);
	}*/

	public static long getNowTime() {
		return Calendar.getInstance().getTime().getTime();
	}

	public static long getDiffMin(String date1, String date2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//java.util.Date now;
		java.util.Date begin;
		try {
			begin = df.parse(date1);
			java.util.Date end = df.parse(date2);
			//Log.d(TAG, date1+"-"+date2);
			long between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒

			long minute1=between%3600/60;

			return minute1;
		} catch (ParseException e) {
			e.printStackTrace();

			return 0;
		}

	}
	public static String getDiff(Date begin, Date end) {
		//long between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒

		//long day1=between/(24*3600);
		int day2 = (end.getMonth()*100+end.getDate())-(begin.getMonth()*100+begin.getDate());

		//long hour1=between%(24*3600)/3600;
		//long minute1=between%3600/60;
		//long second1=between%60/60;
		//String dateAt = String.valueOf(begin.getYear())+CONF.T_Y+String.valueOf(begin.getMonth())+CONF.T_M+String.valueOf(begin.getDay())+CONF.T_D;
		String x1 = String.valueOf(begin.getHours());
		if (x1.length()==1) {
			x1 = "0"+x1;
		}
		String x2 = String.valueOf(begin.getMinutes());
		if (x2.length()==1) {
			x2 = "0"+x2;
		}
		String timeAt = x1+":"+x2;
		//String ret = "";
		//Log.d(TAG, "getDiff:"+date1+"-"+date2+"-"+(end.getMonth()*100+end.getDate())+"-"+(begin.getMonth()*100+begin.getDate()));

		SimpleDateFormat ymformatter = new SimpleDateFormat("yyyyMM");
		String bYm = ymformatter.format(begin);
		String eYm = ymformatter.format(end);

		if (bYm.equals(eYm)) {
			if (day2>1) {
				SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
				return formatter.format(begin)+" "+timeAt;
			}

			if (day2==1) {
				return "Yesterday "+timeAt;
			}
			/*if (day2==2) {
				return CONF.T_BS+" "+timeAt;
			} */

			return "Today "+timeAt;


		} else {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.format(begin)+" "+timeAt;
		}

	}
	public static String getDiff(String date1, String date2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//java.util.Date now;
		try {
			java.util.Date begin=df.parse(date1);
			java.util.Date end = df.parse(date2);
			return DateTimeHelper.getDiff(begin, end);

		} catch (ParseException e) {
			e.printStackTrace();
			return "unknow";
		}

	}
	
	public static String longTimeToString(int i) {
		StringBuilder sb = new StringBuilder();
		i = i / 1000;
		int m = i / 60;
		int s = i % 60;
		if (i >= 60) {
			if (m < 10) {
				sb.append("0");
				sb.append(String.valueOf(m));
			} else {
				sb.append(String.valueOf(m));
			}
			sb.append(":");
			if (s > 9) {
				sb.append(String.valueOf(s));
			} else {
				sb.append("0");
				sb.append(String.valueOf(s));
			}
		} else {
			sb.append("00:");
			if (s > 9) {
				sb.append(String.valueOf(s));
			} else {
				sb.append("0");
				sb.append(String.valueOf(s));
			}
		}
		return sb.toString();
	}
}
