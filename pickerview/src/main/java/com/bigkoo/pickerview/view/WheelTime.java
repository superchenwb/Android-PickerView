package com.bigkoo.pickerview.view;

import android.view.View;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.listener.ISelectTimeCallback;
import com.bigkoo.pickerview.utils.ChinaDate;
import com.bigkoo.pickerview.utils.LunarCalendar;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class WheelTime {
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private View view;
    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;
    private WheelView wv_hours;
    private WheelView wv_minutes;
    private WheelView wv_seconds;
    private int gravity;

    private boolean[] type;
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;
    private static final int DEFAULT_START_MONTH = 1;
    private static final int DEFAULT_END_MONTH = 12;
    private static final int DEFAULT_START_DAY = 1;
    private static final int DEFAULT_END_DAY = 31;
    private static final int DEFAULT_START_HOUR = 0;
    private static final int DEFAULT_END_HOUR = 23;
    private static final int DEFAULT_START_MINUTE = 0;
    private static final int DEFAULT_END_MINUTE = 59;

    private int startYear = DEFAULT_START_YEAR;
    private int endYear = DEFAULT_END_YEAR;
    private int startMonth = DEFAULT_START_MONTH;
    private int endMonth = DEFAULT_END_MONTH;
    private int startDay = DEFAULT_START_DAY;
    private int endDay = DEFAULT_END_DAY; //表示31天的
    private int startHour = DEFAULT_START_HOUR;
    private int endHour = DEFAULT_END_HOUR;
    private int startMinute = DEFAULT_START_MINUTE;
    private int endMinute = DEFAULT_END_MINUTE;
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private int currentHour;

    private int textSize;

    //文字的颜色和分割线的颜色
    private int textColorOut;
    private int textColorCenter;
    private int dividerColor;

    private float lineSpacingMultiplier;
    private WheelView.DividerType dividerType;
    private boolean isLunarCalendar = false;
    private ISelectTimeCallback mSelectChangeCallback;

    // 添加大小月月份并将其转换为list,方便之后的判断
    String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
    String[] months_little = {"4", "6", "9", "11"};

    final List<String> list_big = Arrays.asList(months_big);
    final List<String> list_little = Arrays.asList(months_little);

    public WheelTime(View view, boolean[] type, int gravity, int textSize) {
        super();
        this.view = view;
        this.type = type;
        this.gravity = gravity;
        this.textSize = textSize;
    }

    public void setLunarMode(boolean isLunarCalendar) {
        this.isLunarCalendar = isLunarCalendar;
    }

    public boolean isLunarMode() {
        return isLunarCalendar;
    }

    public void setPicker(int year, int month, int day) {
        this.setPicker(year, month, day, 0, 0, 0);
    }

    public void setPicker(int year, final int month, int day, int h, int m, int s) {
        if (isLunarCalendar) {
            int[] lunar = LunarCalendar.solarToLunar(year, month + 1, day);
            setLunar(lunar[0], lunar[1] - 1, lunar[2], lunar[3] == 1, h, m, s);
        } else {
            setSolar(year, month, day, h, m, s);
        }
    }

    /**
     * 设置农历
     *
     * @param year
     * @param month
     * @param day
     * @param h
     * @param m
     * @param s
     */
    private void setLunar(int year, final int month, int day, boolean isLeap, int h, int m, int s) {
        // 年
        wv_year = (WheelView) view.findViewById(R.id.year);
        wv_year.setAdapter(new ArrayWheelAdapter(ChinaDate.getYears(startYear, endYear)));// 设置"年"的显示数据
        wv_year.setLabel("");// 添加文字
        wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据
        wv_year.setGravity(gravity);

        // 月
        wv_month = (WheelView) view.findViewById(R.id.month);
        wv_month.setAdapter(new ArrayWheelAdapter(ChinaDate.getMonths(year)));
        wv_month.setLabel("");
        
        int leapMonth = ChinaDate.leapMonth(year);
        if (leapMonth != 0 && (month > leapMonth - 1 || isLeap)) { //选中月是闰月或大于闰月
            wv_month.setCurrentItem(month + 1);
        } else {
            wv_month.setCurrentItem(month);
        }
        
        wv_month.setGravity(gravity);

        // 日
        wv_day = (WheelView) view.findViewById(R.id.day);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (ChinaDate.leapMonth(year) == 0) {
            wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year, month))));
        } else {
            wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year))));
        }
        wv_day.setLabel("");
        wv_day.setCurrentItem(day - 1);
        wv_day.setGravity(gravity);

        wv_hours = (WheelView) view.findViewById(R.id.hour);
        wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        //wv_hours.setLabel(context.getString(R.string.pickerview_hours));// 添加文字
        wv_hours.setCurrentItem(h);
        wv_hours.setGravity(gravity);

        wv_minutes = (WheelView) view.findViewById(R.id.min);
        wv_minutes.setAdapter(new NumericWheelAdapter(0, 59));
        //wv_minutes.setLabel(context.getString(R.string.pickerview_minutes));// 添加文字
        wv_minutes.setCurrentItem(m);
        wv_minutes.setGravity(gravity);

        wv_seconds = (WheelView) view.findViewById(R.id.second);
        wv_seconds.setAdapter(new NumericWheelAdapter(0, 59));
        //wv_seconds.setLabel(context.getString(R.string.pickerview_minutes));// 添加文字
        wv_seconds.setCurrentItem(m);
        wv_seconds.setGravity(gravity);

        // 添加"年"监听
        wv_year.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int year_num = index + startYear;
                // 判断是不是闰年,来确定月和日的选择
                wv_month.setAdapter(new ArrayWheelAdapter(ChinaDate.getMonths(year_num)));
                if (ChinaDate.leapMonth(year_num) != 0 && wv_month.getCurrentItem() > ChinaDate.leapMonth(year_num) - 1) {
                    wv_month.setCurrentItem(wv_month.getCurrentItem() + 1);
                } else {
                    wv_month.setCurrentItem(wv_month.getCurrentItem());
                }

                int currentIndex = wv_day.getCurrentItem();
                int maxItem = 29;
                if (ChinaDate.leapMonth(year_num) != 0 && wv_month.getCurrentItem() > ChinaDate.leapMonth(year_num) - 1) {
                    if (wv_month.getCurrentItem() == ChinaDate.leapMonth(year_num) + 1) {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year_num))));
                        maxItem = ChinaDate.leapDays(year_num);
                    } else {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, wv_month.getCurrentItem()))));
                        maxItem = ChinaDate.monthDays(year_num, wv_month.getCurrentItem());
                    }
                } else {
                    wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, wv_month.getCurrentItem() + 1))));
                    maxItem = ChinaDate.monthDays(year_num, wv_month.getCurrentItem() + 1);
                }

                if (currentIndex > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });

        // 添加"月"监听
        wv_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int month_num = index;
                int year_num = wv_year.getCurrentItem() + startYear;
                int currentIndex = wv_day.getCurrentItem();
                int maxItem = 29;
                if (ChinaDate.leapMonth(year_num) != 0 && month_num > ChinaDate.leapMonth(year_num) - 1) {
                    if (wv_month.getCurrentItem() == ChinaDate.leapMonth(year_num) + 1) {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year_num))));
                        maxItem = ChinaDate.leapDays(year_num);
                    } else {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, month_num))));
                        maxItem = ChinaDate.monthDays(year_num, month_num);
                    }
                } else {
                    wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, month_num + 1))));
                    maxItem = ChinaDate.monthDays(year_num, month_num + 1);
                }

                if (currentIndex > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });

        setChangedListener(wv_day);
        setChangedListener(wv_hours);
        setChangedListener(wv_minutes);
        setChangedListener(wv_seconds);

        if (type.length != 6) {
            throw new RuntimeException("type[] length is not 6");
        }
        wv_year.setVisibility(type[0] ? View.VISIBLE : View.GONE);
        wv_month.setVisibility(type[1] ? View.VISIBLE : View.GONE);
        wv_day.setVisibility(type[2] ? View.VISIBLE : View.GONE);
        wv_hours.setVisibility(type[3] ? View.VISIBLE : View.GONE);
        wv_minutes.setVisibility(type[4] ? View.VISIBLE : View.GONE);
        wv_seconds.setVisibility(type[5] ? View.VISIBLE : View.GONE);
        setContentTextSize();
    }

    /**
     * 设置公历
     *
     * @param year
     * @param month
     * @param day
     * @param h
     * @param m
     * @param s
     */
    private void setSolar(int year, final int month, int day, int h, int m, int s) {
        System.out.println("year" + year + "-" + month + "-" + day + " " + h + ":" + m + ":" + s);

        currentYear = year;
        currentMonth = month + 1;
        currentDay = day;
        currentHour = h;
        // 年
        wv_year = (WheelView) view.findViewById(R.id.year);
        wv_year.setAdapter(new NumericWheelAdapter(startYear, endYear));// 设置"年"的显示数据


        wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据
        wv_year.setGravity(gravity);
        // 月
        wv_month = (WheelView) view.findViewById(R.id.month);
        wv_month.setGravity(gravity);

        // 日
        wv_day = (WheelView) view.findViewById(R.id.day);
        wv_day.setGravity(gravity);

        //时
        wv_hours = (WheelView) view.findViewById(R.id.hour);
        wv_hours.setCurrentItem(h);
        wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, DEFAULT_END_HOUR));
        wv_hours.setGravity(gravity);

        //分
        wv_minutes = (WheelView) view.findViewById(R.id.min);
        wv_minutes.setCurrentItem(m);
        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
        wv_minutes.setGravity(gravity);

        if (startYear == endYear) {//开始年等于终止年
            wv_month.setAdapter(new NumericWheelAdapter(startMonth, endMonth));
            wv_month.setCurrentItem(currentMonth - startMonth);

        } else if (year == startYear) {
            //起始日期的月份控制
            wv_month.setAdapter(new NumericWheelAdapter(startMonth, 12));
            wv_month.setCurrentItem(currentMonth - startMonth);
        } else if (year == endYear) {
            //终止日期的月份控制
            wv_month.setAdapter(new NumericWheelAdapter(1, endMonth));
            wv_month.setCurrentItem(currentMonth);
        } else {
            wv_month.setAdapter(new NumericWheelAdapter(1, 12));
            wv_month.setCurrentItem(currentMonth);
        }


        if (startYear == endYear && startMonth == endMonth) {

            setReDay(startDay, endDay);
            wv_day.setCurrentItem(day - startDay);
            if(startDay == endDay) {
                wv_hours.setCurrentItem(currentHour - startHour);
                wv_hours.setAdapter(new NumericWheelAdapter(startHour, endHour));
                if(startHour == endHour) {
                    wv_minutes.setCurrentItem(m - startMinute);
                    wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, endMinute));
                } else if(startHour == currentHour) {
                    wv_minutes.setCurrentItem(m - startMinute);
                    wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, DEFAULT_END_MINUTE));
                } else if(endHour == currentHour) {
                    wv_minutes.setCurrentItem(m);
                    wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, endMinute));
                }
            } else if (day == startDay) {
                wv_hours.setCurrentItem(h - startHour);
                wv_hours.setAdapter(new NumericWheelAdapter(startHour, DEFAULT_END_HOUR));
                if(startHour == h) {
                    wv_minutes.setCurrentItem(m - startMinute);
                    wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, DEFAULT_END_MINUTE));
                }
            } else if(day == endDay) {
                wv_hours.setCurrentItem(h);
                wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, endHour));
                if(endHour == h) {
                    wv_minutes.setCurrentItem(m);
                    wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, endMinute));
                }
            }
        } else if (year == startYear && currentMonth == startMonth) {
            setReDay(startDay, 31);
            wv_day.setCurrentItem(day - startDay);
            if (day == startDay) {
                wv_hours.setCurrentItem(h - startHour);
                wv_hours.setAdapter(new NumericWheelAdapter(startHour, DEFAULT_END_HOUR));
                if(h == startHour) {
                    wv_minutes.setCurrentItem(m - startMinute);
                    wv_hours.setAdapter(new NumericWheelAdapter(startMinute, DEFAULT_END_MINUTE));
                }
            }
        } else if (year == endYear && currentMonth == endMonth) {
            setReDay(1, endDay);
            wv_day.setCurrentItem(day - 1);
            if(day == endDay) {
                wv_hours.setCurrentItem(h);
                wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, endHour));
                if(endHour == h) {
                    wv_minutes.setCurrentItem(m);
                    wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, endMinute));
                }
            }
        } else {
            setReDay(1, 31);
            wv_day.setCurrentItem(day - 1);
        }

        //秒
        wv_seconds = (WheelView) view.findViewById(R.id.second);
        wv_seconds.setAdapter(new NumericWheelAdapter(0, 59));

        wv_seconds.setCurrentItem(s);
        wv_seconds.setGravity(gravity);

        // 添加"年"监听
        wv_year.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int year_num = index + startYear;
                currentYear = year_num;

                int currentMonthItem = wv_month.getCurrentItem();//记录上一次的item位置
                int currentHourItem = wv_hours.getCurrentItem(); // 记录上一次的小时的位置
                int currentMinuteItem = wv_minutes.getCurrentItem(); // 记录上一次的分钟的位置
                if(startYear == endYear) {
                    // 不处理
                } else if(currentYear == startYear) {   // 当前选择的年份与开始年份相等
                    //重新设置月份
                    wv_month.setAdapter(new NumericWheelAdapter(startMonth, 12));

                    if (currentMonthItem > wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = wv_month.getAdapter().getItemsCount() - 1;
                        wv_month.setCurrentItem(currentMonthItem);
                    }

                    currentMonth = currentMonthItem + startMonth;

                    if (currentMonth == startMonth) {
                        //重新设置日
                        setReDay(startDay, 31);
                    } else {
                        //重新设置日
                        setReDay(1, 31);
                    }

                    if(currentDay == startDay) {
                        wv_hours.setAdapter(new NumericWheelAdapter(startHour, DEFAULT_END_HOUR));
                        if (currentHourItem > wv_hours.getAdapter().getItemsCount() - 1) {
                            currentHourItem = wv_hours.getAdapter().getItemsCount() - 1;
                            wv_hours.setCurrentItem(currentHourItem);
                        }
                        currentHour = currentHourItem + startHour;

                        if(startHour == currentHour) {
                            wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, DEFAULT_END_MINUTE));
                            if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                                currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                                wv_minutes.setCurrentItem(currentMinuteItem);
                            }
                        } else {
                            wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                        }
                    } else {
                        wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, DEFAULT_END_HOUR));
                        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                    }
                } else if(currentYear == endYear) {     // 当前选择的年份与结束年份相等
                    //重新设置月份
                    wv_month.setAdapter(new NumericWheelAdapter(1, endMonth));
                    if (currentMonthItem > wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = wv_month.getAdapter().getItemsCount() - 1;
                        wv_month.setCurrentItem(currentMonthItem);
                    }
                    currentMonth = currentMonthItem + 1;

                    if (currentMonth == endMonth) {
                        //重新设置日
                        setReDay(1, endDay);
                        if(currentDay == endDay) {
                            wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, endHour));
                            if (currentHourItem > wv_hours.getAdapter().getItemsCount() - 1) {
                                currentHourItem = wv_hours.getAdapter().getItemsCount() - 1;
                                wv_hours.setCurrentItem(currentHourItem);
                            }
                            currentHour = currentHourItem;
                            if(currentHour == endHour) {
                                wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, endMinute));
                                if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                                    currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                                    wv_hours.setCurrentItem(currentMinuteItem);
                                }
                            }
                        } else {
                            wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, DEFAULT_END_HOUR));
                        }
                    } else {
                        //重新设置日
                        setReDay(1, 31);
                        wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, DEFAULT_END_HOUR));
                        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                    }
                } else {                                // 其他
                    //重新设置月份
                    wv_month.setAdapter(new NumericWheelAdapter(1, 12));
                    //重新设置日
                    setReDay(1, 31);
                    // 重新设置小时
                    wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, DEFAULT_END_HOUR));
                    // 重新设置分钟
                    wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });


        // 添加"月"监听
        wv_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int currentHourItem = wv_hours.getCurrentItem(); // 记录上一次的小时的位置
                int currentMinuteItem = wv_minutes.getCurrentItem(); // 记录上一次的分钟的位置
                int month_num = index + 1;
                // 开始时间等于结束时间
                if(currentYear == startYear) {
                    currentMonth = index + startMonth;
                } else {
                    currentMonth = month_num;
                }

                if (startYear == endYear && startMonth == endMonth) {
                    //重新设置日
                    setReDay(startDay, endDay);

                    // 重新设置小时
                    if(startDay == endDay) {
                        wv_hours.setAdapter(new NumericWheelAdapter(startHour, endHour));
                        if (currentHourItem > wv_hours.getAdapter().getItemsCount() - 1) {
                            currentHourItem = wv_hours.getAdapter().getItemsCount() - 1;
                            wv_hours.setCurrentItem(currentHourItem);
                        }
                        currentHour = currentHourItem + startHour;
                        if(startHour == endHour) {
                            wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, endMinute));
                            if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                                currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                                wv_minutes.setCurrentItem(currentMinuteItem);
                            }

                        } else if(currentHour == startHour) {
                            wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, DEFAULT_END_MINUTE));
                            if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                                currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                                wv_minutes.setCurrentItem(currentMinuteItem);
                            }
                        } else if(currentHour == endHour) {
                            wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, endMinute));
                        } else {
                            wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                        }
                    } else if(startDay == currentDay) {
                        wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, endHour));
                        currentHour = currentHourItem + startHour;
                        if(startHour == currentHour) {
                            wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, DEFAULT_END_MINUTE));
                            if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                                currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                                wv_minutes.setCurrentItem(currentMinuteItem);
                            }
                        } else {
                            wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                        }
                    } else if(endDay == currentDay) {
                        wv_hours.setAdapter(new NumericWheelAdapter(startHour, DEFAULT_END_HOUR));
                        if (currentHourItem > wv_hours.getAdapter().getItemsCount() - 1) {
                            currentHourItem = wv_hours.getAdapter().getItemsCount() - 1;
                            wv_hours.setCurrentItem(currentHourItem);
                        }
                        currentHour = currentHourItem;
                        if(endHour == currentHour) {
                            wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, endHour));
                        } else {
                            wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                        }
                    } else {
                        wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, DEFAULT_END_HOUR));
                        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                    }
                } else if (currentYear == startYear && currentMonth == startMonth) {
                    //重新设置日
                    setReDay(startDay, 31);
                    if(startDay == currentDay) {
                        wv_hours.setAdapter(new NumericWheelAdapter(startHour, DEFAULT_END_HOUR));
                        currentHour = currentHourItem + startHour;
                        if(startHour == currentHour) {
                            wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, DEFAULT_END_MINUTE));
                            if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                                currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                                wv_minutes.setCurrentItem(currentMinuteItem);
                            }
                        } else {
                            wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                        }
                    } else {
                        wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, DEFAULT_END_HOUR));
                        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                    }
                } else if (currentYear == endYear && currentMonth == endMonth) {
                    //重新设置日
                    setReDay(1, endDay);
                    if(endDay == currentDay) {
                        wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, endHour));
                        currentHour = currentHourItem;
                        if(endHour == currentHour) {
                            wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, endMinute));
                        }
                    } else {
                        wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, DEFAULT_END_HOUR));
                        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                    }

                } else {
                    //重新设置日
                    setReDay(1, 31);
                    // 重新设置小时
                    wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, DEFAULT_END_HOUR));
                    wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });

        // 添加"日"监听
        wv_day.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int currentHourItem = wv_hours.getCurrentItem(); // 记录上一次的小时的位置
                int currentMinuteItem = wv_minutes.getCurrentItem(); // 记录上一次的分钟的位置
                int day_num = index + 1;
                if(currentYear == startYear && currentMonth == startMonth) {
                    currentDay = index + startDay;
                } else {
                    currentDay = day_num;
                }

                if (startYear == endYear && startMonth == endMonth && startDay == endDay) {
                    wv_hours.setAdapter(new NumericWheelAdapter(startHour, endHour));
                    if (currentHourItem > wv_hours.getAdapter().getItemsCount() - 1) {
                        currentHourItem = wv_hours.getAdapter().getItemsCount() - 1;
                        wv_hours.setCurrentItem(currentHourItem);
                    }
                    currentHour = currentHourItem + startHour;
                    if(startHour == endHour) {
                        wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, endMinute));
                        if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                            currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                            wv_minutes.setCurrentItem(currentMinuteItem);
                        }
                    } else if(currentHour == startHour) {
                        wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, DEFAULT_END_MINUTE));
                        if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                            currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                            wv_minutes.setCurrentItem(currentMinuteItem);
                        }
                    } else if(currentHour == endHour) {
                        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, endMinute));
                    } else {
                        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                    }
                } else if (currentYear == startYear && currentMonth == startMonth && startDay == currentDay) {
                    wv_hours.setAdapter(new NumericWheelAdapter(startHour, DEFAULT_END_HOUR));
                    if (currentHourItem > wv_hours.getAdapter().getItemsCount() - 1) {
                        currentHourItem = wv_hours.getAdapter().getItemsCount() - 1;
                        wv_hours.setCurrentItem(currentHourItem);
                    }
                    currentHour = currentHourItem + startHour;
                    if(currentHour == startHour) {
                        wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, DEFAULT_END_MINUTE));
                        if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                            currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                            wv_minutes.setCurrentItem(currentMinuteItem);
                        }
                    } else {
                        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                    }
                } else if (currentYear == endYear && currentMonth == endMonth && endDay == currentDay) {
                    wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, endHour));
                    currentHour = currentHourItem;
                    if(currentHour == endHour) {
                        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, endMinute));
                    } else {
                        wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                    }
                } else {
                    // 重新设置小时
                    wv_hours.setAdapter(new NumericWheelAdapter(DEFAULT_START_HOUR, DEFAULT_END_HOUR));
                    wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });

        // 添加"时"监听
        wv_hours.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if(currentYear == startYear && currentMonth == startMonth && currentDay == startDay) {
                    currentHour = index + startHour;
                } else {
                    currentHour = index;
                }
                int currentMinuteItem = wv_minutes.getCurrentItem(); // 记录上一次的分钟的位置
//                Log.d("currentHour", String.valueOf(currentHour));
                if (startYear == endYear && startMonth == endMonth && startDay == endDay && startHour == endHour) {
                    wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, endMinute));
                    if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                        currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                        wv_minutes.setCurrentItem(currentMinuteItem);
                    }
                } else if (currentYear == startYear && currentMonth == startMonth && startDay == currentDay && currentHour == startHour) {
                    wv_minutes.setAdapter(new NumericWheelAdapter(startMinute, DEFAULT_END_MINUTE));
                    if (currentMinuteItem > wv_minutes.getAdapter().getItemsCount() - 1) {
                        currentMinuteItem = wv_minutes.getAdapter().getItemsCount() - 1;
                        wv_minutes.setCurrentItem(currentMinuteItem);
                    }
                } else if (currentYear == endYear && currentMonth == endMonth && startDay == currentDay && currentHour == endHour) {
                    wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, endMinute));
                } else {
                    wv_minutes.setAdapter(new NumericWheelAdapter(DEFAULT_START_MINUTE, DEFAULT_END_MINUTE));
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });

        setChangedListener(wv_minutes);
        setChangedListener(wv_seconds);

        if (type.length != 6) {
            throw new IllegalArgumentException("type[] length is not 6");
        }
        wv_year.setVisibility(type[0] ? View.VISIBLE : View.GONE);
        wv_month.setVisibility(type[1] ? View.VISIBLE : View.GONE);
        wv_day.setVisibility(type[2] ? View.VISIBLE : View.GONE);
        wv_hours.setVisibility(type[3] ? View.VISIBLE : View.GONE);
        wv_minutes.setVisibility(type[4] ? View.VISIBLE : View.GONE);
        wv_seconds.setVisibility(type[5] ? View.VISIBLE : View.GONE);
        setContentTextSize();
    }

    private void setChangedListener(WheelView wheelView) {
        if (mSelectChangeCallback != null) {
            wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            });
        }

    }


    private void setReDay(int startD, int endD) {
        int currentItem = wv_day.getCurrentItem();

//        int maxItem;
        if (list_big.contains(String.valueOf(currentMonth))) {
            if (endD > 31) {
                endD = 31;
            }
            wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
//            maxItem = endD;
        } else if (list_little.contains(String.valueOf(currentMonth))) {
            if (endD > 30) {
                endD = 30;
            }
            wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
//            maxItem = endD;
        } else {
            if ((currentYear % 4 == 0 && currentYear % 100 != 0)
                    || currentYear % 400 == 0) {
                if (endD > 29) {
                    endD = 29;
                }
                wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
//                maxItem = endD;
            } else {
                if (endD > 28) {
                    endD = 28;
                }
                wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
//                maxItem = endD;
            }
        }

        if (currentItem > wv_day.getAdapter().getItemsCount() - 1) {
            currentItem = wv_day.getAdapter().getItemsCount() - 1;
            wv_day.setCurrentItem(currentItem);
        }
        if(currentYear == startYear && currentMonth == startMonth) {
            currentDay = currentItem + startD;
        } else {
            currentDay = currentItem + 1;
        }
    }


    private void setContentTextSize() {
        wv_day.setTextSize(textSize);
        wv_month.setTextSize(textSize);
        wv_year.setTextSize(textSize);
        wv_hours.setTextSize(textSize);
        wv_minutes.setTextSize(textSize);
        wv_seconds.setTextSize(textSize);
    }

    private void setTextColorOut() {
        wv_day.setTextColorOut(textColorOut);
        wv_month.setTextColorOut(textColorOut);
        wv_year.setTextColorOut(textColorOut);
        wv_hours.setTextColorOut(textColorOut);
        wv_minutes.setTextColorOut(textColorOut);
        wv_seconds.setTextColorOut(textColorOut);
    }

    private void setTextColorCenter() {
        wv_day.setTextColorCenter(textColorCenter);
        wv_month.setTextColorCenter(textColorCenter);
        wv_year.setTextColorCenter(textColorCenter);
        wv_hours.setTextColorCenter(textColorCenter);
        wv_minutes.setTextColorCenter(textColorCenter);
        wv_seconds.setTextColorCenter(textColorCenter);
    }

    private void setDividerColor() {
        wv_day.setDividerColor(dividerColor);
        wv_month.setDividerColor(dividerColor);
        wv_year.setDividerColor(dividerColor);
        wv_hours.setDividerColor(dividerColor);
        wv_minutes.setDividerColor(dividerColor);
        wv_seconds.setDividerColor(dividerColor);
    }

    private void setDividerType() {

        wv_day.setDividerType(dividerType);
        wv_month.setDividerType(dividerType);
        wv_year.setDividerType(dividerType);
        wv_hours.setDividerType(dividerType);
        wv_minutes.setDividerType(dividerType);
        wv_seconds.setDividerType(dividerType);

    }

    private void setLineSpacingMultiplier() {
        wv_day.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_month.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_year.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_hours.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_minutes.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_seconds.setLineSpacingMultiplier(lineSpacingMultiplier);
    }

    public void setLabels(String label_year, String label_month, String label_day, String label_hours, String label_mins, String label_seconds) {
        if (isLunarCalendar) {
            return;
        }

        if (label_year != null) {
            wv_year.setLabel(label_year);
        } else {
            wv_year.setLabel(view.getContext().getString(R.string.pickerview_year));
        }
        if (label_month != null) {
            wv_month.setLabel(label_month);
        } else {
            wv_month.setLabel(view.getContext().getString(R.string.pickerview_month));
        }
        if (label_day != null) {
            wv_day.setLabel(label_day);
        } else {
            wv_day.setLabel(view.getContext().getString(R.string.pickerview_day));
        }
        if (label_hours != null) {
            wv_hours.setLabel(label_hours);
        } else {
            wv_hours.setLabel(view.getContext().getString(R.string.pickerview_hours));
        }
        if (label_mins != null) {
            wv_minutes.setLabel(label_mins);
        } else {
            wv_minutes.setLabel(view.getContext().getString(R.string.pickerview_minutes));
        }
        if (label_seconds != null) {
            wv_seconds.setLabel(label_seconds);
        } else {
            wv_seconds.setLabel(view.getContext().getString(R.string.pickerview_seconds));
        }

    }

    public void setTextXOffset(int x_offset_year, int x_offset_month, int x_offset_day,
                               int x_offset_hours, int x_offset_minutes, int x_offset_seconds) {
        wv_year.setTextXOffset(x_offset_year);
        wv_month.setTextXOffset(x_offset_month);
        wv_day.setTextXOffset(x_offset_day);
        wv_hours.setTextXOffset(x_offset_hours);
        wv_minutes.setTextXOffset(x_offset_minutes);
        wv_seconds.setTextXOffset(x_offset_seconds);
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        wv_year.setCyclic(cyclic);
        wv_month.setCyclic(cyclic);
        wv_day.setCyclic(cyclic);
        wv_hours.setCyclic(cyclic);
        wv_minutes.setCyclic(cyclic);
        wv_seconds.setCyclic(cyclic);
    }

    public String getTime() {
        if (isLunarCalendar) {
            //如果是农历 返回对应的公历时间
            return getLunarTime();
        }
        StringBuilder sb = new StringBuilder();
//        System.out.println("i:" + currentYear + "-" + currentMonth + "-" + currentDay + " " + currentHour + ":" + wv_minutes.getCurrentItem() + ":" + wv_seconds.getCurrentItem());
        if (currentYear == startYear && currentMonth == startMonth && currentDay == startDay && startHour == currentHour) {
            sb.append(currentYear).append("-")
                    .append(currentMonth).append("-")
                    .append(currentDay).append(" ")
                    .append(currentHour).append(":")
                    .append((wv_minutes.getCurrentItem() + startMinute)).append(":")
                    .append(wv_seconds.getCurrentItem());


        } else {
            sb.append(currentYear).append("-")
                    .append(currentMonth).append("-")
                    .append(currentDay).append(" ")
                    .append(currentHour).append(":")
                    .append(wv_minutes.getCurrentItem()).append(":")
                    .append(wv_seconds.getCurrentItem());
        }

        return sb.toString();
    }


    /**
     * 农历返回对应的公历时间
     *
     * @return
     */
    private String getLunarTime() {
        StringBuilder sb = new StringBuilder();
        int year = wv_year.getCurrentItem() + startYear;
        int month = 1;
        boolean isLeapMonth = false;
        if (ChinaDate.leapMonth(year) == 0) {
            month = wv_month.getCurrentItem() + 1;
        } else {
            if ((wv_month.getCurrentItem() + 1) - ChinaDate.leapMonth(year) <= 0) {
                month = wv_month.getCurrentItem() + 1;
            } else if ((wv_month.getCurrentItem() + 1) - ChinaDate.leapMonth(year) == 1) {
                month = wv_month.getCurrentItem();
                isLeapMonth = true;
            } else {
                month = wv_month.getCurrentItem();
            }
        }
        int day = wv_day.getCurrentItem() + 1;
        int[] solar = LunarCalendar.lunarToSolar(year, month, day, isLeapMonth);

        sb.append(solar[0]).append("-")
                .append(solar[1]).append("-")
                .append(solar[2]).append(" ")
                .append(wv_hours.getCurrentItem()).append(":")
                .append(wv_minutes.getCurrentItem()).append(":")
                .append(wv_seconds.getCurrentItem());
        return sb.toString();
    }

    public View getView() {
        return view;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }


    public void setRangDate(Calendar startDate, Calendar endDate) {

        if (startDate == null && endDate != null) {
            int year = endDate.get(Calendar.YEAR);
            int month = endDate.get(Calendar.MONTH) + 1;
            int day = endDate.get(Calendar.DAY_OF_MONTH);
            int hour = endDate.get(Calendar.HOUR_OF_DAY);
            int minute = endDate.get(Calendar.MINUTE);
            if (year > startYear) {
                this.endYear = year;
                this.endMonth = month;
                this.endDay = day;
                this.endHour = hour;
                this.endMinute = minute;
            } else if (year == startYear) {
                if (month > startMonth) {
                    this.endYear = year;
                    this.endMonth = month;
                    this.endDay = day;
                    this.endHour = hour;
                    this.endMinute = minute;
                } else if (month == startMonth) {
                    if (day > startDay) {
                        this.endYear = year;
                        this.endMonth = month;
                        this.endDay = day;
                        this.endHour = hour;
                        this.endMinute = minute;
                    } else if(day == startDay) {
                        if(hour > startHour) {
                            this.endYear = year;
                            this.endMonth = month;
                            this.endDay = day;
                            this.endHour = hour;
                            this.endMinute = minute;
                        }
                    }
                }
            }

        } else if (startDate != null && endDate == null) {
            int year = startDate.get(Calendar.YEAR);
            int month = startDate.get(Calendar.MONTH) + 1;
            int day = startDate.get(Calendar.DAY_OF_MONTH);
            int hour = startDate.get(Calendar.HOUR_OF_DAY);
            int minute = startDate.get(Calendar.MINUTE);
            if (year < endYear) {
                this.startMonth = month;
                this.startDay = day;
                this.startYear = year;
                this.startHour = hour;
                this.startMinute = minute;
            } else if (year == endYear) {
                if (month < endMonth) {
                    this.startMonth = month;
                    this.startDay = day;
                    this.startYear = year;
                    this.startHour = hour;
                    this.startMinute = minute;
                } else if (month == endMonth) {
                    if (day < endDay) {
                        this.startMonth = month;
                        this.startDay = day;
                        this.startYear = year;
                        this.startHour = hour;
                        this.startMinute = minute;
                    } else if(day == endDay) {
                        if(hour < endHour) {
                            this.startMonth = month;
                            this.startDay = day;
                            this.startYear = year;
                            this.startHour = hour;
                            this.startMinute = minute;
                        }
                    }
                }
            }

        } else if (startDate != null && endDate != null) {
            this.startYear = startDate.get(Calendar.YEAR);
            this.endYear = endDate.get(Calendar.YEAR);
            this.startMonth = startDate.get(Calendar.MONTH) + 1;
            this.endMonth = endDate.get(Calendar.MONTH) + 1;
            this.startDay = startDate.get(Calendar.DAY_OF_MONTH);
            this.endDay = endDate.get(Calendar.DAY_OF_MONTH);
            this.startHour = startDate.get(Calendar.HOUR_OF_DAY);
            this.endHour = endDate.get(Calendar.HOUR_OF_DAY);
            this.startMinute = startDate.get(Calendar.MINUTE);
            this.endMinute = endDate.get(Calendar.MINUTE);
        }

    }

    /**
     * 设置间距倍数,但是只能在1.0-4.0f之间
     *
     * @param lineSpacingMultiplier
     */
    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        setLineSpacingMultiplier();
    }

    /**
     * 设置分割线的颜色
     *
     * @param dividerColor
     */
    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        setDividerColor();
    }

    /**
     * 设置分割线的类型
     *
     * @param dividerType
     */
    public void setDividerType(WheelView.DividerType dividerType) {
        this.dividerType = dividerType;
        setDividerType();
    }

    /**
     * 设置分割线之间的文字的颜色
     *
     * @param textColorCenter
     */
    public void setTextColorCenter(int textColorCenter) {
        this.textColorCenter = textColorCenter;
        setTextColorCenter();
    }

    /**
     * 设置分割线以外文字的颜色
     *
     * @param textColorOut
     */
    public void setTextColorOut(int textColorOut) {
        this.textColorOut = textColorOut;
        setTextColorOut();
    }

    /**
     * @param isCenterLabel 是否只显示中间选中项的
     */
    public void isCenterLabel(boolean isCenterLabel) {
        wv_day.isCenterLabel(isCenterLabel);
        wv_month.isCenterLabel(isCenterLabel);
        wv_year.isCenterLabel(isCenterLabel);
        wv_hours.isCenterLabel(isCenterLabel);
        wv_minutes.isCenterLabel(isCenterLabel);
        wv_seconds.isCenterLabel(isCenterLabel);
    }

    public void setSelectChangeCallback(ISelectTimeCallback mSelectChangeCallback) {
        this.mSelectChangeCallback = mSelectChangeCallback;
    }
}
