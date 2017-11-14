package org.dhis2.mobile.utils.date;


import org.dhis2.mobile.ui.models.Filter;
import org.dhis2.mobile.utils.date.filters.BiMonthlyPeriodFilter;
import org.dhis2.mobile.utils.date.filters.FinAprilYearPeriodFilter;
import org.dhis2.mobile.utils.date.filters.FinJulyYearPeriodFilter;
import org.dhis2.mobile.utils.date.filters.FinOctYearPeriodFilter;
import org.dhis2.mobile.utils.date.filters.MonthlyPeriodFilter;
import org.dhis2.mobile.utils.date.filters.QuarterlyPeriodFilter;
import org.dhis2.mobile.utils.date.filters.SixmonthlyPeriodFilter;
import org.dhis2.mobile.utils.date.filters.WeeklyPeriodFilter;
import org.dhis2.mobile.utils.date.filters.YearlyPeriodFilter;
import org.joda.time.DateTime;

public class PeriodFilterFactory {
    public static final String YEARLY = "Yearly";
    public static final String FINANCIAL_APRIL = "FinancialApril";
    public static final String FINANCIAL_JULY = "FinancialJuly";
    public static final String FINANCIAL_OCT = "FinancialOct";
    public static final String SIX_MONTHLY = "SixMonthly";
    public static final String QUARTERLY = "Quarterly";
    public static final String BIMONTHLY = "BiMonthly";
    public static final String MONTHLY = "Monthly";
    public static final String WEEKLY = "Weekly";
    public static final String DAILY = "Daily";

    private PeriodFilterFactory(){

    }
    public static Filter getPeriodFilter(DateTime startDate, DateTime endDate, String periodType){
        if(periodType.equals(YEARLY)){
            return new YearlyPeriodFilter(startDate, endDate);
        }else if(periodType.equals(WEEKLY)){
            return new WeeklyPeriodFilter(startDate, endDate);
        } else if (periodType.equals(MONTHLY)){
            return new MonthlyPeriodFilter(startDate, endDate);
        } else if (periodType.equals(BIMONTHLY)){
            return new BiMonthlyPeriodFilter(startDate, endDate);
        } else if (periodType.equals(QUARTERLY)){
            return new QuarterlyPeriodFilter(startDate, endDate);
        } else if (periodType.equals(SIX_MONTHLY)) {
            return new SixmonthlyPeriodFilter(startDate, endDate);
        } else if (periodType.equals(FINANCIAL_JULY)){
            return new FinJulyYearPeriodFilter(startDate, endDate);
        } else if (periodType.equals(FINANCIAL_APRIL)){
            return new FinAprilYearPeriodFilter(startDate, endDate);
        } else if (periodType.equals(FINANCIAL_OCT)){
            return new FinOctYearPeriodFilter(startDate, endDate);
        } else {
            return new PeriodFilter(startDate, endDate);
        }
    }
}
