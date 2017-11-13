package org.onosproject.yang.runtime.impl;

public final class UtilsConstants {
    static final String AT = "@";
    static final String E_NEXIST = "node with {} namespace not found.";
    static final String E_MEXIST =
            "Model with given modelId already exist";
    static final String E_NULL = "Model must not be null";
    static final String E_NOT_VAL = "Model id is invalid";
    static final String QNAME_PRE = "org.onosproject.yang.gen";
    static final String REV_REGEX =
            "rev([12]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))";
    static final String DOT_REGEX = "\\.";
    private static final String FMT_NOT_EXIST =
            "Schema node with name %s doesn't exist.";

    // No instantiation.
    private UtilsConstants() {
    }

    /**
     * Returns the error string by filling the parameters in the given
     * formatted error string.
     *
     * @param fmt    error format string
     * @param params parameters to be filled in formatted string
     * @return error string
     */
    public static String errorMsg(String fmt, Object... params) {
        return String.format(fmt, params);
    }
}
