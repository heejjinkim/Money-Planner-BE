package com.umc5th.muffler.global.util;

import java.time.LocalDate;
import java.util.Date;

public interface DateTimeProvider {
    LocalDate nowDate();
    Date getIssuedDate();
    Date getDateAfterDays(int duration);
    Date getDateAfterMinutes(int minutes);
}
