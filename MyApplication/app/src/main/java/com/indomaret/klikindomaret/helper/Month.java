package com.indomaret.klikindomaret.helper;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by indomaretitsd7 on 6/16/16.
 */
public class Month {
    public String getMonth(String month){
        if(month.equals("01")){
            return "Januari";
        } else if(month.equals("02")){
            return "Februari";
        } else if(month.equals("03")){
            return "Maret";
        } else if(month.equals("04")){
            return "April";
        } else if(month.equals("05")){
            return "Mei";
        } else if(month.equals("06")){
            return "Juni";
        } else if(month.equals("07")){
            return "Juli";
        } else if(month.equals("08")){
            return "Agustus";
        } else if(month.equals("09")){
            return "September";
        } else if(month.equals("10")){
            return "Oktober";
        } else if(month.equals("11")){
            return "November";
        } else if(month.equals("12")){
            return "Desember";
        }

        return null;
    }

    public String getMonth2(String month){
        if(month.equals("01")){
            return "Jan";
        } else if(month.equals("02")){
            return "Feb";
        } else if(month.equals("03")){
            return "Mar";
        } else if(month.equals("04")){
            return "Apr";
        } else if(month.equals("05")){
            return "Mei";
        } else if(month.equals("06")){
            return "Jun";
        } else if(month.equals("07")){
            return "Jul";
        } else if(month.equals("08")){
            return "Agust";
        } else if(month.equals("09")){
            return "Sept";
        } else if(month.equals("10")){
            return "Okt";
        } else if(month.equals("11")){
            return "Nov";
        } else if(month.equals("12")){
            return "Des";
        }

        return null;
    }

    public String getDay(String day){
        if(day.equals("Sun") || day.equals("Min")){
            return "Minggu";
        } else if(day.equals("Mon") || day.equals("Sen")){
            return "Senin";
        } else if(day.equals("Tue") || day.equals("Sel")){
            return "Selasa";
        } else if(day.equals("Wed") || day.equals("Rab")){
            return "Rabu";
        } else if(day.equals("Thu") || day.equals("Kam")){
            return "Kamis";
        } else if(day.equals("Fri") || day.equals("Jum")){
            return "Jumat";
        } else if(day.equals("Sat") || day.equals("Sab")){
            return "Sabtu";
        }

        return null;
    }

    public String getDay2(String day){
        if(day.equals("1")){
            return "Minggu";
        } else if(day.equals("2")){
            return "Senin";
        } else if(day.equals("3")){
            return "Selasa";
        } else if(day.equals("4")){
            return "Rabu";
        } else if(day.equals("5")){
            return "Kamis";
        } else if(day.equals("6")){
            return "Jumat";
        } else if(day.equals("7")){
            return "Sabtu";
        }

        return null;
    }

    public String getDay3(String day){
        if(day.equals("1")){
            return "Mg";
        } else if(day.equals("2")){
            return "Sn";
        } else if(day.equals("3")){
            return "Sl";
        } else if(day.equals("4")){
            return "Rb";
        } else if(day.equals("5")){
            return "Km";
        } else if(day.equals("6")){
            return "Jm";
        } else if(day.equals("7")){
            return "Sb";
        }

        return null;
    }

    public String getCurrentDay(String year, String month, String date){
        Calendar calendar = new GregorianCalendar(Integer.valueOf(year), Integer.valueOf(month) - 1, Integer.valueOf(date));
        int resultDay = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println("--- resultDay : "+resultDay);
        switch (resultDay) {
            case Calendar.MONDAY:
                return "Senin";
            case Calendar.TUESDAY:
                return "Selasa";
            case Calendar.WEDNESDAY:
                return "Rabu";
            case Calendar.THURSDAY:
                return "Kamis";
            case Calendar.FRIDAY:
                return "Jumat";
            case Calendar.SATURDAY:
                return "Sabtu";
            case Calendar.SUNDAY:
                return "Minggu";
        }

        return null;
    }

    public String getMonthNumber(String month){
        if(month.equals("Januari")){
            return "01";
        } else if(month.equals("Februari")){
            return "02";
        } else if(month.equals("Maret")){
            return "03";
        } else if(month.equals("April")){
            return "04";
        } else if(month.equals("Mei")){
            return "05";
        } else if(month.equals("Juni")){
            return "06";
        } else if(month.equals("Juli")){
            return "07";
        } else if(month.equals("Agustus")){
            return "08";
        } else if(month.equals("September")){
            return "09";
        } else if(month.equals("Oktober")){
            return "10";
        } else if(month.equals("November")){
            return "11";
        } else if(month.equals("Desember")){
            return "12";
        }

        return null;
    }
}
