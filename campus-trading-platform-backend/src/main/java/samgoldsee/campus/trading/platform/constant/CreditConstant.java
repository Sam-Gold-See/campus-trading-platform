package samgoldsee.campus.trading.platform.constant;

/**
 * 信用分相关常量
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
public class CreditConstant {

    /**
     * 新用户基础信用分
     */
    public static final int BASE_SCORE = 100;

    /**
     * 最低信用分阈值（低于此值将被限制）
     */
    public static final int MIN_THRESHOLD = 60;

    /**
     * 好评加分
     */
    public static final int GOOD_REVIEW_SCORE = 2;

    /**
     * 差评扣分
     */
    public static final int BAD_REVIEW_SCORE = -2;

    /**
     * 交易成交加分
     */
    public static final int DEAL_SCORE = 1;

    /**
     * 评价类型：好评
     */
    public static final int RATING_GOOD = 1;

    /**
     * 评价类型：中评
     */
    public static final int RATING_NEUTRAL = 0;

    /**
     * 评价类型：差评
     */
    public static final int RATING_BAD = -1;
}