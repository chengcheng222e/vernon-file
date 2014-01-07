package com.vernon.file.client;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/7/14
 * Time: 10:20
 * To change this template use File | Settings | File Templates.
 */
public class HttpParams {

    public final static String CONTENT_MD5 = "Content-MD5";
    public final static String MKDIR = "mkdir";
    public final static String X_DZQ_UID = "x_dzq_uid";
    public final static String X_DZQ_LID = "x_dzq_lid";
    public final static String X_DZQ_REQUEST_ID = "x-dzq-request-id";
    public final static String X_GMKERL_CROP = "x-gmkerl-crop";
    public final static String X_GMKERL_CROP_PAGE_SIZE = "x-gmkerl-crop-page-size";

    public final static String X_DZQ_OBJTYPE = "x-dzq-objtype";
    public final static String X_DZQ_OBJID = "x-dzq-objid";

    // 如果在原缩略图版本或裁剪图设置的基础上进行调整,也可使用以下参数
    public final static String X_GMKERL_TYPE = "x-gmkerl-type";
    // 缩略图宽度/像素
    public final static String X_GMKERL_VALUE = "x-gmkerl-value";

    // 缩略图类型(单位为像素值)
    // fix_max : 限定最长边,短边自适应, 例: 150
    // fix_min : 限定最短边,长边自适应, 例: 150
    // fix_width_or_height : 限定宽度和高度, 例: 150x130
    // fix_width : 限定宽度,高度自适应, 例: 150
    // fix_height : 限定高度,宽度自适应, 例: 150
    // fix_both : 固定宽度和高度, 例: 150x130
    // fix_scale : 等比例缩放（1-99）, 例: 50
    public final static String REGEX_GMKERL_TYPE_FIX_ONE = "^[1-9]\\d{1,3}$"; // 例: 150
    public final static String REGEX_GMKERL_TYPE_FIX_BOTH = "^[1-9]\\d{1,3}x[1-9]\\d{1,3}$";  // 固定宽度和高度, 例: 150x130
    public final static String REGEX_GMKERL_TYPE_FIX_SCALE = "^[1-9][0-9]$";  // 等比例缩放（1-99）, 例: 50

    public enum GmkerlType {
        fix_max(REGEX_GMKERL_TYPE_FIX_ONE), // 限定最长边,短边自适应, 例: 150
        fix_min(REGEX_GMKERL_TYPE_FIX_ONE), // 限定最短边,长边自适应, 例: 150
        fix_width_or_height(REGEX_GMKERL_TYPE_FIX_BOTH), // 限定宽度和高度, 例: 150x130
        fix_width(REGEX_GMKERL_TYPE_FIX_ONE), // 限定宽度,高度自适应, 例: 150
        fix_height(REGEX_GMKERL_TYPE_FIX_ONE), // 限定高度,宽度自适应, 例: 150
        fix_both(REGEX_GMKERL_TYPE_FIX_BOTH), // 固定宽度和高度, 例: 150x130
        fix_scale(REGEX_GMKERL_TYPE_FIX_SCALE); // 等比例缩放（1-99）, 例: 50

        private String regex = "";

        public String getRegex() {
            return regex;
        }

        private GmkerlType(String regex) {
            this.regex = regex;
        }

    }
}
