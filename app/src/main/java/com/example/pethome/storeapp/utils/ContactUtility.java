
package com.example.pethome.storeapp.utils;

import android.support.v4.util.PatternsCompat;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;

public final class ContactUtility {

    private static final int PHONE_NUMBER_MIN_LENGTH = 4;
    private static final int PHONE_NUMBER_MAX_LENGTH = 15;

    private static final int EMAIL_MAX_LENGTH = 254;

    private static final String EMAIL_LOCAL_DOMAIN_SEPARATOR = "@";
    private ContactUtility() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }

        if (email.length() > EMAIL_MAX_LENGTH) {
            return false;
        }

        if (!email.contains(EMAIL_LOCAL_DOMAIN_SEPARATOR)) {
            return false;
        }

        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean isValidPhoneNumber(String phoneNumber) {
        phoneNumber = convertAndStripPhoneNumber(phoneNumber);

        if (TextUtils.isEmpty(phoneNumber)
                || phoneNumber.length() < PHONE_NUMBER_MIN_LENGTH
                || phoneNumber.length() > PHONE_NUMBER_MAX_LENGTH) {
            return false;
        }

        return Patterns.PHONE.matcher(phoneNumber).matches() & PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
    }
    public static String convertAndStripPhoneNumber(String phoneNumber) {
        return PhoneNumberUtils.stripSeparators(PhoneNumberUtils.convertKeypadLettersToDigits(phoneNumber));
    }
    public static int getPhoneNumberMaxLength() {
        return PHONE_NUMBER_MAX_LENGTH;
    }
    public static int getPhoneNumberMinLength() {
        return PHONE_NUMBER_MIN_LENGTH;
    }
    public static int getEmailMaxLength() {
        return EMAIL_MAX_LENGTH;
    }
}
