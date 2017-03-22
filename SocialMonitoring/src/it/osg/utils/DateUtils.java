package it.osg.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DateUtils {
	public static final String DATE_PATTERN = "dd-MM-yyyy";
	public static final String DATE_PATTERN_MONTH_YEAR = "MM-yyyy";
	public static final String DATE_ORACLE_PATTERN = "yyyy-MM-dd";
	public static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
	public static final String DATE_TIME_PATTERN_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.S";

	public static Date parseDate(String dateStr) throws ParseException {
		if ((dateStr == null) || (dateStr.trim().length() != 10)) {
			throw new ParseException("", 0);
		}

		if ((dateStr.charAt(2) != '-') || (dateStr.charAt(5) != '-')) {
			throw new ParseException("", 0);
		}

		SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
		formatter.setLenient(false);

		return formatter.parse(dateStr);
	}

	public static Date parseDate(String dateStr, String pattern)
			throws ParseException {
		if ((dateStr == null) ||
				(dateStr.trim().length() != DATE_PATTERN.length())) {
			throw new ParseException("", 0);
		}

		dateStr = dateStr.replace("/", "-");

		if ((dateStr.charAt(2) != '-') || (dateStr.charAt(5) != '-')) {
			throw new ParseException("", 0);
		}

		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		formatter.setLenient(false);

		return formatter.parse(dateStr);
	}

	//	public static Date parseDateTimestamp(String dateStr,String pattern) throws ParseException{
	//		if(dateStr == null || dateStr.trim().length() < 10)
	//			throw new ParseException("", 0);
	//
	//		if(dateStr.charAt(2) != '-' ||  dateStr.charAt(5) != '-' ){
	//			pattern = DATE_ORACLE_PATTERN; 
	//		}else{
	//			pattern = DATE_PATTERN;
	//		}
	//
	//
	//		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
	//		formatter.setLenient(false);
	//		return formatter.parse(dateStr);
	//	}
	public static Date parseDateAndTime(String dateStr)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);

		return formatter.parse(dateStr);
	}

	public static String formatDateAndTime(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);

		return formatter.format(date);
	}

	public static String formatDate(Date date) {
		if (date == null) {
			return "";
		}

		SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
		String dataStr = formatter.format(date);
		dataStr = dataStr.replace("-", "/");

		return dataStr;
	}

	public static String formatDate(Date date, String pattern) {
		if (date == null) {
			return "";
		}

		SimpleDateFormat formatter = new SimpleDateFormat(pattern);

		return formatter.format(date);
	}

	public static Date parseDateOracle(String dateStr)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_ORACLE_PATTERN);

		return formatter.parse(dateStr);
	}

	public static String formatDateOracle(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
		String d = formatter.format(date);
		String ora = " to_date('dd/mm/yyyy', '" + d + "') ";

		return ora;
	}

	public static Date addOneDay(Date date) {
		return addDayToDate(date, 1);
	}

	public static Date subOneDay(Date date) {
		return addDayToDate(date, -1);
	}

	public static Date addDayToDate(Date date, int numDay) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, numDay);

		return cal.getTime();
	}
	
	public static Date addHoursToDate(Date date, int numHours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, numHours);

		return cal.getTime();
	}

	public static Date addMonthToDate(Date data, int t) {
		if (data == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);

		int day = cal.get(Calendar.DATE);
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		//se la data e' fine mese
		if (day == lastDay) {
			int month = cal.get(Calendar.MONTH);
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.set(Calendar.YEAR, cal.get(Calendar.YEAR));
			newCalendar.set(Calendar.MONTH, month + t);

			int newLastDay = newCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

			if (newLastDay != lastDay) {
				newCalendar.set(Calendar.DATE, newLastDay);
			} else {
				newCalendar.set(Calendar.DATE, lastDay);
			}

			return newCalendar.getTime();
		} else {
			cal.add(Calendar.MONTH, t);

			return cal.getTime();
		}
	}

	public static int diffInMonth(Date dataI, Date dataF) {
		if ((dataI == null) || (dataF == null)) {
			return -1;
		}

		if ((dataI != null) && dataI.after(dataF)) {
			return -1;
		}

		int diffMesi = 0;
		Calendar cal = Calendar.getInstance();

		cal.setTime(dataI);

		int annoI = cal.get(Calendar.YEAR);
		int meseI = cal.get(Calendar.MONTH);
		int dayI = cal.get(Calendar.DATE);

		cal.setTime(dataF);

		int annoF = cal.get(Calendar.YEAR);
		int meseF = cal.get(Calendar.MONTH);
		int dayF = cal.get(Calendar.DATE);

		if (annoF == annoI) {
			diffMesi = meseF - meseI + 1;

			return diffMesi;
		}

		int restoAnnoCorr = 12 - meseI;
		int restoAnni = (annoF - annoI) - 1;
		restoAnni = restoAnni * 12;

		diffMesi = restoAnnoCorr + restoAnni + meseF + 1;

		return diffMesi;
	}

	public static int diffInMonthArrotondata(Date dataI, Date dataF) {
		if ((dataI == null) || (dataF == null)) {
			return -1;
		}

		if ((dataI != null) && dataI.after(dataF)) {
			return -1;
		}

		int diffMesi = 0;
		Calendar cal = Calendar.getInstance();

		cal.setTime(dataI);

		int annoI = cal.get(Calendar.YEAR);
		int meseI = cal.get(Calendar.MONTH);
		int dayI = cal.get(Calendar.DATE);

		cal.setTime(dataF);

		int annoF = cal.get(Calendar.YEAR);
		int meseF = cal.get(Calendar.MONTH);
		int dayF = cal.get(Calendar.DATE);

		if (annoF == annoI) {
			diffMesi = meseF - meseI + 1;

			if (diffMesi == 0) {
				if ((dayF - dayI) > 15) {
					return 1;
				} else {
					return 0;
				}
			}

			return diffMesi;
		}

		int restoAnnoCorr = 12 - meseI;
		int restoAnni = (annoF - annoI) - 1;
		restoAnni = restoAnni * 12;

		diffMesi = restoAnnoCorr + restoAnni + meseF + 1;

		if (dayI <= 15) {
			diffMesi++;
		}

		return diffMesi;
	}

	public static int diffInDay(Date dataA, Date dataB) {
		Calendar calendarA = Calendar.getInstance();
		calendarA.setTime(dataA);
		calendarA.set(Calendar.HOUR_OF_DAY, 0);
		calendarA.set(Calendar.MINUTE, 0);
		calendarA.set(Calendar.SECOND, 0);
		calendarA.set(Calendar.MILLISECOND, 0);

		Calendar calendarB = Calendar.getInstance();
		calendarB.setTime(dataB);

		calendarB.set(Calendar.HOUR_OF_DAY, 0);
		calendarB.set(Calendar.MINUTE, 0);
		calendarB.set(Calendar.SECOND, 0);
		calendarB.set(Calendar.MILLISECOND, 0);

		long numMill = (calendarB.getTimeInMillis() -
				calendarA.getTimeInMillis());

		return (int) (numMill / 86400000);
	}

	public static Date maxData(Date[] data) {
		if ((data == null) || (data.length == 0)) {
			return null;
		}

		Arrays.sort(data);

		return data[data.length - 1];
	}

	/**
	 * confonta giorno,mese e anno tra due date<br>
	 *  <br>
	 * ritorna -1 se data < dataRif<br>
	 * ritorna 0 se data = dataRif<br>
	 * ritorna +1 se data > dataRif<br>
	 * @return
	 */
	 public static int compareDate(Date data, Date dataRif) {
		 Calendar da = new GregorianCalendar();
		 Calendar dRiferimento = new GregorianCalendar();
		 da.setTime(data);
		 dRiferimento.setTime(dataRif);

		 da.set(Calendar.HOUR_OF_DAY, 0);
		 da.set(Calendar.MINUTE, 0);
		 da.set(Calendar.SECOND, 0);
		 da.set(Calendar.MILLISECOND, 0);

		 dRiferimento.set(Calendar.HOUR_OF_DAY, 0);
		 dRiferimento.set(Calendar.MINUTE, 0);
		 dRiferimento.set(Calendar.SECOND, 0);
		 dRiferimento.set(Calendar.MILLISECOND, 0);

		 if (da.before(dRiferimento)) {
			 return -1;
		 }

		 if (da.after(dRiferimento)) {
			 return +1;
		 }

		 if (da.equals(dRiferimento)) {
			 return 0;
		 }

		 return 0;
	 }

	 /**
	  * confonta mese e anno tra due date
	  *
	  * ritorna -1 se data < dataRif
	  * ritorna 0 se data = dataRif
	  * ritorna +1 se data > dataRif
	  * @return
	  */
	 public static int compareDateMonthYear(Date data, Date dataRif) {
		 Calendar da = new GregorianCalendar();
		 Calendar dRiferimento = new GregorianCalendar();
		 da.setTime(data);
		 dRiferimento.setTime(dataRif);

		 da.set(Calendar.DATE, 1);
		 da.set(Calendar.HOUR_OF_DAY, 0);
		 da.set(Calendar.MINUTE, 0);
		 da.set(Calendar.SECOND, 0);
		 da.set(Calendar.MILLISECOND, 0);

		 dRiferimento.set(Calendar.DATE, 1);
		 dRiferimento.set(Calendar.HOUR_OF_DAY, 0);
		 dRiferimento.set(Calendar.MINUTE, 0);
		 dRiferimento.set(Calendar.SECOND, 0);
		 dRiferimento.set(Calendar.MILLISECOND, 0);

		 if (da.before(dRiferimento)) {
			 return -1;
		 }

		 if (da.after(dRiferimento)) {
			 return +1;
		 }

		 if (da.equals(dRiferimento)) {
			 return 0;
		 }

		 return 0;
	 }

	 /**
	  * ritorna true se data e' maggiore di dataRif
	  * @param data
	  * @param dataRif
	  * @return
	  */
	 public static boolean afterNoTime(Date data, Date dataRif) {
		 return compareDate(data, dataRif) == 1;
	 }

	 /**
	  * ritorna true se data e' minore di dataRif
	  * @param data
	  * @param dataRif
	  * @return
	  */
	 public static boolean beforeNoTime(Date data, Date dataRif) {
		 return compareDate(data, dataRif) == -1;
	 }

	 /**
	  * ritorna true se data e' uguale di dataRif
	  * @param data
	  * @param dataRif
	  * @return
	  */
	 public static boolean equalNoTime(Date data, Date dataRif) {
		 return compareDate(data, dataRif) == 0;
	 }

	 /**
	  * Azzera l'orario di una data
	  * @param dataIn
	  * @return
	  */
	 public static Date truncCrono(Date dataIn) {
		 Calendar dataOut = new GregorianCalendar();
		 dataOut.setTime(dataIn);

		 dataOut.set(Calendar.HOUR_OF_DAY, 0);
		 dataOut.set(Calendar.MINUTE, 0);
		 dataOut.set(Calendar.SECOND, 0);
		 dataOut.set(Calendar.MILLISECOND, 0);

		 return dataOut.getTime();
	 }

	 /**
	  * imposta l'ora a 00:00:00 e il giorno al pimo del mese
	  * @param data
	  */
	 public static Date resetDayAndTime(Date data) {
		 Calendar da = new GregorianCalendar();
		 da.setTime(data);

		 da.set(Calendar.DATE, 1);
		 da.set(Calendar.HOUR_OF_DAY, 0);
		 da.set(Calendar.MINUTE, 0);
		 da.set(Calendar.SECOND, 0);
		 da.set(Calendar.MILLISECOND, 0);

		 return da.getTime();
	 }

	 /**
	  * Restituisce la data corrente senza l'orario
	  **/
	 public static Date todayNoTime() {
		 return truncCrono(Calendar.getInstance().getTime());
	 }

	 public static void main(String[] args) {
		 Calendar cal = Calendar.getInstance();
		 cal.set(2005, Calendar.FEBRUARY, 25);

		 System.out.println(DateUtils.addMonthToDate(cal.getTime(), 2));

		 Calendar cal1 = Calendar.getInstance();
		 cal1.set(2005, Calendar.APRIL, 25);

		 System.out.println("Diff In DAy" +
				 diffInDay(cal1.getTime(), cal.getTime()));

		 int x = 24;

		 System.out.println((char) (65 + x));
	 }

	 public static boolean between(Date data, Date dataA, Date dataB) {
		 if (!beforeNoTime(data, dataA) && !afterNoTime(data, dataB)) {
			 return true;
		 }

		 return false;
	 }

	 public static void addYearToDate(Date date, int numYear) {
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(date);
		 cal.add(Calendar.YEAR, numYear);
		 date.setTime(cal.getTimeInMillis());
	 }

	 public static void lastDayOfDeterminateMonth(Date data, int mese) {
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(data);
		 cal.set(Calendar.MONTH, mese - 1);
		 cal.set(Calendar.DAY_OF_MONTH,
				 cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		 cal.set(Calendar.MINUTE, 0);
		 cal.set(Calendar.SECOND, 0);
		 cal.set(Calendar.MILLISECOND, 0);
		 data.setTime(cal.getTimeInMillis());
	 }

	 public static Date lastDayOfMonth(Date data) {
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(data);
		 cal.set(Calendar.DAY_OF_MONTH,
				 cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		 cal.set(Calendar.MINUTE, 0);
		 cal.set(Calendar.SECOND, 0);
		 cal.set(Calendar.MILLISECOND, 0);

		 return cal.getTime();
	 }

	 /**
	  *  Ritorna la differenza in mesi tra la due date <br>
	  *  es. dataI=xx/08/2005 - dataF=xx/09/2005 --> out = 1
	  */
	 public static int diffInMonthNoDate(Date dataI, Date dataF) {
		 int diffMesi = 0;
		 Calendar cal = Calendar.getInstance();

		 cal.setTime(dataI);

		 int annoI = cal.get(Calendar.YEAR);
		 int meseI = cal.get(Calendar.MONTH);

		 cal.setTime(dataF);

		 int annoF = cal.get(Calendar.YEAR);
		 int meseF = cal.get(Calendar.MONTH);

		 int restoAnnoCorr = 12 - meseI;
		 int restoAnni = (annoF - annoI) - 1;
		 restoAnni = restoAnni * 12;
		 diffMesi = restoAnnoCorr + restoAnni + meseF;

		 return diffMesi;
	 }

	 public static String formatoData(String natoIlStr2) {
		 String a = "";

		 for (int i = 0; i < natoIlStr2.length(); i++) {
			 int c = natoIlStr2.charAt(i);

			 if (c == '/') {
				 a = natoIlStr2.replace('/', '-');
			 }
		 }

		 if (a.equals("")) {
			 return natoIlStr2;
		 } else {
			 return a;
		 }
	 }

	 public static Date parseDateTimestamp(String dateStr, String pattern)
			 throws ParseException {
		 if ((dateStr == null) || (dateStr.trim().length() < 10)) {
			 throw new ParseException("", 0);
		 }

		 if ((dateStr.charAt(2) != '-') || (dateStr.charAt(5) != '-')) {
			 pattern = DATE_ORACLE_PATTERN;
		 } else {
			 pattern = DATE_PATTERN;
		 }

		 SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		 formatter.setLenient(false);

		 return formatter.parse(dateStr);
	 }

	 public static long giorniTraDueDate(Date uno, Date due) {
		 Calendar c1 = Calendar.getInstance();
		 Calendar c2 = Calendar.getInstance();
		 c1.setTime(uno);
		 c2.setTime(due);
		 long giorni = (c2.getTime().getTime() - c1.getTime().getTime()) / (24 * 3600 * 1000);
		 return giorni + 1;
	 }
	 
	 public static long secondiTraDueDate(Date uno, Date due) {
		 Calendar c1 = Calendar.getInstance();
		 Calendar c2 = Calendar.getInstance();
		 c1.setTime(uno);
		 c2.setTime(due);
		 long giorni = (c2.getTime().getTime() - c1.getTime().getTime()) / (1000);
		 return giorni;
	 }
	 
	 public static String getNow() {
			Calendar cal= Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.add(Calendar.HOUR_OF_DAY, 2);
			SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date cestDate= cal.getTime();
			return DateUtils.formatDateAndTime(cestDate);
	 }
	 
	 public static Date getNowDate() {
			Calendar cal= Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.add(Calendar.HOUR_OF_DAY, 2);
			SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date cestDate= cal.getTime();
			return cestDate;
	 }
	 
	 public static String parseTimestamp(long timestamp) {
			Calendar cal= Calendar.getInstance();
			cal.setTimeInMillis(timestamp);
			cal.add(Calendar.HOUR_OF_DAY, 2);
			SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date cestDate= cal.getTime();
			return DateUtils.formatDateAndTime(cestDate);
	 }
	 
}
